package dev.theolm.freestyle_libre_alarm.domain.repository

import dev.theolm.freestyle_libre_alarm.domain.model.UpdateInfo
import java.io.File

sealed class CheckUpdateResult {
    data class Available(val info: UpdateInfo) : CheckUpdateResult()
    data object UpToDate : CheckUpdateResult()
    data class Error(val code: Int? = null) : CheckUpdateResult()
}

interface UpdateRepository {
    suspend fun checkForUpdate(currentVersion: String): CheckUpdateResult
    suspend fun downloadApk(url: String, onProgress: (Int) -> Unit): File
    fun cleanupDownloadedFile()
}
