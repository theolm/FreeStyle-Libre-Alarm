package dev.theolm.freestyle_libre_alarm.domain.model

data class UpdateInfo(
    val version: String,
    val changelog: String?,
    val downloadUrl: String
)
