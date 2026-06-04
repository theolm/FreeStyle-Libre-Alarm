package dev.theolm.freestyle_libre_alarm.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.theolm.freestyle_libre_alarm.domain.model.GlucoseAlert
import dev.theolm.freestyle_libre_alarm.domain.repository.GlucoseAlertRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val glucoseAlertRepository: GlucoseAlertRepository
) : ViewModel() {

    val glucoseAlerts: StateFlow<List<GlucoseAlert>> = glucoseAlertRepository.getAllAlerts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    class Factory(
        private val glucoseAlertRepository: GlucoseAlertRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HistoryViewModel(glucoseAlertRepository) as T
        }
    }
}
