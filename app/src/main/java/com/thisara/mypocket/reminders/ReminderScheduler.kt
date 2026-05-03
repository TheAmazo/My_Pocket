package com.thisara.mypocket.reminders

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.thisara.mypocket.R
import com.thisara.mypocket.data.SavingsBoardGenerator
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar

class ReminderScheduler(private val context: Context) {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.notification_channel_savings),
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = context.getString(R.string.notification_channel_savings_description)
        }

        context.getSystemService(NotificationManager::class.java)
            .createNotificationChannel(channel)
    }

    fun scheduleDaily(hour: Int) {
        val intent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            Intent(context, ReminderReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            nextTriggerMillis(hour),
            AlarmManager.INTERVAL_DAY,
            intent,
        )
    }

    fun cancel() {
        val intent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            Intent(context, ReminderReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        alarmManager.cancel(intent)
    }

    fun recordSavedToday() {
        context.getSharedPreferences(REMINDER_STATE, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_LAST_SAVED_DAY, SavingsBoardGenerator.todayKey())
            .apply()
    }

    private fun nextTriggerMillis(hour: Int): Long {
        val now = Calendar.getInstance()
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour.coerceIn(0, 23))
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (!after(now)) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }.timeInMillis
    }

    companion object {
        const val CHANNEL_ID = "savings_reminders"
        const val REMINDER_STATE = "reminder_state"
        const val KEY_LAST_SAVED_DAY = "last_saved_day"
        private const val REQUEST_CODE = 3120

        fun today(): String = LocalDate.now(ZoneId.systemDefault()).toString()
    }
}
