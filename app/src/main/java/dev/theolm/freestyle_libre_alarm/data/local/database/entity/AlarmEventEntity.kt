package dev.theolm.freestyle_libre_alarm.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarm_events")
data class AlarmEventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val notificationTitle: String,
    val notificationText: String,
    val timestamp: Long,
    val acknowledged: Boolean = false
)
