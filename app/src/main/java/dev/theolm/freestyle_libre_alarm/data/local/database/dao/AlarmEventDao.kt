package dev.theolm.freestyle_libre_alarm.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import dev.theolm.freestyle_libre_alarm.data.local.database.entity.AlarmEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmEventDao {
    @Insert
    suspend fun insert(event: AlarmEventEntity): Long

    @Query("SELECT * FROM alarm_events ORDER BY timestamp DESC")
    fun getAll(): Flow<List<AlarmEventEntity>>

    @Update
    suspend fun update(event: AlarmEventEntity)

    @Query("UPDATE alarm_events SET acknowledged = 1 WHERE id = :id")
    suspend fun acknowledge(id: Long)

    @Query("DELETE FROM alarm_events")
    suspend fun clearAll()
}
