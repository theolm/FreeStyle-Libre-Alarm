package dev.theolm.freestyle_libre_alarm.data.repository

import android.content.Context
import dev.theolm.freestyle_libre_alarm.AppLogger
import dev.theolm.freestyle_libre_alarm.data.remote.dto.GitHubReleaseDto
import dev.theolm.freestyle_libre_alarm.domain.model.UpdateInfo
import dev.theolm.freestyle_libre_alarm.domain.repository.CheckUpdateResult
import dev.theolm.freestyle_libre_alarm.domain.repository.UpdateRepository
import dev.theolm.freestyle_libre_alarm.domain.util.SemVerParser
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.contentLength
import io.ktor.serialization.kotlinx.json.json

import kotlinx.serialization.json.Json
import java.io.File

class UpdateRepositoryImpl(
    private val context: Context
) : UpdateRepository {

    private val logger = AppLogger.log

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    private var downloadedFile: File? = null

    @Suppress("ReturnCount")
    override suspend fun checkForUpdate(currentVersion: String): CheckUpdateResult {
        logger.d { "Checking for update. Current version: $currentVersion" }
        
        return try {
            logger.d { "Fetching GitHub API: $GITHUB_API_URL" }
            val response: HttpResponse = client.get(GITHUB_API_URL)
            
            logger.d { "GitHub API response status: ${response.status}" }

            if (response.status.value !in 200..299) {
                logger.e { "GitHub API returned error: ${response.status}" }
                return CheckUpdateResult.Error(response.status.value)
            }

            val release: GitHubReleaseDto = response.body()
            logger.d { "GitHub release: tag=${release.tagName}, prerelease=${release.prerelease}, " +
                "assets=${release.assets.size}" }

            if (release.prerelease) {
                logger.d { "Ignoring prerelease version: ${release.tagName}" }
                return CheckUpdateResult.UpToDate
            }

            if (!SemVerParser.isNewer(currentVersion, release.tagName)) {
                logger.d { "Current version $currentVersion is up to date (latest: ${release.tagName})" }
                return CheckUpdateResult.UpToDate
            }

            val apkAsset = findApkAsset(release.assets)
            if (apkAsset == null) {
                logger.e { "No APK asset found in release ${release.tagName}" }
                return CheckUpdateResult.UpToDate
            }
            
            logger.d { "Found APK asset: ${apkAsset.name} at ${apkAsset.browserDownloadUrl}" }

            CheckUpdateResult.Available(
                UpdateInfo(
                    version = release.tagName,
                    changelog = release.body?.takeIf { it.isNotBlank() },
                    downloadUrl = apkAsset.browserDownloadUrl
                )
            )
        } catch (e: ClientRequestException) {
            logger.e(e) { "ClientRequestException: ${e.response.status}" }
            CheckUpdateResult.Error(e.response.status.value)
        } catch (e: Exception) {
            logger.e(e) { "Exception during update check" }
            CheckUpdateResult.Error()
        }
    }

    override suspend fun downloadApk(url: String, onProgress: (Int) -> Unit): File {
        logger.d { "Starting APK download from: $url" }
        cleanupDownloadedFile()

        val file = File(context.cacheDir, "update.apk")
        downloadedFile = file

        logger.d { "Download destination: ${file.absolutePath}" }

        try {
            val response: HttpResponse = client.get(url)
            logger.d { "Download response status: ${response.status}" }

            if (response.status.value !in 200..299) {
                error("Download failed with status: ${response.status}")
            }

            val contentLength = response.contentLength()
            logger.d { "Content-Length: $contentLength" }

            val bodyBytes: ByteArray = response.body()
            logger.d { "Downloaded ${bodyBytes.size} bytes" }

            if (bodyBytes.isEmpty()) {
                error("Downloaded file is empty")
            }

            file.writeBytes(bodyBytes)

            // Report 100% progress since download is complete
            onProgress(100)
            logger.d { "Download progress: 100% (${bodyBytes.size} bytes)" }

            logger.d { "Downloaded ${bodyBytes.size} bytes" }

            if (bodyBytes.isEmpty()) {
                error("Downloaded file is empty")
            }

            if (contentLength != null && bodyBytes.size.toLong() != contentLength) {
                logger.w { "Download size mismatch: expected $contentLength, got ${bodyBytes.size}" }
            }

            logger.d { "APK saved to: ${file.absolutePath}, size: ${file.length()} bytes" }
            return file
        } catch (e: Exception) {
            logger.e(e) { "Download failed" }
            cleanupDownloadedFile()
            throw e
        }
    }

    override fun cleanupDownloadedFile() {
        downloadedFile?.let { file ->
            if (file.exists()) {
                val deleted = file.delete()
                logger.d { "Cleaned up downloaded file: ${file.absolutePath}, deleted=$deleted" }
            }
        }
        downloadedFile = null
    }

    private fun findApkAsset(
        assets: List<dev.theolm.freestyle_libre_alarm.data.remote.dto.ReleaseAssetDto>
    ): dev.theolm.freestyle_libre_alarm.data.remote.dto.ReleaseAssetDto? {
        val apkAsset = assets.find { it.contentType == APK_CONTENT_TYPE }
            ?: assets.find { it.name.endsWith(".apk", ignoreCase = true) }
        
        if (apkAsset != null) {
            logger.d { "Selected APK asset: ${apkAsset.name} (contentType=${apkAsset.contentType})" }
        }
        
        return apkAsset
    }

    companion object {
        private const val GITHUB_API_URL = "https://api.github.com/repos/theolm/FreeStyle-Libre-Alarm/releases/latest"
        private const val APK_CONTENT_TYPE = "application/vnd.android.package-archive"
    }
}
