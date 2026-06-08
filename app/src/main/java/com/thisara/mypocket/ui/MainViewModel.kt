package com.thisara.mypocket.ui

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.thisara.mypocket.data.ActivityItem
import com.thisara.mypocket.data.AppSettings
import com.thisara.mypocket.data.FirebaseRepository
import com.thisara.mypocket.data.MAX_POCKET_TARGET_AMOUNT
import com.thisara.mypocket.data.MonthBoard
import com.thisara.mypocket.data.MonthSummary
import com.thisara.mypocket.data.Pocket
import com.thisara.mypocket.data.SavingsBoardGenerator
import com.thisara.mypocket.data.SavingsCell
import com.thisara.mypocket.data.SettingsStore
import com.thisara.mypocket.data.TargetScope
import com.thisara.mypocket.data.ThemeMode
import com.thisara.mypocket.data.UserSession
import com.thisara.mypocket.data.canToggle
import com.thisara.mypocket.reminders.ReminderScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout

enum class AuthMode {
    Landing,
    SignIn,
    SignUp,
}

enum class HomeTab {
    Board,
    History,
    Settings,
    Profile,
}

data class MainUiState(
    val loading: Boolean = true,
    val authMode: AuthMode = AuthMode.Landing,
    val selectedTab: HomeTab = HomeTab.Board,
    val user: UserSession? = null,
    val pocket: Pocket? = null,
    val pockets: List<Pocket> = emptyList(),
    val board: MonthBoard? = null,
    val yearSummaries: List<MonthSummary> = emptyList(),
    val allMonthSummaries: List<MonthSummary> = emptyList(),
    val selectedSummaryYear: Int = SavingsBoardGenerator.currentYear(),
    val selectedSummaryMonthKey: String? = null,
    val selectedMonthSavedCells: List<SavingsCell> = emptyList(),
    val selectedMonthLoading: Boolean = false,
    val showPocketPicker: Boolean = false,
    val message: String? = null,
) {
    val isSignedIn: Boolean = user != null
    val needsEmailVerification: Boolean = user != null && !user.isEmailVerified
    val needsPocket: Boolean = user != null && user.isEmailVerified && (pockets.isEmpty() || pocket == null)
    val todayKey: String = SavingsBoardGenerator.todayKey()
    val currentMonthSummary: MonthSummary?
        get() {
            val monthKey = board?.monthKey ?: SavingsBoardGenerator.currentMonthKey()
            val boardSnapshot = board
            return if (boardSnapshot != null) {
                MonthSummary(
                    monthKey = monthKey,
                    targetTotal = boardSnapshot.targetTotal,
                    savedTotal = boardSnapshot.savedTotal,
                    savedCount = boardSnapshot.savedCount,
                    cellCount = boardSnapshot.cells.size,
                )
            } else {
                yearSummaries.firstOrNull { it.monthKey == monthKey }
            }
        }
    val yearlySavedTotal: Int = yearSummaries.sumOf { it.savedTotal }
    val yearlyTargetTotal: Int = yearSummaries.sumOf { it.targetTotal }
    val yearlyMissedTotal: Int = (yearlyTargetTotal - yearlySavedTotal).coerceAtLeast(0)
    val lifetimeSavedTotal: Int
        get() {
            val boardSnapshot = board
            val currentMonthKey = boardSnapshot?.monthKey
            return allMonthSummaries
                .filterNot { it.monthKey == currentMonthKey }
                .sumOf { it.savedTotal } + (boardSnapshot?.savedTotal ?: 0)
        }
    val selectedMonthSummary: MonthSummary?
        get() = selectedSummaryMonthKey?.let(::summaryForMonth)
    val activity: List<ActivityItem> = board?.cells
        .orEmpty()
        .filter { it.saved && it.savedAtMillis != null }
        .sortedByDescending { it.savedAtMillis }
        .map {
            ActivityItem(
                amount = it.amount,
                savedByName = it.savedByName ?: "You",
                savedAtMillis = it.savedAtMillis ?: 0L,
            )
        }

    fun summaryForMonth(monthKey: String): MonthSummary? {
        val boardSnapshot = board
        return if (boardSnapshot != null && boardSnapshot.monthKey == monthKey) {
            MonthSummary(
                monthKey = monthKey,
                targetTotal = boardSnapshot.targetTotal,
                savedTotal = boardSnapshot.savedTotal,
                savedCount = boardSnapshot.savedCount,
                cellCount = boardSnapshot.cells.size,
            )
        } else {
            yearSummaries.firstOrNull { it.monthKey == monthKey }
        }
    }

    val canHandleSystemBack: Boolean
        get() = when {
            !isSignedIn -> authMode != AuthMode.Landing
            showPocketPicker && !needsPocket -> true
            selectedSummaryMonthKey != null -> true
            selectedTab != HomeTab.Board -> true
            else -> false
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
    private var pocketsRegistration: ListenerRegistration? = null
    private var pocketRegistration: ListenerRegistration? = null
    private var boardRegistration: ListenerRegistration? = null
    private var summariesRegistration: ListenerRegistration? = null
    private var allSummariesRegistration: ListenerRegistration? = null
    private var selectedMonthRegistration: ListenerRegistration? = null
    private var activePocketId: String? = null
    private val attemptedAutoBoardRepairs = mutableSetOf<String>()

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
                    reminderScheduler.scheduleDaily(
                        hour = appSettings.reminderHour,
                        minute = appSettings.reminderMinute,
                    )
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

    fun showPocketPicker() {
        mutableState.update { it.copy(showPocketPicker = true, selectedTab = HomeTab.Board) }
    }

    fun handleSystemBack() {
        val state = mutableState.value
        when {
            !state.isSignedIn && state.authMode != AuthMode.Landing -> {
                mutableState.update { it.copy(authMode = AuthMode.Landing, message = null) }
            }

            state.showPocketPicker && !state.needsPocket -> {
                mutableState.update { it.copy(showPocketPicker = false) }
            }

            state.selectedSummaryMonthKey != null -> {
                closeSummaryMonth()
            }

            state.selectedTab != HomeTab.Board -> {
                mutableState.update { it.copy(selectedTab = HomeTab.Board) }
            }
        }
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

    fun googleAccountDeleteIntent(): Intent? {
        return try {
            repository.googleSignInIntent()
        } catch (error: Throwable) {
            mutableState.update { it.copy(message = error.message ?: "Google reauthentication is not ready yet.") }
            null
        }
    }

    fun completeGoogleAccountDeletion(data: Intent?) {
        runLoading {
            repository.reauthenticateWithGoogle(data)
            repository.deleteAccountAfterRecentLogin()
            stopFirestoreListeners()
            mutableState.value = MainUiState(loading = false)
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

    fun createPocket(
        name: String,
        purpose: String,
        targetAmountText: String,
        targetScope: TargetScope,
    ) {
        val cleanName = name.cleanPocketName()
        val cleanPurpose = purpose.cleanPocketName()
        val parsedTarget = parsePocketTargetAmount(targetAmountText) ?: return
        val targetAmount = parsedTarget.amount
        if (cleanName.isBlank()) {
            mutableState.update { it.copy(message = "Give your pocket a name.") }
            return
        }
        if (cleanName.length > MAX_POCKET_NAME_LENGTH) {
            mutableState.update { it.copy(message = "Pocket name must be $MAX_POCKET_NAME_LENGTH characters or less.") }
            return
        }
        if (hasPocketName(cleanName)) {
            mutableState.update { it.copy(message = "You already have a pocket with that name.") }
            return
        }
        if (cleanPurpose.isBlank()) {
            mutableState.update { it.copy(message = "Tell the purpose of this pocket.") }
            return
        }
        if (cleanPurpose.length > MAX_POCKET_PURPOSE_LENGTH) {
            mutableState.update { it.copy(message = "Pocket purpose must be $MAX_POCKET_PURPOSE_LENGTH characters or less.") }
            return
        }

        runLoading {
            repository.createPocket(cleanName, cleanPurpose, targetAmount, targetScope)
            mutableState.update { it.copy(showPocketPicker = false) }
        }
    }

    fun updatePocket(
        pocketId: String,
        name: String,
        purpose: String,
        targetAmountText: String,
        targetScope: TargetScope,
    ) {
        val cleanName = name.cleanPocketName()
        val cleanPurpose = purpose.cleanPocketName()
        val parsedTarget = parsePocketTargetAmount(targetAmountText) ?: return
        val targetAmount = parsedTarget.amount
        if (cleanName.isBlank()) {
            mutableState.update { it.copy(message = "Pocket name cannot be blank.") }
            return
        }
        if (cleanName.length > MAX_POCKET_NAME_LENGTH) {
            mutableState.update { it.copy(message = "Pocket name must be $MAX_POCKET_NAME_LENGTH characters or less.") }
            return
        }
        if (hasPocketName(cleanName, excludePocketId = pocketId)) {
            mutableState.update { it.copy(message = "You already have a pocket with that name.") }
            return
        }
        if (cleanPurpose.isBlank()) {
            mutableState.update { it.copy(message = "Pocket purpose cannot be blank.") }
            return
        }
        if (cleanPurpose.length > MAX_POCKET_PURPOSE_LENGTH) {
            mutableState.update { it.copy(message = "Pocket purpose must be $MAX_POCKET_PURPOSE_LENGTH characters or less.") }
            return
        }

        runLoading {
            repository.updatePocket(pocketId, cleanName, cleanPurpose, targetAmount, targetScope)
            mutableState.update { it.copy(message = "Pocket updated.") }
        }
    }

    fun deletePocket(pocketId: String) {
        val state = mutableState.value
        val nextPocketId = state.pockets.firstOrNull { it.id != pocketId }?.id
        val isCurrentPocket = state.pocket?.id == pocketId
        runLoading {
            repository.deletePocket(
                pocketId = pocketId,
                isCurrentPocket = isCurrentPocket,
                nextPocketId = nextPocketId,
            )
            mutableState.update {
                it.copy(
                    board = if (isCurrentPocket) null else it.board,
                    pocket = if (isCurrentPocket && nextPocketId == null) null else it.pocket,
                    yearSummaries = if (isCurrentPocket) emptyList() else it.yearSummaries,
                    selectedSummaryMonthKey = null,
                    selectedMonthSavedCells = emptyList(),
                    selectedMonthLoading = false,
                    message = "Pocket deleted.",
                )
            }
        }
    }

    fun switchPocket(pocketId: String) {
        if (pocketId == mutableState.value.pocket?.id) {
            mutableState.update { it.copy(showPocketPicker = false) }
            return
        }

        runLoading {
            selectedMonthRegistration?.remove()
            selectedMonthRegistration = null
            mutableState.update {
                it.copy(
                    board = null,
                    yearSummaries = emptyList(),
                    allMonthSummaries = emptyList(),
                    selectedSummaryYear = SavingsBoardGenerator.currentYear(),
                    selectedSummaryMonthKey = null,
                    selectedMonthSavedCells = emptyList(),
                    selectedMonthLoading = false,
                    showPocketPicker = false,
                )
            }
            repository.switchPocket(pocketId)
        }
    }

    fun toggleCell(cell: SavingsCell) {
        val pocket = mutableState.value.pocket ?: return
        val board = mutableState.value.board ?: return
        val todayKey = SavingsBoardGenerator.todayKey()
        if (!cell.canToggle(todayKey)) {
            mutableState.update { it.copy(message = "That saved cell is locked because the day has passed.") }
            return
        }

        runLoading(showLoading = false) {
            val saved = repository.toggleCell(pocket.id, board.monthKey, cell)
            if (saved) reminderScheduler.recordSavedToday()
        }
    }

    fun updateMonthSavedTotal(monthKey: String, savedTotal: Int) {
        val pocketId = activePocketId ?: mutableState.value.pocket?.id ?: return
        if (savedTotal !in 0..MAX_POCKET_TARGET_AMOUNT) {
            mutableState.update {
                it.copy(message = "Saved total must be between 0 and $MAX_POCKET_TARGET_AMOUNT.")
            }
            return
        }

        runLoading(showLoading = false) {
            repository.updateMonthSavedTotal(pocketId, monthKey, savedTotal)
            mutableState.update {
                it.withMonthSavedTotal(
                    monthKey = monthKey,
                    savedTotal = savedTotal,
                    savedTotalOverride = savedTotal,
                ).copy(message = "Monthly saved total updated.")
            }
        }
    }

    fun clearMonthSavedTotalOverride(monthKey: String) {
        val pocketId = activePocketId ?: mutableState.value.pocket?.id ?: return
        val resetTotal = mutableState.value.board
            ?.takeIf { it.monthKey == monthKey }
            ?.cellSavedTotal
            ?: 0
        runLoading(showLoading = false) {
            repository.clearMonthSavedTotalOverride(pocketId, monthKey, resetTotal)
            mutableState.update {
                it.withMonthSavedTotal(
                    monthKey = monthKey,
                    savedTotal = resetTotal,
                    savedTotalOverride = null,
                ).copy(message = "Monthly saved total reset.")
            }
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

    fun previousSummaryYear() {
        setSummaryYear(mutableState.value.selectedSummaryYear - 1)
    }

    fun nextSummaryYear() {
        setSummaryYear(mutableState.value.selectedSummaryYear + 1)
    }

    fun openSummaryMonth(monthKey: String) {
        val pocketId = activePocketId ?: mutableState.value.pocket?.id ?: return
        selectedMonthRegistration?.remove()
        mutableState.update {
            it.copy(
                selectedSummaryMonthKey = monthKey,
                selectedMonthSavedCells = emptyList(),
                selectedMonthLoading = true,
            )
        }
        selectedMonthRegistration = repository.listenSavedCellsForMonth(
            pocketId = pocketId,
            monthKey = monthKey,
            onCells = { cells ->
                mutableState.update {
                    it.copy(
                        selectedMonthSavedCells = cells,
                        selectedMonthLoading = false,
                    )
                }
            },
            onError = ::showError,
        )
    }

    fun closeSummaryMonth() {
        selectedMonthRegistration?.remove()
        selectedMonthRegistration = null
        mutableState.update {
            it.copy(
                selectedSummaryMonthKey = null,
                selectedMonthSavedCells = emptyList(),
                selectedMonthLoading = false,
            )
        }
    }

    fun updateProfilePhoto(
        uri: Uri,
        zoom: Float = 1f,
        offsetX: Float = 0f,
        offsetY: Float = 0f,
    ) {
        runLoading {
            val session = repository.updateProfilePhoto(
                imageUri = uri,
                zoom = zoom,
                offsetX = offsetX,
                offsetY = offsetY,
            )
            mutableState.update { it.copy(user = session, message = "Profile photo updated.") }
        }
    }

    fun deleteProfilePhoto() {
        runLoading {
            val session = repository.deleteProfilePhoto()
            mutableState.update { it.copy(user = session, message = "Profile photo deleted.") }
        }
    }

    fun updateDisplayName(name: String) {
        val cleanName = name.cleanPocketName()
        if (cleanName.isBlank()) {
            mutableState.update { it.copy(message = "Name cannot be blank.") }
            return
        }
        if (cleanName.length > MAX_DISPLAY_NAME_LENGTH) {
            mutableState.update { it.copy(message = "Name must be $MAX_DISPLAY_NAME_LENGTH characters or less.") }
            return
        }

        runLoading {
            val session = repository.updateDisplayName(cleanName)
            mutableState.update { it.copy(user = session, message = "Name updated.") }
        }
    }

    fun changePassword(currentPassword: String, newPassword: String, confirmPassword: String) {
        val validation = when {
            currentPassword.isBlank() -> "Enter your current password."
            passwordSecurityMessage(newPassword) != null -> passwordSecurityMessage(newPassword)
            newPassword != confirmPassword -> "New passwords do not match."
            else -> null
        }
        if (validation != null) {
            mutableState.update { it.copy(message = validation) }
            return
        }

        runLoading {
            repository.changePassword(currentPassword, newPassword)
            mutableState.update { it.copy(message = "Password changed.") }
        }
    }

    fun deleteAccountWithPassword(currentPassword: String) {
        if (currentPassword.isBlank()) {
            mutableState.update { it.copy(message = "Enter your current password to delete this account.") }
            return
        }

        runLoading {
            repository.deleteAccountWithPassword(currentPassword)
            stopFirestoreListeners()
            mutableState.value = MainUiState(loading = false)
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

    fun setReminderMinute(minute: Int) {
        viewModelScope.launch {
            settingsStore.setReminderMinute(minute)
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            settingsStore.setThemeMode(mode)
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
                pockets = if (session.isEmailVerified) it.pockets else emptyList(),
            )
        }

        if (session.isEmailVerified) {
            startUserPocketListener()
            startUserPocketsListener()
        } else {
            stopFirestoreListeners()
        }
    }

    private fun startUserPocketsListener() {
        pocketsRegistration?.remove()
        pocketsRegistration = repository.listenUserPockets(
            onPockets = { pockets ->
                mutableState.update {
                    it.copy(
                        pockets = pockets,
                        showPocketPicker = if (pockets.isEmpty()) true else it.showPocketPicker,
                    )
                }
            },
            onError = ::showError,
        )
    }

    private fun startUserPocketListener() {
        userRegistration?.remove()
        userRegistration = repository.listenUserDocument(
            onUserDocument = { user, pocketId ->
                mutableState.update { it.copy(user = user) }
                if (pocketId == null) {
                    activePocketId = null
                    pocketRegistration?.remove()
                    boardRegistration?.remove()
                    summariesRegistration?.remove()
                    allSummariesRegistration?.remove()
                    selectedMonthRegistration?.remove()
                    mutableState.update {
                        it.copy(
                            pocket = null,
                            board = null,
                            yearSummaries = emptyList(),
                            allMonthSummaries = emptyList(),
                            selectedSummaryMonthKey = null,
                            selectedMonthSavedCells = emptyList(),
                            selectedMonthLoading = false,
                            loading = false,
                        )
                    }
                    return@listenUserDocument
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
        selectedMonthRegistration?.remove()
        selectedMonthRegistration = null
        attemptedAutoBoardRepairs.clear()
        val currentYear = SavingsBoardGenerator.currentYear()
        mutableState.update {
            it.copy(
                yearSummaries = emptyList(),
                allMonthSummaries = emptyList(),
                selectedSummaryYear = currentYear,
                selectedSummaryMonthKey = null,
                selectedMonthSavedCells = emptyList(),
                selectedMonthLoading = false,
            )
        }
        pocketRegistration = repository.listenPocket(
            pocketId = pocketId,
            onPocket = { pocket ->
                mutableState.update { it.copy(pocket = pocket, loading = false) }
            },
            onError = ::showError,
        )
        startSummaryListener(pocketId, currentYear)
        startAllSummariesListener(pocketId)

        viewModelScope.launch {
            runCatching {
                withTimeout(FIREBASE_ACTION_TIMEOUT_MILLIS) {
                    val monthKey = SavingsBoardGenerator.currentMonthKey()
                    boardRegistration = repository.listenMonthBoard(
                        pocketId = pocketId,
                        monthKey = monthKey,
                        onBoard = { board ->
                            mutableState.update { it.copy(board = board, loading = false) }
                            val repairKey = "$pocketId:$monthKey"
                            if (board.cells.isEmpty() && attemptedAutoBoardRepairs.add(repairKey)) {
                                viewModelScope.launch {
                                    runCatching {
                                        withTimeout(FIREBASE_ACTION_TIMEOUT_MILLIS) {
                                            repository.ensureMonthBoard(pocketId, monthKey)
                                        }
                                    }.onFailure(::showError)
                                }
                            }
                        },
                        onError = ::showError,
                    )
                    repository.ensureMonthBoard(pocketId, monthKey)
                }
            }.onFailure(::showError)
        }
    }

    private fun setSummaryYear(year: Int) {
        val pocketId = activePocketId ?: mutableState.value.pocket?.id ?: return
        selectedMonthRegistration?.remove()
        selectedMonthRegistration = null
        mutableState.update {
            it.copy(
                selectedSummaryYear = year,
                selectedSummaryMonthKey = null,
                selectedMonthSavedCells = emptyList(),
                selectedMonthLoading = false,
                yearSummaries = emptyList(),
            )
        }
        startSummaryListener(pocketId, year)
    }

    private fun startSummaryListener(pocketId: String, year: Int) {
        summariesRegistration?.remove()
        summariesRegistration = repository.listenYearSummaries(
            pocketId = pocketId,
            year = year,
            onSummaries = { summaries ->
                mutableState.update { it.copy(yearSummaries = summaries) }
            },
            onError = ::showError,
        )
    }

    private fun startAllSummariesListener(pocketId: String) {
        allSummariesRegistration?.remove()
        allSummariesRegistration = repository.listenAllMonthSummaries(
            pocketId = pocketId,
            onSummaries = { summaries ->
                mutableState.update { it.copy(allMonthSummaries = summaries) }
            },
            onError = ::showError,
        )
    }

    private fun stopFirestoreListeners() {
        userRegistration?.remove()
        pocketsRegistration?.remove()
        pocketRegistration?.remove()
        boardRegistration?.remove()
        summariesRegistration?.remove()
        allSummariesRegistration?.remove()
        selectedMonthRegistration?.remove()
        userRegistration = null
        pocketsRegistration = null
        pocketRegistration = null
        boardRegistration = null
        summariesRegistration = null
        allSummariesRegistration = null
        selectedMonthRegistration = null
    }

    private fun MainUiState.withMonthSavedTotal(
        monthKey: String,
        savedTotal: Int,
        savedTotalOverride: Int?,
    ): MainUiState {
        fun updateSummaries(summaries: List<MonthSummary>): List<MonthSummary> {
            return summaries.map { summary ->
                if (summary.monthKey == monthKey) summary.copy(savedTotal = savedTotal) else summary
            }
        }

        return copy(
            board = board?.let { board ->
                if (board.monthKey == monthKey) {
                    board.copy(savedTotalOverride = savedTotalOverride)
                } else {
                    board
                }
            },
            yearSummaries = updateSummaries(yearSummaries),
            allMonthSummaries = updateSummaries(allMonthSummaries),
        )
    }

    private fun runLoading(showLoading: Boolean = true, block: suspend () -> Unit) {
        viewModelScope.launch {
            if (showLoading) mutableState.update { it.copy(loading = true, message = null) }
            runCatching {
                withTimeout(FIREBASE_ACTION_TIMEOUT_MILLIS) {
                    block()
                }
            }
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
                    it.copy(message = "Email verified. Create your first pocket.")
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
        return when {
            this is TimeoutCancellationException -> {
                "Firebase is taking too long. Check the emulator internet connection, then try again."
            }
            this is FirebaseFirestoreException && code == FirebaseFirestoreException.Code.UNAVAILABLE -> {
                "Firestore is offline. Check the emulator internet connection, then try again."
            }
            this is FirebaseFirestoreException && code == FirebaseFirestoreException.Code.PERMISSION_DENIED -> {
                "You do not have permission for this pocket. Check your account or Firestore rules."
            }
            this is FirebaseFirestoreException && code == FirebaseFirestoreException.Code.RESOURCE_EXHAUSTED -> {
                "Firebase quota is exhausted, so this pocket cannot create savings cards now. Try later or check Firebase usage."
            }
            else -> message ?: "Something went wrong. Try again."
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
            name.cleanPocketName().length > MAX_DISPLAY_NAME_LENGTH -> "Name must be $MAX_DISPLAY_NAME_LENGTH characters or less."
            !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() -> "Enter a valid email address."
            passwordSecurityMessage(password) != null -> passwordSecurityMessage(password)
            password != confirmPassword -> "Passwords do not match."
            else -> null
        }
    }

    private fun hasPocketName(name: String, excludePocketId: String? = null): Boolean {
        val normalizedName = name.normalizedForCompare()
        return mutableState.value.pockets.any { pocket ->
            pocket.id != excludePocketId && pocket.name.normalizedForCompare() == normalizedName
        }
    }

    private fun parsePocketTargetAmount(value: String): ParsedTargetAmount? {
        val cleanValue = value.trim()
        if (cleanValue.isBlank()) return ParsedTargetAmount(null)
        val amount = cleanValue.toIntOrNull()
        return when {
            amount == null -> {
                mutableState.update { it.copy(message = "Target amount must be a whole number.") }
                null
            }
            amount <= 0 -> {
                mutableState.update { it.copy(message = "Target amount must be greater than zero.") }
                null
            }
            amount > MAX_POCKET_TARGET_AMOUNT -> {
                mutableState.update { it.copy(message = "Target amount must be Rs. $MAX_POCKET_TARGET_AMOUNT or less.") }
                null
            }
            else -> ParsedTargetAmount(amount)
        }
    }

    private fun String.cleanPocketName(): String {
        return trim().replace(Regex("\\s+"), " ")
    }

    private fun String.normalizedForCompare(): String {
        return cleanPocketName().lowercase()
    }

    private fun passwordSecurityMessage(password: String): String? {
        return when {
            password.length < MIN_PASSWORD_LENGTH -> "Password must be at least $MIN_PASSWORD_LENGTH characters."
            password.none { it.isLetter() } -> "Password must include at least one letter."
            password.none { it.isDigit() } -> "Password must include at least one number."
            else -> null
        }
    }

    private fun cacheBootSettings(appSettings: AppSettings) {
        getApplication<Application>()
            .getSharedPreferences(ReminderScheduler.SETTINGS_BOOT_CACHE, 0)
            .edit()
            .putBoolean(ReminderScheduler.KEY_REMINDERS_ENABLED, appSettings.remindersEnabled)
            .putInt(ReminderScheduler.KEY_REMINDER_HOUR, appSettings.reminderHour)
            .putInt(ReminderScheduler.KEY_REMINDER_MINUTE, appSettings.reminderMinute)
            .apply()
    }

    private companion object {
        const val FIREBASE_ACTION_TIMEOUT_MILLIS = 15_000L
        const val MAX_DISPLAY_NAME_LENGTH = 80
        const val MAX_POCKET_NAME_LENGTH = 40
        const val MAX_POCKET_PURPOSE_LENGTH = 140
        const val MIN_PASSWORD_LENGTH = 8
    }

    private data class ParsedTargetAmount(val amount: Int?)
}
