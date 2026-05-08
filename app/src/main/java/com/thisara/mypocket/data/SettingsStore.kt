package com.thisara.mypocket.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "settings")

data class AppSettings(
    val remindersEnabled: Boolean = true,
    val reminderHour: Int = 20,
    val darkModeEnabled: Boolean = false,
)

class SettingsStore(private val context: Context) {
    private val remindersEnabledKey = booleanPreferencesKey("reminders_enabled")
    private val reminderHourKey = intPreferencesKey("reminder_hour")
    private val darkModeEnabledKey = booleanPreferencesKey("dark_mode_enabled")

    val settings: Flow<AppSettings> = context.settingsDataStore.data.map { preferences ->
        AppSettings(
            remindersEnabled = preferences[remindersEnabledKey] ?: true,
            reminderHour = preferences[reminderHourKey] ?: 20,
            darkModeEnabled = preferences[darkModeEnabledKey] ?: false,
        )
    }

    suspend fun setRemindersEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[remindersEnabledKey] = enabled
        }
    }

    suspend fun setReminderHour(hour: Int) {
        context.settingsDataStore.edit { preferences ->
            preferences[reminderHourKey] = hour.coerceIn(0, 23)
        }
    }

    suspend fun setDarkModeEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[darkModeEnabledKey] = enabled
        }
    }
}
