package dev.theolm.freestyle_libre_alarm.presentation.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.theolm.freestyle_libre_alarm.BuildConfig
import dev.theolm.freestyle_libre_alarm.domain.model.UpdateInfo
import dev.theolm.freestyle_libre_alarm.domain.repository.SettingsRepository
import dev.theolm.freestyle_libre_alarm.domain.repository.UpdateRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File

sealed class UpdateUiState {
    data object Idle : UpdateUiState()
    data object Checking : UpdateUiState()
    data class UpdateAvailable(val updateInfo: UpdateInfo) : UpdateUiState()
    data class Downloading(val progress: Int) : UpdateUiState()
    data class Downloaded(val file: File) : UpdateUiState()
    data class Error(val message: String) : UpdateUiState()
    data object UpToDate : UpdateUiState()
}

class UpdateViewModel(
    private val updateRepository: UpdateRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UpdateUiState>(UpdateUiState.Idle)
    val uiState: StateFlow<UpdateUiState> = _uiState.asStateFlow()

    private var downloadJob: Job? = null

    val currentVersion: String = BuildConfig.VERSION_NAME

    fun checkForUpdate(isAutomatic: Boolean = false) {
        if (_uiState.value is UpdateUiState.Checking || _uiState.value is UpdateUiState.Downloading) {
            return
        }

        _uiState.value = UpdateUiState.Checking

        viewModelScope.launch {
            when (val result = updateRepository.checkForUpdate(currentVersion)) {
                is dev.theolm.freestyle_libre_alarm.domain.repository.CheckUpdateResult.Available -> {
                    val settings = settingsRepository.settings.first()
                    val lastDismissed = settings.lastDismissedVersion

                    val shouldShow = if (lastDismissed != null) {
                        try {
                            dev.theolm.freestyle_libre_alarm.domain.util.SemVerParser.isNewer(lastDismissed, result.info.version)
                        } catch (e: Exception) {
                            true
                        }
                    } else {
                        true
                    }

                    if (shouldShow) {
                        _uiState.value = UpdateUiState.UpdateAvailable(result.info)
                    } else {
                        _uiState.value = UpdateUiState.UpToDate
                    }
                }
                is dev.theolm.freestyle_libre_alarm.domain.repository.CheckUpdateResult.UpToDate -> {
                    _uiState.value = UpdateUiState.UpToDate
                }
                is dev.theolm.freestyle_libre_alarm.domain.repository.CheckUpdateResult.Error -> {
                    if (isAutomatic) {
                        _uiState.value = UpdateUiState.Idle
                    } else {
                        _uiState.value = UpdateUiState.Error(result.message)
                    }
                }
            }
        }
    }

    fun downloadUpdate() {
        val currentState = _uiState.value
        if (currentState !is UpdateUiState.UpdateAvailable) {
            return
        }

        _uiState.value = UpdateUiState.Downloading(0)

        downloadJob = viewModelScope.launch {
            try {
                val file = updateRepository.downloadApk(
                    currentState.updateInfo.downloadUrl
                ) { progress ->
                    _uiState.value = UpdateUiState.Downloading(progress)
                }
                _uiState.value = UpdateUiState.Downloaded(file)
            } catch (e: Exception) {
                _uiState.value = UpdateUiState.Error("Falha ao baixar atualização. Tente novamente mais tarde.")
            }
        }
    }

    fun dismissUpdate() {
        val currentState = _uiState.value
        if (currentState is UpdateUiState.UpdateAvailable) {
            viewModelScope.launch {
                settingsRepository.updateLastDismissedVersion(currentState.updateInfo.version)
            }
        }
        _uiState.value = UpdateUiState.Idle
    }

    fun cancelDownload() {
        downloadJob?.cancel()
        downloadJob = null
        updateRepository.cleanupDownloadedFile()
        _uiState.value = UpdateUiState.Idle
    }

    fun installUpdate(context: Context) {
        val currentState = _uiState.value
        if (currentState !is UpdateUiState.Downloaded) {
            return
        }

        val file = currentState.file
        val intent = Intent(Intent.ACTION_VIEW).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
            val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
            } else {
                Uri.fromFile(file)
            }
            setDataAndType(uri, "application/vnd.android.package-archive")
        }
        context.startActivity(intent)
    }

    fun resetState() {
        downloadJob?.cancel()
        downloadJob = null
        updateRepository.cleanupDownloadedFile()
        _uiState.value = UpdateUiState.Idle
    }

    override fun onCleared() {
        super.onCleared()
        downloadJob?.cancel()
        updateRepository.cleanupDownloadedFile()
    }

    class Factory(
        private val updateRepository: UpdateRepository,
        private val settingsRepository: SettingsRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return UpdateViewModel(updateRepository, settingsRepository) as T
        }
    }
}
