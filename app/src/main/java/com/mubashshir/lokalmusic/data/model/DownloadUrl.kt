package com.mubashshir.lokalmusic.data.model

data class DownloadUrl(
    val quality: String,
    val url: String
)

fun List<DownloadUrl>.getStreamUrl(): String {
    // FIX: Changed .link to .url to match the data class property
    return this.find { it.quality == "320kbps" }?.url
        ?: this.find { it.quality == "160kbps" }?.url
        ?: this.firstOrNull()?.url
        ?: ""
}