package dev.theolm.freestyle_libre_alarm.domain.model

data class AppSettings(
    val isAlarmEnabled: Boolean = true,
    val isLowGlucoseEnabled: Boolean = true,
    val isHighGlucoseEnabled: Boolean = true,
    val isDarkModeEnabled: Boolean = false,
    val snoozeEndTime: Long = 0L,
    val lastDismissedVersion: String? = null,
    val useCustomThresholds: Boolean = false,
    val lowThresholdMgDl: Int = 70,
    val highThresholdMgDl: Int = 180
)
