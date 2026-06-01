package dev.theolm.freestyle_libre_alarm.domain.repository

import dev.theolm.freestyle_libre_alarm.domain.model.LibreNotification
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    val lastNotification: Flow<LibreNotification?>
    suspend fun saveNotification(notification: LibreNotification)
    fun getNotificationHistory(): Flow<List<LibreNotification>>
    suspend fun clearHistory()
}
