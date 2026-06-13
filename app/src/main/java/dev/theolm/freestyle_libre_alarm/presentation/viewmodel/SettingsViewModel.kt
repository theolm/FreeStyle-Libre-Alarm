package dev.theolm.freestyle_libre_alarm.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.theolm.freestyle_libre_alarm.domain.model.AppSettings
import dev.theolm.freestyle_libre_alarm.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val settings: StateFlow<AppSettings> = settingsRepository.settings
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppSettings()
        )

    fun updateAlarmEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateAlarmEnabled(enabled)
        }
    }

    fun updateLibrePackageName(packageName: String) {
        viewModelScope.launch {
            settingsRepository.updateLibrePackageName(packageName)
        }
    }

    fun updateLowGlucoseEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateLowGlucoseEnabled(enabled)
        }
    }

    fun updateHighGlucoseEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateHighGlucoseEnabled(enabled)
        }
    }

    fun updateDarkModeEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateDarkModeEnabled(enabled)
        }
    }

    fun updateUseCustomThresholds(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateUseCustomThresholds(enabled)
        }
    }

    fun updateLowThresholdMgDl(threshold: Int) {
        viewModelScope.launch {
            settingsRepository.updateLowThresholdMgDl(threshold)
        }
    }

    fun updateHighThresholdMgDl(threshold: Int) {
        viewModelScope.launch {
            settingsRepository.updateHighThresholdMgDl(threshold)
        }
    }

    class Factory(
        private val settingsRepository: SettingsRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SettingsViewModel(settingsRepository) as T
        }
    }
}
