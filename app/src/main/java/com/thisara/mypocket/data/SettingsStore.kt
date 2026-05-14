package com.thisara.mypocket.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "settings")

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK,
}

data class AppSettings(
    val remindersEnabled: Boolean = true,
    val reminderHour: Int = 20,
    val reminderMinute: Int = 0,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
)

class SettingsStore(private val context: Context) {
    private val remindersEnabledKey = booleanPreferencesKey("reminders_enabled")
    private val reminderHourKey = intPreferencesKey("reminder_hour")
    private val reminderMinuteKey = intPreferencesKey("reminder_minute")
    private val themeModeKey = stringPreferencesKey("theme_mode")

    val settings: Flow<AppSettings> = context.settingsDataStore.data.map { preferences ->
        AppSettings(
            remindersEnabled = preferences[remindersEnabledKey] ?: true,
            reminderHour = preferences[reminderHourKey] ?: 20,
            reminderMinute = preferences[reminderMinuteKey] ?: 0,
            themeMode = preferences[themeModeKey]?.let(::themeModeFromValue) ?: ThemeMode.SYSTEM,
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

    suspend fun setReminderMinute(minute: Int) {
        context.settingsDataStore.edit { preferences ->
            preferences[reminderMinuteKey] = minute.coerceIn(0, 59)
        }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.settingsDataStore.edit { preferences ->
            preferences[themeModeKey] = mode.name
        }
    }

    private fun themeModeFromValue(value: String): ThemeMode {
        return ThemeMode.entries.firstOrNull { it.name == value } ?: ThemeMode.SYSTEM
    }
}
