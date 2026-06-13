package dev.theolm.freestyle_libre_alarm.presentation.viewmodel

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.annotation.StringRes
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.theolm.freestyle_libre_alarm.AppLogger
import dev.theolm.freestyle_libre_alarm.BuildConfig
import dev.theolm.freestyle_libre_alarm.R
import dev.theolm.freestyle_libre_alarm.domain.model.UpdateInfo
import dev.theolm.freestyle_libre_alarm.domain.repository.CheckUpdateResult
import dev.theolm.freestyle_libre_alarm.domain.repository.SettingsRepository
import dev.theolm.freestyle_libre_alarm.domain.repository.UpdateRepository
import dev.theolm.freestyle_libre_alarm.domain.usecase.ShouldShowUpdate
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

sealed class UpdateUiState {
    data object Idle : UpdateUiState()
    data object Checking : UpdateUiState()
    data class UpdateAvailable(val updateInfo: UpdateInfo) : UpdateUiState()
    data class Downloading(val progress: Int) : UpdateUiState()
    data class Downloaded(val file: File) : UpdateUiState()
    data class Error(@StringRes val messageResId: Int, val formatArgs: List<Any> = emptyList()) : UpdateUiState()
    data object UpToDate : UpdateUiState()
    data object NeedsPermission : UpdateUiState()
}

class UpdateViewModel(
    private val updateRepository: UpdateRepository,
    private val settingsRepository: SettingsRepository,
    private val shouldShowUpdate: ShouldShowUpdate
) : ViewModel() {

    private val logger = AppLogger.log

    private val _uiState = MutableStateFlow<UpdateUiState>(UpdateUiState.Idle)
    val uiState: StateFlow<UpdateUiState> = _uiState.asStateFlow()

    private var downloadJob: Job? = null
    private var pendingInstallFile: File? = null

    val currentVersion: String = BuildConfig.VERSION_NAME

    fun checkForUpdate(isAutomatic: Boolean = false) {
        if (_uiState.value is UpdateUiState.Checking || _uiState.value is UpdateUiState.Downloading) {
            logger.d { "checkForUpdate skipped: already in progress" }
            return
        }

        logger.d { "checkForUpdate started (isAutomatic=$isAutomatic)" }
        _uiState.value = UpdateUiState.Checking

        viewModelScope.launch {
            when (val result = updateRepository.checkForUpdate(currentVersion)) {
                is CheckUpdateResult.Available -> {
                    logger.d { "Update available: ${result.info.version}" }
                    val shouldShow = shouldShowUpdate(result.info.version)

                    if (shouldShow) {
                        logger.d { "Showing update available dialog" }
                        _uiState.value = UpdateUiState.UpdateAvailable(result.info)
                    } else {
                        logger.d { "Update ${result.info.version} was dismissed, hiding" }
                        _uiState.value = UpdateUiState.UpToDate
                    }
                }
                is CheckUpdateResult.UpToDate -> {
                    logger.d { "App is up to date" }
                    _uiState.value = UpdateUiState.UpToDate
                }
                is CheckUpdateResult.Error -> {
                    logger.e { "Update check failed: code=${result.code}" }
                    if (isAutomatic) {
                        _uiState.value = UpdateUiState.Idle
                    } else {
                        val resId = if (result.code != null) {
                            R.string.error_update_check
                        } else {
                            R.string.error_update_network
                        }
                        val args = if (result.code != null) listOf(result.code as Any) else emptyList()
                        _uiState.value = UpdateUiState.Error(resId, args)
                    }
                }
            }
        }
    }

    fun downloadUpdate() {
        val currentState = _uiState.value
        if (currentState !is UpdateUiState.UpdateAvailable) {
            logger.w { "downloadUpdate called but state is not UpdateAvailable: $currentState" }
            return
        }

        logger.d { "Starting download for version: ${currentState.updateInfo.version}" }
        _uiState.value = UpdateUiState.Downloading(0)

        downloadJob = viewModelScope.launch {
            try {
                val file = updateRepository.downloadApk(
                    currentState.updateInfo.downloadUrl
                ) { progress ->
                    _uiState.value = UpdateUiState.Downloading(progress)
                }
                logger.d { "Download completed: ${file.absolutePath}" }
                _uiState.value = UpdateUiState.Downloaded(file)
            } catch (e: Exception) {
                logger.e(e) { "Download failed" }
                _uiState.value = UpdateUiState.Error(R.string.error_download_failed)
            }
        }
    }

    fun dismissUpdate() {
        val currentState = _uiState.value
        if (currentState is UpdateUiState.UpdateAvailable) {
            logger.d { "Dismissing update ${currentState.updateInfo.version}" }
            viewModelScope.launch {
                settingsRepository.updateLastDismissedVersion(currentState.updateInfo.version)
            }
        }
        _uiState.value = UpdateUiState.Idle
    }

    fun cancelDownload() {
        logger.d { "Canceling download" }
        downloadJob?.cancel()
        downloadJob = null
        updateRepository.cleanupDownloadedFile()
        _uiState.value = UpdateUiState.Idle
    }

    fun checkInstallPermission(context: Context): Boolean {
        val canInstall = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.packageManager.canRequestPackageInstalls()
        } else {
            true
        }
        logger.d { "canRequestPackageInstalls: $canInstall" }
        return canInstall
    }

    fun requestInstallPermission(context: Context) {
        logger.d { "Requesting install permission" }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                data = "package:${context.packageName}".toUri()
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        }
    }

    fun installUpdate(context: Context) {
        val currentState = _uiState.value
        if (currentState !is UpdateUiState.Downloaded) {
            logger.w { "installUpdate called but state is not Downloaded: $currentState" }
            return
        }

        if (!checkInstallPermission(context)) {
            logger.w { "Install permission not granted" }
            pendingInstallFile = currentState.file
            _uiState.value = UpdateUiState.NeedsPermission
            return
        }

        val file = currentState.file
        logger.d { "Installing update from: ${file.absolutePath}" }
        
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
                logger.d { "APK URI: $uri" }
                setDataAndType(uri, "application/vnd.android.package-archive")
            }
            context.startActivity(intent)
            logger.d { "Install intent launched successfully" }
        } catch (e: Exception) {
            logger.e(e) { "Failed to launch install intent" }
            _uiState.value = UpdateUiState.Error(R.string.error_install_failed, listOf(e.message ?: ""))
        }
    }

    fun retryInstallAfterPermission(context: Context) {
        logger.d { "Retrying install after permission grant" }
        pendingInstallFile?.let { file ->
            _uiState.value = UpdateUiState.Downloaded(file)
            installUpdate(context)
        } ?: run {
            _uiState.value = UpdateUiState.Error(R.string.error_install_file_not_found)
        }
        pendingInstallFile = null
    }

    fun resetState() {
        logger.d { "Resetting state" }
        downloadJob?.cancel()
        downloadJob = null
        pendingInstallFile = null
        updateRepository.cleanupDownloadedFile()
        _uiState.value = UpdateUiState.Idle
    }

    override fun onCleared() {
        super.onCleared()
        logger.d { "ViewModel cleared, cleaning up" }
        downloadJob?.cancel()
        updateRepository.cleanupDownloadedFile()
    }

    class Factory(
        private val updateRepository: UpdateRepository,
        private val settingsRepository: SettingsRepository,
        private val shouldShowUpdate: ShouldShowUpdate
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return UpdateViewModel(updateRepository, settingsRepository, shouldShowUpdate) as T
        }
    }
}
