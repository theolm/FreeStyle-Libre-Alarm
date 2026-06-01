package dev.theolm.freestyle_libre_alarm.data.repository

import dev.theolm.freestyle_libre_alarm.data.local.database.LibreDatabase
import dev.theolm.freestyle_libre_alarm.data.local.database.entity.NotificationLogEntity
import dev.theolm.freestyle_libre_alarm.domain.model.NotificationLog
import dev.theolm.freestyle_libre_alarm.domain.repository.NotificationLogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NotificationLogRepositoryImpl(
    private val database: LibreDatabase
) : NotificationLogRepository {

    override suspend fun insertNotificationLog(log: NotificationLog) {
        database.notificationLogDao().insert(
            NotificationLogEntity(
                packageName = log.packageName,
                appName = log.appName,
                title = log.title,
                text = log.text,
                subText = log.subText,
                tickerText = log.tickerText,
                bigText = log.bigText,
                summaryText = log.summaryText,
                timestamp = log.timestamp,
                postTime = log.postTime,
                key = log.key,
                groupKey = log.groupKey,
                sortKey = log.sortKey,
                extrasJson = log.extrasJson,
                isOngoing = log.isOngoing,
                isClearable = log.isClearable
            )
        )
    }

    override fun getAllNotificationLogs(): Flow<List<NotificationLog>> {
        return database.notificationLogDao().getAll().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun clearAllLogs() {
        database.notificationLogDao().clearAll()
    }

    override suspend fun getLogCount(): Int {
        return database.notificationLogDao().getCount()
    }

    private fun NotificationLogEntity.toDomainModel(): NotificationLog {
        return NotificationLog(
            id = id,
            packageName = packageName,
            appName = appName,
            title = title,
            text = text,
            subText = subText,
            tickerText = tickerText,
            bigText = bigText,
            summaryText = summaryText,
            timestamp = timestamp,
            postTime = postTime,
            key = key,
            groupKey = groupKey,
            sortKey = sortKey,
            extrasJson = extrasJson,
            isOngoing = isOngoing,
            isClearable = isClearable
        )
    }
}