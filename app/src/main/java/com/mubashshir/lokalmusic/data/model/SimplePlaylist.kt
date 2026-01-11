package com.mubashshir.lokalmusic.data.model

data class SimplePlaylist(
    val id: String,
    val name: String,
    val url: String,
    val image: List<ImageXX>,
    val songCount: Int,
    val followerCount: Int,
    val language: String
)