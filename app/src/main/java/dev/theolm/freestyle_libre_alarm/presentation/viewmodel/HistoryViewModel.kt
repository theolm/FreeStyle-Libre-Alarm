package dev.theolm.freestyle_libre_alarm.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.theolm.freestyle_libre_alarm.domain.model.NotificationLog
import dev.theolm.freestyle_libre_alarm.domain.repository.NotificationLogRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val notificationLogRepository: NotificationLogRepository
) : ViewModel() {

    val notificationLogs: StateFlow<List<NotificationLog>> = notificationLogRepository.getAllNotificationLogs()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun clearLogs() {
        viewModelScope.launch {
            notificationLogRepository.clearAllLogs()
        }
    }

    class Factory(
        private val notificationLogRepository: NotificationLogRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            return HistoryViewModel(notificationLogRepository) as T
        }
    }
}