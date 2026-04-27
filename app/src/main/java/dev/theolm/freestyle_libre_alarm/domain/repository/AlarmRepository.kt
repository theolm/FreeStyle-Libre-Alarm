package dev.theolm.freestyle_libre_alarm.domain.repository

import dev.theolm.freestyle_libre_alarm.domain.model.AlarmEvent
import kotlinx.coroutines.flow.Flow

interface AlarmRepository {
    suspend fun insertAlarmEvent(event: AlarmEvent)
    fun getAllAlarmEvents(): Flow<List<AlarmEvent>>
    suspend fun acknowledgeAlarmEvent(id: Long)
    suspend fun clearHistory()
}
