package com.thisara.mypocket

import android.app.Application
import com.google.firebase.FirebaseApp
import com.thisara.mypocket.reminders.ReminderScheduler

class MyPocketApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        ReminderScheduler(this).createNotificationChannel()
    }
}
