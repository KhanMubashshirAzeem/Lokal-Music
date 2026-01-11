// data/model/AlbumResponse.kt
package com.mubashshir.lokalmusic.data.model

data class AlbumResponse(
    val success: Boolean,
    val data: AlbumData?
)

data class AlbumData(
    val id: String,
    val name: String,
    val image: List<ImageXX>,
    val year: String?,
    val label: String?,
    val language: String?,
    val songs: List<Result>,          // ← same Result as song (great reuse!)
    // ... more fields if needed (artists, playCount, etc.)
)