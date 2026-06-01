package dev.theolm.freestyle_libre_alarm.domain.repository

import dev.theolm.freestyle_libre_alarm.domain.model.NotificationLog
import kotlinx.coroutines.flow.Flow

interface NotificationLogRepository {
    suspend fun insertNotificationLog(log: NotificationLog)
    fun getAllNotificationLogs(): Flow<List<NotificationLog>>
    suspend fun clearAllLogs()
    suspend fun getLogCount(): Int
}