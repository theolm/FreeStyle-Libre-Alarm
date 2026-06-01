package dev.theolm.freestyle_libre_alarm.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notification_logs")
data class NotificationLogEntity(
    @PrimaryKey(autoGenerate = true)
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