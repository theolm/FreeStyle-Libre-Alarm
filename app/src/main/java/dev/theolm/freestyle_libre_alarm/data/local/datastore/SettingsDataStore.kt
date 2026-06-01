package dev.theolm.freestyle_libre_alarm.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dev.theolm.freestyle_libre_alarm.domain.model.AppSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")

class SettingsDataStore(private val context: Context) {

    private object Keys {
        val ALARM_ENABLED = booleanPreferencesKey("alarm_enabled")
        val LIBRE_PACKAGE_NAME = stringPreferencesKey("libre_package_name")
        val LOW_GLUCOSE_ENABLED = booleanPreferencesKey("low_glucose_enabled")
        val HIGH_GLUCOSE_ENABLED = booleanPreferencesKey("high_glucose_enabled")
        val DARK_MODE_ENABLED = booleanPreferencesKey("dark_mode_enabled")
        val SNOOZE_END_TIME = longPreferencesKey("snooze_end_time")
    }

    val settings: Flow<AppSettings> = context.dataStore.data.map { preferences ->
        AppSettings(
            isAlarmEnabled = preferences[Keys.ALARM_ENABLED] ?: true,
            librePackageName = preferences[Keys.LIBRE_PACKAGE_NAME] ?: "com.freestylelibre.app",
            isLowGlucoseEnabled = preferences[Keys.LOW_GLUCOSE_ENABLED] ?: true,
            isHighGlucoseEnabled = preferences[Keys.HIGH_GLUCOSE_ENABLED] ?: true,
            isDarkModeEnabled = preferences[Keys.DARK_MODE_ENABLED] ?: false,
            snoozeEndTime = preferences[Keys.SNOOZE_END_TIME] ?: 0L
        )
    }

    suspend fun updateAlarmEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[Keys.ALARM_ENABLED] = enabled
        }
    }

    suspend fun updateLibrePackageName(packageName: String) {
        context.dataStore.edit { preferences ->
            preferences[Keys.LIBRE_PACKAGE_NAME] = packageName
        }
    }

    suspend fun updateLowGlucoseEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[Keys.LOW_GLUCOSE_ENABLED] = enabled
        }
    }

    suspend fun updateHighGlucoseEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[Keys.HIGH_GLUCOSE_ENABLED] = enabled
        }
    }

    suspend fun updateDarkModeEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[Keys.DARK_MODE_ENABLED] = enabled
        }
    }

    suspend fun updateSnoozeEndTime(endTime: Long) {
        context.dataStore.edit { preferences ->
            preferences[Keys.SNOOZE_END_TIME] = endTime
        }
    }
}
