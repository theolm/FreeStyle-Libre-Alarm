package dev.theolm.freestyle_libre_alarm.domain.model

data class LibreNotification(
    val id: String,
    val packageName: String,
    val title: String,
    val text: String,
    val timestamp: Long
)
