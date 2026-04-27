package dev.theolm.freestyle_libre_alarm.domain.model

data class AlarmEvent(
    val id: Long = 0,
    val notificationTitle: String,
    val notificationText: String,
    val timestamp: Long,
    val acknowledged: Boolean = false
)
