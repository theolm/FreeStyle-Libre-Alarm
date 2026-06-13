package dev.theolm.freestyle_libre_alarm.domain.repository

import dev.theolm.freestyle_libre_alarm.domain.model.AppSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val settings: Flow<AppSettings>
    suspend fun updateAlarmEnabled(enabled: Boolean)
    suspend fun updateLowGlucoseEnabled(enabled: Boolean)
    suspend fun updateHighGlucoseEnabled(enabled: Boolean)
    suspend fun updateDarkModeEnabled(enabled: Boolean)
    suspend fun updateSnoozeEndTime(endTime: Long)
    suspend fun updateLastDismissedVersion(version: String?)
    suspend fun updateUseCustomThresholds(enabled: Boolean)
    suspend fun updateLowThresholdMgDl(threshold: Int)
    suspend fun updateHighThresholdMgDl(threshold: Int)
}
