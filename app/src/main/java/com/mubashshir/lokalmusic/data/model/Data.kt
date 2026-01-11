package com.mubashshir.lokalmusic.data.model

data class Data(
    val results: List<Result>,
    val start: Int,
    val total: Int
)