package com.thisara.mypocket.reminders

import android.Manifest
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.thisara.mypocket.R
import com.thisara.mypocket.data.ReminderPolicy

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        rescheduleNextReminder(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) return
        }

        val prefs = context.getSharedPreferences(ReminderScheduler.REMINDER_STATE, Context.MODE_PRIVATE)
        val lastSavedDay = prefs.getString(ReminderScheduler.KEY_LAST_SAVED_DAY, null)
        val today = ReminderScheduler.today()
        if (!ReminderPolicy.shouldNotify(lastSavedDay, today)) return

        val notification = NotificationCompat.Builder(context, ReminderScheduler.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("My Pocket")
            .setContentText("No saving was marked today. Tap a cell when you put money aside.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        context.getSystemService(NotificationManager::class.java).notify(1001, notification)
    }

    private fun rescheduleNextReminder(context: Context) {
        val prefs = context.getSharedPreferences(ReminderScheduler.SETTINGS_BOOT_CACHE, Context.MODE_PRIVATE)
        if (!prefs.getBoolean(ReminderScheduler.KEY_REMINDERS_ENABLED, true)) return

        ReminderScheduler(context).scheduleDaily(
            hour = prefs.getInt(ReminderScheduler.KEY_REMINDER_HOUR, 20),
            minute = prefs.getInt(ReminderScheduler.KEY_REMINDER_MINUTE, 0),
        )
    }
}
