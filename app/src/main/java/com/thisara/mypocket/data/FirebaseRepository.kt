package com.thisara.mypocket.data

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import java.util.Locale
import kotlin.random.Random

class FirebaseRepository(private val context: Context) {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun currentSession(): UserSession? = auth.currentUser?.toSession()

    fun addAuthStateListener(listener: (UserSession?) -> Unit): FirebaseAuth.AuthStateListener {
        val authListener = FirebaseAuth.AuthStateListener {
            listener(it.currentUser?.toSession())
        }
        auth.addAuthStateListener(authListener)
        return authListener
    }

    fun removeAuthStateListener(listener: FirebaseAuth.AuthStateListener) {
        auth.removeAuthStateListener(listener)
    }

    suspend fun signUp(name: String, email: String, password: String) {
        val result = auth.createUserWithEmailAndPassword(email.trim(), password).await()
        val user = requireNotNull(result.user)
        user.updateProfile(
            UserProfileChangeRequest.Builder()
                .setDisplayName(name.trim())
                .build(),
        ).await()
        user.sendEmailVerification().await()
        runCatching {
            upsertUserDocument(user, name.trim())
        }.onFailure { error ->
            if (!error.isPermissionDenied()) throw error
        }
    }

    suspend fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email.trim(), password).await()
    }

    suspend fun sendVerificationEmail() {
        auth.currentUser?.sendEmailVerification()?.await()
    }

    suspend fun reloadUser(): UserSession? {
        auth.currentUser?.reload()?.await()
        return auth.currentUser?.toSession()
    }

    fun googleSignInIntent(): Intent {
        val webClientId = googleWebClientId()
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(context, options).signInIntent
    }

    suspend fun signInWithGoogle(data: Intent?) {
        val account = GoogleSignIn.getSignedInAccountFromIntent(data).await()
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        val result = auth.signInWithCredential(credential).await()
        val user = requireNotNull(result.user)
        runCatching {
            upsertUserDocument(user, user.displayName ?: account.email?.substringBefore("@") ?: "Member")
        }.onFailure { error ->
            if (!error.isPermissionDenied()) throw error
        }
    }

    fun signOut() {
        auth.signOut()
    }

    fun listenUserPocketId(onPocketId: (String?) -> Unit, onError: (Throwable) -> Unit): ListenerRegistration? {
        val uid = auth.currentUser?.uid ?: return null
        return db.collection(USERS)
            .document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onError(error)
                    return@addSnapshotListener
                }
                onPocketId(snapshot?.getString("currentPocketId"))
            }
    }

    fun listenPocket(
        pocketId: String,
        onPocket: (Pocket?) -> Unit,
        onError: (Throwable) -> Unit,
    ): ListenerRegistration {
        return db.collection(POCKETS)
            .document(pocketId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onError(error)
                    return@addSnapshotListener
                }
                onPocket(snapshot?.toPocket())
            }
    }

    fun listenMonthBoard(
        pocketId: String,
        monthKey: String,
        onBoard: (MonthBoard) -> Unit,
        onError: (Throwable) -> Unit,
    ): ListenerRegistration {
        return db.collection(POCKETS)
            .document(pocketId)
            .collection(MONTHS)
            .document(monthKey)
            .collection(CELLS)
            .orderBy("index")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onError(error)
                    return@addSnapshotListener
                }
                val cells = snapshot?.documents.orEmpty().mapNotNull { it.toSavingsCell() }
                onBoard(MonthBoard(monthKey = monthKey, cells = cells))
            }
    }

    suspend fun createPocket(pocketName: String): String {
        val user = requireNotNull(auth.currentUser)
        val inviteCode = uniqueInviteCode()
        val pocketRef = db.collection(POCKETS).document()
        val userRef = db.collection(USERS).document(user.uid)
        val inviteRef = db.collection(INVITE_CODES).document(inviteCode)

        val batch = db.batch()
        batch.set(
            pocketRef,
            mapOf(
                "name" to pocketName.trim().ifBlank { "Our Pocket" },
                "inviteCode" to inviteCode,
                "memberIds" to listOf(user.uid),
                "createdBy" to user.uid,
                "createdAt" to FieldValue.serverTimestamp(),
            ),
        )
        batch.set(
            inviteRef,
            mapOf(
                "pocketId" to pocketRef.id,
                "pocketName" to pocketName.trim().ifBlank { "Our Pocket" },
                "createdBy" to user.uid,
                "createdAt" to FieldValue.serverTimestamp(),
            ),
        )
        batch.set(
            userRef,
            mapOf(
                "name" to (user.displayName ?: user.email?.substringBefore("@") ?: "Member"),
                "email" to user.email.orEmpty(),
                "currentPocketId" to pocketRef.id,
                "updatedAt" to FieldValue.serverTimestamp(),
            ),
            SetOptions.merge(),
        )
        batch.commit().await()
        ensureMonthBoard(pocketRef.id, SavingsBoardGenerator.currentMonthKey())
        return pocketRef.id
    }

    suspend fun joinPocket(inviteCode: String): String {
        val user = requireNotNull(auth.currentUser)
        val normalizedCode = inviteCode.trim().uppercase(Locale.US)
        val invite = db.collection(INVITE_CODES).document(normalizedCode).get().await()
        val pocketId = invite.getString("pocketId")
            ?: throw IllegalArgumentException("Invite code not found.")

        val batch = db.batch()
        batch.update(
            db.collection(POCKETS).document(pocketId),
            mapOf(
                "memberIds" to FieldValue.arrayUnion(user.uid),
                "updatedAt" to FieldValue.serverTimestamp(),
            ),
        )
        batch.set(
            db.collection(USERS).document(user.uid),
            mapOf(
                "name" to (user.displayName ?: user.email?.substringBefore("@") ?: "Member"),
                "email" to user.email.orEmpty(),
                "currentPocketId" to pocketId,
                "updatedAt" to FieldValue.serverTimestamp(),
            ),
            SetOptions.merge(),
        )
        batch.commit().await()
        ensureMonthBoard(pocketId, SavingsBoardGenerator.currentMonthKey())
        return pocketId
    }

    suspend fun ensureMonthBoard(pocketId: String, monthKey: String) {
        val monthRef = db.collection(POCKETS)
            .document(pocketId)
            .collection(MONTHS)
            .document(monthKey)

        val monthExists = monthRef.get().await().exists()
        val firstCellExists = monthRef.collection(CELLS)
            .document("00")
            .get()
            .await()
            .exists()

        if (monthExists && firstCellExists) return

        val amounts = SavingsBoardGenerator.generate(monthKey, pocketId)
        val batch = db.batch()
        batch.set(
            monthRef,
            mapOf(
                "monthKey" to monthKey,
                "cellCount" to amounts.size,
                "targetTotal" to amounts.sum(),
                "createdAt" to FieldValue.serverTimestamp(),
            ),
            SetOptions.merge(),
        )
        amounts.forEachIndexed { index, amount ->
            val cellId = index.toString().padStart(2, '0')
            batch.set(
                monthRef.collection(CELLS).document(cellId),
                mapOf(
                    "index" to index,
                    "amount" to amount,
                    "saved" to false,
                    "savedByUid" to null,
                    "savedByName" to null,
                    "savedAtMillis" to null,
                ),
            )
        }
        batch.commit().await()
    }

    suspend fun toggleCell(pocketId: String, monthKey: String, cell: SavingsCell): Boolean {
        val user = requireNotNull(auth.currentUser)
        val ref = db.collection(POCKETS)
            .document(pocketId)
            .collection(MONTHS)
            .document(monthKey)
            .collection(CELLS)
            .document(cell.id)

        val willSave = !cell.saved
        val update = if (willSave) {
            mapOf(
                "saved" to true,
                "savedByUid" to user.uid,
                "savedByName" to (user.displayName ?: user.email?.substringBefore("@") ?: "Member"),
                "savedAtMillis" to System.currentTimeMillis(),
            )
        } else {
            mapOf(
                "saved" to false,
                "savedByUid" to null,
                "savedByName" to null,
                "savedAtMillis" to null,
            )
        }
        ref.update(update).await()
        return willSave
    }

    private suspend fun upsertUserDocument(user: FirebaseUser, fallbackName: String) {
        db.collection(USERS)
            .document(user.uid)
            .set(
                mapOf(
                    "name" to (user.displayName ?: fallbackName),
                    "email" to user.email.orEmpty(),
                    "updatedAt" to FieldValue.serverTimestamp(),
                ),
                SetOptions.merge(),
            )
            .await()
    }

    private suspend fun uniqueInviteCode(): String {
        repeat(10) {
            val code = randomInviteCode()
            val exists = db.collection(INVITE_CODES).document(code).get().await().exists()
            if (!exists) return code
        }
        error("Could not create an invite code. Try again.")
    }

    private fun randomInviteCode(): String {
        val alphabet = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        return buildString {
            repeat(6) {
                append(alphabet[Random.nextInt(alphabet.length)])
            }
        }
    }

    private fun googleWebClientId(): String {
        val id = context.resources.getIdentifier(
            "default_web_client_id",
            "string",
            context.packageName,
        )
        if (id == 0) {
            throw IllegalStateException(
                "Google sign-in needs SHA fingerprints in Firebase, then a fresh google-services.json.",
            )
        }
        return context.getString(id)
    }

    private fun FirebaseUser.toSession(): UserSession {
        val isGoogleUser = providerData.any { it.providerId == "google.com" }
        return UserSession(
            uid = uid,
            name = displayName ?: email?.substringBefore("@") ?: "Member",
            email = email.orEmpty(),
            isEmailVerified = isEmailVerified || isGoogleUser,
            isGoogleUser = isGoogleUser,
        )
    }

    private fun DocumentSnapshot.toPocket(): Pocket? {
        if (!exists()) return null
        return Pocket(
            id = id,
            name = getString("name") ?: "Our Pocket",
            inviteCode = getString("inviteCode") ?: "",
            memberIds = get("memberIds") as? List<String> ?: emptyList(),
        )
    }

    private fun DocumentSnapshot.toSavingsCell(): SavingsCell? {
        if (!exists()) return null
        return SavingsCell(
            id = id,
            index = getLong("index")?.toInt() ?: return null,
            amount = getLong("amount")?.toInt() ?: return null,
            saved = getBoolean("saved") ?: false,
            savedByUid = getString("savedByUid"),
            savedByName = getString("savedByName"),
            savedAtMillis = getLong("savedAtMillis"),
        )
    }

    private fun Throwable.isPermissionDenied(): Boolean {
        return this is FirebaseFirestoreException &&
            code == FirebaseFirestoreException.Code.PERMISSION_DENIED
    }

    companion object {
        private const val USERS = "users"
        private const val POCKETS = "pockets"
        private const val MONTHS = "months"
        private const val CELLS = "cells"
        private const val INVITE_CODES = "inviteCodes"
    }
}
