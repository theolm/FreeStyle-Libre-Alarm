package dev.theolm.freestyle_libre_alarm.domain.model

data class NotificationLog(
    val id: Long = 0,
    val packageName: String,
    val appName: String?,
    val title: String?,
    val text: String?,
    val subText: String?,
    val tickerText: String?,
    val bigText: String?,
    val summaryText: String?,
    val timestamp: Long,
    val postTime: Long,
    val key: String?,
    val groupKey: String?,
    val sortKey: String?,
    val extrasJson: String?,
    val isOngoing: Boolean = false,
    val isClearable: Boolean = true
)