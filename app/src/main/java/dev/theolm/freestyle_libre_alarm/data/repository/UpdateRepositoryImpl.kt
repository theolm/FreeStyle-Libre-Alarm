package dev.theolm.freestyle_libre_alarm.data.repository

import android.content.Context
import dev.theolm.freestyle_libre_alarm.data.remote.dto.GitHubReleaseDto
import dev.theolm.freestyle_libre_alarm.domain.model.UpdateInfo
import dev.theolm.freestyle_libre_alarm.domain.repository.CheckUpdateResult
import dev.theolm.freestyle_libre_alarm.domain.repository.UpdateRepository
import dev.theolm.freestyle_libre_alarm.domain.util.SemVerParser
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.onDownload
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import java.io.File

class UpdateRepositoryImpl(
    private val context: Context
) : UpdateRepository {

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    private var downloadedFile: File? = null

    override suspend fun checkForUpdate(currentVersion: String): CheckUpdateResult {
        return try {
            val response: HttpResponse = client.get(GITHUB_API_URL)

            if (response.status.value !in 200..299) {
                return CheckUpdateResult.Error(
                    "Erro ${response.status.value}: Não foi possível verificar atualizações"
                )
            }

            val release: GitHubReleaseDto = response.body()

            if (release.prerelease) {
                return CheckUpdateResult.UpToDate
            }

            if (!SemVerParser.isNewer(currentVersion, release.tagName)) {
                return CheckUpdateResult.UpToDate
            }

            val apkAsset = findApkAsset(release.assets)
                ?: return CheckUpdateResult.UpToDate

            CheckUpdateResult.Available(
                UpdateInfo(
                    version = release.tagName,
                    changelog = release.body?.takeIf { it.isNotBlank() },
                    downloadUrl = apkAsset.browserDownloadUrl
                )
            )
        } catch (e: ClientRequestException) {
            CheckUpdateResult.Error(
                "Erro ${e.response.status.value}: Não foi possível verificar atualizações"
            )
        } catch (e: Exception) {
            CheckUpdateResult.Error(
                "Não foi possível verificar atualizações. Verifique sua conexão e tente novamente."
            )
        }
    }

    override suspend fun downloadApk(url: String, onProgress: (Int) -> Unit): File {
        cleanupDownloadedFile()

        val file = File(context.cacheDir, "update.apk")
        downloadedFile = file

        client.get(url) {
            onDownload { bytesSentTotal, contentLength ->
                if (contentLength != null && contentLength > 0) {
                    val progress = ((bytesSentTotal * 100) / contentLength).toInt()
                    onProgress(progress)
                }
            }
        }.let { response ->
            file.writeBytes(response.body())
        }

        return file
    }

    override fun cleanupDownloadedFile() {
        downloadedFile?.let { file ->
            if (file.exists()) {
                file.delete()
            }
        }
        downloadedFile = null
    }

    private fun findApkAsset(assets: List<dev.theolm.freestyle_libre_alarm.data.remote.dto.ReleaseAssetDto>): dev.theolm.freestyle_libre_alarm.data.remote.dto.ReleaseAssetDto? {
        return assets.find { it.contentType == APK_CONTENT_TYPE }
            ?: assets.find { it.name.endsWith(".apk", ignoreCase = true) }
    }

    companion object {
        private const val GITHUB_API_URL = "https://api.github.com/repos/theolm/FreeStyle-Libre-Alarm/releases/latest"
        private const val APK_CONTENT_TYPE = "application/vnd.android.package-archive"
    }
}
