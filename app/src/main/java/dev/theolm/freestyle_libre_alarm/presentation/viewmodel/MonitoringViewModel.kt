package dev.theolm.freestyle_libre_alarm.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.theolm.freestyle_libre_alarm.data.alarm.AlarmManager
import dev.theolm.freestyle_libre_alarm.domain.model.AppSettings
import dev.theolm.freestyle_libre_alarm.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MonitoringViewModel(
    private val context: Context,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MonitoringUiState())
    val uiState: StateFlow<MonitoringUiState> = _uiState.asStateFlow()

    init {
        AlarmManager.init(context)
        viewModelScope.launch {
            settingsRepository.settings.collect { settings ->
                _uiState.update { it.copy(settings = settings) }
            }
        }
    }

    fun toggleAlarm(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateAlarmEnabled(enabled)
        }
    }

    fun triggerTestAlarm() {
        AlarmManager.triggerAlarm()
    }

    data class MonitoringUiState(
        val settings: AppSettings = AppSettings()
    )
}
