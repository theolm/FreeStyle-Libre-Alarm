package dev.theolm.freestyle_libre_alarm.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubReleaseDto(
    @SerialName("tag_name")
    val tagName: String,
    val prerelease: Boolean,
    val body: String? = null,
    val assets: List<ReleaseAssetDto> = emptyList()
)

@Serializable
data class ReleaseAssetDto(
    val name: String,
    @SerialName("browser_download_url")
    val browserDownloadUrl: String,
    @SerialName("content_type")
    val contentType: String
)
