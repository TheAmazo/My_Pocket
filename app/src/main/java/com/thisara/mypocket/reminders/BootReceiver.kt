package com.thisara.mypocket.reminders

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED) return

        val prefs = context.getSharedPreferences(ReminderScheduler.SETTINGS_BOOT_CACHE, Context.MODE_PRIVATE)
        if (prefs.getBoolean(ReminderScheduler.KEY_REMINDERS_ENABLED, true)) {
            ReminderScheduler(context).scheduleDaily(
                hour = prefs.getInt(ReminderScheduler.KEY_REMINDER_HOUR, 20),
                minute = prefs.getInt(ReminderScheduler.KEY_REMINDER_MINUTE, 0),
            )
        }
    }
}
