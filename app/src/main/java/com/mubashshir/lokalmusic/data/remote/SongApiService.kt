package com.mubashshir.lokalmusic.data.remote

import com.mubashshir.lokalmusic.data.model.AlbumResponse
import com.mubashshir.lokalmusic.data.model.AlbumsApiResponse
import com.mubashshir.lokalmusic.data.model.ArtistsApiResponse
import com.mubashshir.lokalmusic.data.model.SongsApiResponce
import retrofit2.http.GET
import retrofit2.http.Query

interface SongApiService {

    @GET("search/songs")
    suspend fun searchSongs(
        @Query("query") query: String,
        @Query("name") name: String? = null,
        @Query("limit") limit: Int = 20,
        @Query("page") page: Int = 1
    ): SongsApiResponce

    @GET("albums")
    suspend fun getAlbum(
        @Query("id") id: String
    ): AlbumResponse

    @GET("search/artists")
    suspend fun searchArtists(
        @Query("query") query: String,
        @Query("limit") limit: Int = 20
    ): ArtistsApiResponse

    @GET("search/albums")
    suspend fun searchAlbums(
        @Query("query") query: String,
        @Query("limit") limit: Int = 20
    ): AlbumsApiResponse
}
