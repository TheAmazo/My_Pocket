package com.thisara.mypocket.reminders

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED) return

        val prefs = context.getSharedPreferences("settings_boot_cache", Context.MODE_PRIVATE)
        if (prefs.getBoolean("reminders_enabled", true)) {
            ReminderScheduler(context).scheduleDaily(prefs.getInt("reminder_hour", 20))
        }
    }
}
