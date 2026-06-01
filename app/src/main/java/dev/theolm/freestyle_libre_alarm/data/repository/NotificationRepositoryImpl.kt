package dev.theolm.freestyle_libre_alarm.data.repository

import dev.theolm.freestyle_libre_alarm.domain.model.LibreNotification
import dev.theolm.freestyle_libre_alarm.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class NotificationRepositoryImpl : NotificationRepository {

    private val _lastNotification = MutableStateFlow<LibreNotification?>(null)
    private val _history = MutableStateFlow<List<LibreNotification>>(emptyList())

    override val lastNotification: Flow<LibreNotification?> = _lastNotification.asStateFlow()

    override suspend fun saveNotification(notification: LibreNotification) {
        _lastNotification.value = notification
        _history.value = _history.value + notification
    }

    override fun getNotificationHistory(): Flow<List<LibreNotification>> {
        return _history.asStateFlow()
    }

    override suspend fun clearHistory() {
        _history.value = emptyList()
    }
}
