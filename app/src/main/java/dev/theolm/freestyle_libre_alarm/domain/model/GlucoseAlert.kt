package dev.theolm.freestyle_libre_alarm.domain.model

data class GlucoseAlert(
    val id: Long = 0,
    val type: String,
    val glucoseValueMgDl: Int?,
    val trend: String?,
    val timestamp: Long,
    val rawTitle: String?,
    val rawText: String?
)
