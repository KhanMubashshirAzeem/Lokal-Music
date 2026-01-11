
package com.mubashshir.lokalmusic.data.model

data class SimpleAlbum(
    val id: String,
    val name: String,
    val url: String,
    val image: List<ImageXX>,
    val artists: Artists,
    val songCount: Int,
    val language: String,
    val year: String
)