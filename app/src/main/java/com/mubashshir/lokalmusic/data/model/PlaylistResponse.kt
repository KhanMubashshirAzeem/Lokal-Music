// data/model/PlaylistResponse.kt
package com.mubashshir.lokalmusic.data.model

data class PlaylistResponse(
    val success: Boolean,
    val data: PlaylistData?
)

data class PlaylistData(
    val id: String,
    val name: String,
    val image: List<ImageXX>,
    val followerCount: Int?,
    val songCount: Int?,
    val songs: List<Result>,          // ← again, reuse Result
    // ... more fields (description, user, etc.)
)