package dev.theolm.freestyle_libre_alarm.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "glucose_alerts")
data class GlucoseAlertEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String,
    val glucoseValueMgDl: Int?,
    val trend: String?,
    val timestamp: Long,
    val rawTitle: String?,
    val rawText: String?
)
