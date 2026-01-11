package com.mubashshir.lokalmusic.data.remote

import com.mubashshir.lokalmusic.data.model.SongsApiResponce
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SongApiService {

    // Corrected: Should return the response model, not the interface itself
    @GET("api/songs/{songId}")
    suspend fun getSongById(
        @Path("songId") songId: String
    ): SongsApiResponce

    @GET("api/search/songs")
    suspend fun searchSongs(
        @Query("query") query: String
    ): SongsApiResponce
}