package com.mubashshir.lokalmusic.data.model

data class SimpleArtist(
    val id: String,
    val name: String,
    val image: List<ImageXX>,
    val role: String,
    val type: String,
    val url: String
)