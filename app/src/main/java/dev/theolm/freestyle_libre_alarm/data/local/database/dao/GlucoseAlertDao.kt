package dev.theolm.freestyle_libre_alarm.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.theolm.freestyle_libre_alarm.data.local.database.entity.GlucoseAlertEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GlucoseAlertDao {
    @Insert
    suspend fun insert(alert: GlucoseAlertEntity): Long

    @Query("SELECT * FROM glucose_alerts ORDER BY timestamp DESC")
    fun getAll(): Flow<List<GlucoseAlertEntity>>

    @Query("DELETE FROM glucose_alerts")
    suspend fun clearAll()
}
