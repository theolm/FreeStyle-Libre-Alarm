package dev.theolm.freestyle_libre_alarm.domain.model

data class AppSettings(
    val isAlarmEnabled: Boolean = true,
    val librePackageName: String = "com.freestylelibre.app",
    val isLowGlucoseEnabled: Boolean = true,
    val isHighGlucoseEnabled: Boolean = true,
    val isDarkModeEnabled: Boolean = false,
    val snoozeEndTime: Long = 0L
)
