package com.thisara.mypocket.ui

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.thisara.mypocket.data.ActivityItem
import com.thisara.mypocket.data.AppSettings
import com.thisara.mypocket.data.FirebaseRepository
import com.thisara.mypocket.data.MonthBoard
import com.thisara.mypocket.data.Pocket
import com.thisara.mypocket.data.SavingsBoardGenerator
import com.thisara.mypocket.data.SavingsCell
import com.thisara.mypocket.data.SettingsStore
import com.thisara.mypocket.data.UserSession
import com.thisara.mypocket.reminders.ReminderScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AuthMode {
    Landing,
    SignIn,
    SignUp,
}

enum class HomeTab {
    Board,
    History,
    Settings,
}

data class MainUiState(
    val loading: Boolean = true,
    val authMode: AuthMode = AuthMode.Landing,
    val selectedTab: HomeTab = HomeTab.Board,
    val user: UserSession? = null,
    val pocket: Pocket? = null,
    val board: MonthBoard? = null,
    val message: String? = null,
) {
    val isSignedIn: Boolean = user != null
    val needsEmailVerification: Boolean = user != null && !user.isEmailVerified
    val needsPocket: Boolean = user != null && user.isEmailVerified && pocket == null
    val activity: List<ActivityItem> = board?.cells
        .orEmpty()
        .filter { it.saved && it.savedAtMillis != null }
        .sortedByDescending { it.savedAtMillis }
        .map {
            ActivityItem(
                amount = it.amount,
                savedByName = it.savedByName ?: "Member",
                savedAtMillis = it.savedAtMillis ?: 0L,
            )
        }
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = FirebaseRepository(application.applicationContext)
    private val settingsStore = SettingsStore(application.applicationContext)
    private val reminderScheduler = ReminderScheduler(application.applicationContext)

    private val mutableState = MutableStateFlow(MainUiState())
    val settings: StateFlow<AppSettings> = settingsStore.settings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AppSettings(),
    )

    val uiState: StateFlow<MainUiState> = combine(mutableState, settings) { state, _ -> state }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), mutableState.value)

    private var authListener: FirebaseAuth.AuthStateListener? = null
    private var userRegistration: ListenerRegistration? = null
    private var pocketRegistration: ListenerRegistration? = null
    private var boardRegistration: ListenerRegistration? = null
    private var activePocketId: String? = null

    init {
        reminderScheduler.createNotificationChannel()
        authListener = repository.addAuthStateListener { session ->
            onSessionChanged(session)
        }
        onSessionChanged(repository.currentSession())

        viewModelScope.launch {
            settingsStore.settings.collect { appSettings ->
                cacheBootSettings(appSettings)
                if (appSettings.remindersEnabled) {
                    reminderScheduler.scheduleDaily(appSettings.reminderHour)
                } else {
                    reminderScheduler.cancel()
                }
            }
        }
    }

    fun setAuthMode(mode: AuthMode) {
        mutableState.update { it.copy(authMode = mode, message = null) }
    }

    fun setTab(tab: HomeTab) {
        mutableState.update { it.copy(selectedTab = tab) }
    }

    fun clearMessage() {
        mutableState.update { it.copy(message = null) }
    }

    fun signUp(name: String, email: String, password: String, confirmPassword: String) {
        val validation = validateSignUp(name, email, password, confirmPassword)
        if (validation != null) {
            mutableState.update { it.copy(message = validation) }
            return
        }

        runLoading {
            repository.signUp(name, email, password)
            mutableState.update {
                it.copy(message = "Verification email sent. Check your inbox before logging in.")
            }
        }
    }

    fun signIn(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            mutableState.update { it.copy(message = "Enter your email and password.") }
            return
        }

        runLoading {
            repository.signIn(email, password)
        }
    }

    fun googleSignInIntent(): Intent? {
        return try {
            repository.googleSignInIntent()
        } catch (error: Throwable) {
            mutableState.update { it.copy(message = error.message ?: "Google sign-in is not ready yet.") }
            null
        }
    }

    fun completeGoogleSignIn(data: Intent?) {
        runLoading {
            repository.signInWithGoogle(data)
        }
    }

    fun sendVerificationEmail() {
        runLoading {
            repository.sendVerificationEmail()
            mutableState.update { it.copy(message = "Verification email sent again.") }
        }
    }

    fun refreshVerification() {
        refreshVerification(showLoading = true)
    }

    fun refreshVerificationSilently() {
        refreshVerification(showLoading = false)
    }

    fun createPocket(name: String) {
        if (name.isBlank()) {
            mutableState.update { it.copy(message = "Give your shared pocket a name.") }
            return
        }

        runLoading {
            repository.createPocket(name)
        }
    }

    fun joinPocket(inviteCode: String) {
        if (inviteCode.isBlank()) {
            mutableState.update { it.copy(message = "Enter the invite code.") }
            return
        }

        runLoading {
            repository.joinPocket(inviteCode)
        }
    }

    fun toggleCell(cell: SavingsCell) {
        val pocket = mutableState.value.pocket ?: return
        val board = mutableState.value.board ?: return

        runLoading(showLoading = false) {
            val saved = repository.toggleCell(pocket.id, board.monthKey, cell)
            if (saved) reminderScheduler.recordSavedToday()
        }
    }

    fun repairCurrentBoard() {
        val pocketId = mutableState.value.pocket?.id ?: return
        runLoading {
            val monthKey = SavingsBoardGenerator.currentMonthKey()
            repository.ensureMonthBoard(pocketId, monthKey)
            mutableState.update {
                it.copy(message = "Board checked. It should appear in a moment.")
            }
        }
    }

    fun setRemindersEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsStore.setRemindersEnabled(enabled)
        }
    }

    fun setReminderHour(hour: Int) {
        viewModelScope.launch {
            settingsStore.setReminderHour(hour)
        }
    }

    fun signOut() {
        repository.signOut()
        stopFirestoreListeners()
        mutableState.value = MainUiState(loading = false)
    }

    override fun onCleared() {
        authListener?.let(repository::removeAuthStateListener)
        stopFirestoreListeners()
        super.onCleared()
    }

    private fun onSessionChanged(session: UserSession?) {
        if (session == null) {
            stopFirestoreListeners()
            activePocketId = null
            mutableState.value = MainUiState(loading = false)
            return
        }

        mutableState.update {
            it.copy(
                loading = false,
                user = session,
                pocket = if (session.isEmailVerified) it.pocket else null,
                board = if (session.isEmailVerified) it.board else null,
            )
        }

        if (session.isEmailVerified) {
            startUserPocketListener()
        } else {
            stopFirestoreListeners()
        }
    }

    private fun startUserPocketListener() {
        userRegistration?.remove()
        userRegistration = repository.listenUserPocketId(
            onPocketId = { pocketId ->
                if (pocketId == null) {
                    activePocketId = null
                    pocketRegistration?.remove()
                    boardRegistration?.remove()
                    mutableState.update { it.copy(pocket = null, board = null, loading = false) }
                    return@listenUserPocketId
                }

                if (activePocketId != pocketId) {
                    activePocketId = pocketId
                    startPocketListener(pocketId)
                }
            },
            onError = ::showError,
        )
    }

    private fun startPocketListener(pocketId: String) {
        pocketRegistration?.remove()
        boardRegistration?.remove()
        pocketRegistration = repository.listenPocket(
            pocketId = pocketId,
            onPocket = { pocket ->
                mutableState.update { it.copy(pocket = pocket, loading = false) }
            },
            onError = ::showError,
        )

        viewModelScope.launch {
            runCatching {
                val monthKey = SavingsBoardGenerator.currentMonthKey()
                repository.ensureMonthBoard(pocketId, monthKey)
                boardRegistration = repository.listenMonthBoard(
                    pocketId = pocketId,
                    monthKey = monthKey,
                    onBoard = { board ->
                        mutableState.update { it.copy(board = board, loading = false) }
                        if (board.cells.isEmpty()) {
                            viewModelScope.launch {
                                runCatching {
                                    repository.ensureMonthBoard(pocketId, monthKey)
                                }.onFailure(::showError)
                            }
                        }
                    },
                    onError = ::showError,
                )
            }.onFailure(::showError)
        }
    }

    private fun stopFirestoreListeners() {
        userRegistration?.remove()
        pocketRegistration?.remove()
        boardRegistration?.remove()
        userRegistration = null
        pocketRegistration = null
        boardRegistration = null
    }

    private fun runLoading(showLoading: Boolean = true, block: suspend () -> Unit) {
        viewModelScope.launch {
            if (showLoading) mutableState.update { it.copy(loading = true, message = null) }
            runCatching { block() }
                .onFailure(::showError)
            if (showLoading) mutableState.update { it.copy(loading = false) }
        }
    }

    private fun refreshVerification(showLoading: Boolean) {
        runLoading(showLoading = showLoading) {
            val session = repository.reloadUser()
            onSessionChanged(session)
            if (session?.isEmailVerified == true) {
                mutableState.update {
                    it.copy(message = "Email verified. Set up your shared pocket.")
                }
            }
        }
    }

    private fun showError(error: Throwable) {
        mutableState.update {
            it.copy(
                loading = false,
                message = error.userMessage(),
            )
        }
    }

    private fun Throwable.userMessage(): String {
        return if (
            this is FirebaseFirestoreException &&
            code == FirebaseFirestoreException.Code.PERMISSION_DENIED
        ) {
            "Firestore rules are not published yet. Open Firebase Console, create Firestore, then publish firestore.rules."
        } else {
            message ?: "Something went wrong. Try again."
        }
    }

    private fun validateSignUp(
        name: String,
        email: String,
        password: String,
        confirmPassword: String,
    ): String? {
        return when {
            name.isBlank() -> "Enter your name."
            "@" !in email -> "Enter a valid email address."
            password.length < 6 -> "Password must be at least 6 characters."
            password != confirmPassword -> "Passwords do not match."
            else -> null
        }
    }

    private fun cacheBootSettings(appSettings: AppSettings) {
        getApplication<Application>()
            .getSharedPreferences("settings_boot_cache", 0)
            .edit()
            .putBoolean("reminders_enabled", appSettings.remindersEnabled)
            .putInt("reminder_hour", appSettings.reminderHour)
            .apply()
    }
}
