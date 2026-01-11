package com.mubashshir.lokalmusic.data.remote

import com.mubashshir.lokalmusic.data.model.SongsApiResponce
import retrofit2.http.GET
import retrofit2.http.Query

interface SongApiService {

    @GET("api/search/songs")
    suspend fun searchSongs(
        @Query("query") query: String
    ): SongsApiResponce

}