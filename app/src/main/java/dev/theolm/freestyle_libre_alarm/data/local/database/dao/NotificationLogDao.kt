package dev.theolm.freestyle_libre_alarm.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.theolm.freestyle_libre_alarm.data.local.database.entity.NotificationLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationLogDao {
    @Insert
    suspend fun insert(log: NotificationLogEntity): Long

    @Query("SELECT * FROM notification_logs ORDER BY timestamp DESC")
    fun getAll(): Flow<List<NotificationLogEntity>>

    @Query("DELETE FROM notification_logs")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM notification_logs")
    suspend fun getCount(): Int
}