package com.thisara.mypocket.data

data class UserSession(
    val uid: String,
    val name: String,
    val email: String,
    val photoData: String?,
    val createdAtMillis: Long?,
    val lastLoginAtMillis: Long?,
    val isEmailVerified: Boolean,
    val isGoogleUser: Boolean,
)

data class Pocket(
    val id: String,
    val name: String,
    val purpose: String = DEFAULT_POCKET_PURPOSE,
    val targetAmount: Int? = null,
    val targetScope: TargetScope = TargetScope.MONTHLY,
)

const val DEFAULT_POCKET_PURPOSE = "Personal savings pocket"
const val MAX_POCKET_TARGET_AMOUNT = 720000

enum class TargetScope {
    MONTHLY,
    LIFETIME,
}

data class SavingsCell(
    val id: String,
    val index: Int,
    val amount: Int,
    val saved: Boolean = false,
    val savedByUid: String? = null,
    val savedByName: String? = null,
    val savedAtMillis: Long? = null,
    val savedDayKey: String? = null,
)

data class MonthBoard(
    val monthKey: String,
    val cells: List<SavingsCell>,
    val savedTotalOverride: Int? = null,
) {
    val targetTotal: Int = cells.sumOf { it.amount }
    val cellSavedTotal: Int = cells.filter { it.saved }.sumOf { it.amount }
    val savedTotal: Int = savedTotalOverride ?: cellSavedTotal
    val remainingTotal: Int = targetTotal - savedTotal
    val savedCount: Int = cells.count { it.saved }
    val isComplete: Boolean = cells.isNotEmpty() && savedCount == cells.size
}

data class ActivityItem(
    val amount: Int,
    val savedByName: String,
    val savedAtMillis: Long,
)

data class MonthSummary(
    val monthKey: String,
    val targetTotal: Int,
    val savedTotal: Int,
    val savedCount: Int,
    val cellCount: Int,
) {
    val missedTotal: Int = (targetTotal - savedTotal).coerceAtLeast(0)
}

fun SavingsCell.isLocked(todayKey: String): Boolean {
    return saved && savedDayKey != todayKey
}

fun SavingsCell.canToggle(todayKey: String): Boolean {
    return !isLocked(todayKey)
}

fun adjustedSavedTotalOverride(currentTotal: Int, delta: Int): Int {
    return (currentTotal + delta).coerceIn(0, MAX_POCKET_TARGET_AMOUNT)
}

fun SavingsCell.sortRank(todayKey: String): Int {
    return when {
        isLocked(todayKey) -> 2
        saved -> 1
        else -> 0
    }
}
