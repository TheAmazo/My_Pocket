package com.thisara.mypocket.data

object ReminderPolicy {
    fun shouldNotify(lastSavedDay: String?, today: String): Boolean {
        return lastSavedDay != today
    }
}
