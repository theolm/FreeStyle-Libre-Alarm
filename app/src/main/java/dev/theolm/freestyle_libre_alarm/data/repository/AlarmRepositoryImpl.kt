package dev.theolm.freestyle_libre_alarm.data.repository

import dev.theolm.freestyle_libre_alarm.data.local.database.LibreDatabase
import dev.theolm.freestyle_libre_alarm.data.local.database.entity.AlarmEventEntity
import dev.theolm.freestyle_libre_alarm.domain.model.AlarmEvent
import dev.theolm.freestyle_libre_alarm.domain.repository.AlarmRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AlarmRepositoryImpl(
    private val database: LibreDatabase
) : AlarmRepository {

    override suspend fun insertAlarmEvent(event: AlarmEvent) {
        database.alarmEventDao().insert(
            AlarmEventEntity(
                notificationTitle = event.notificationTitle,
                notificationText = event.notificationText,
                timestamp = event.timestamp,
                acknowledged = event.acknowledged
            )
        )
    }

    override fun getAllAlarmEvents(): Flow<List<AlarmEvent>> {
        return database.alarmEventDao().getAll().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun acknowledgeAlarmEvent(id: Long) {
        database.alarmEventDao().acknowledge(id)
    }

    override suspend fun clearHistory() {
        database.alarmEventDao().clearAll()
    }

    private fun AlarmEventEntity.toDomainModel(): AlarmEvent {
        return AlarmEvent(
            id = id,
            notificationTitle = notificationTitle,
            notificationText = notificationText,
            timestamp = timestamp,
            acknowledged = acknowledged
        )
    }
}
