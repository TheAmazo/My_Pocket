package com.thisara.mypocket.data

data class UserSession(
    val uid: String,
    val name: String,
    val email: String,
    val isEmailVerified: Boolean,
    val isGoogleUser: Boolean,
)

data class Pocket(
    val id: String,
    val name: String,
    val inviteCode: String,
    val memberIds: List<String>,
)

data class SavingsCell(
    val id: String,
    val index: Int,
    val amount: Int,
    val saved: Boolean = false,
    val savedByUid: String? = null,
    val savedByName: String? = null,
    val savedAtMillis: Long? = null,
)

data class MonthBoard(
    val monthKey: String,
    val cells: List<SavingsCell>,
) {
    val targetTotal: Int = cells.sumOf { it.amount }
    val savedTotal: Int = cells.filter { it.saved }.sumOf { it.amount }
    val remainingTotal: Int = targetTotal - savedTotal
    val savedCount: Int = cells.count { it.saved }
}

data class ActivityItem(
    val amount: Int,
    val savedByName: String,
    val savedAtMillis: Long,
)
