package com.mubashshir.lokalmusic.data.remote

import com.mubashshir.lokalmusic.data.model.AlbumResponse
import com.mubashshir.lokalmusic.data.model.AlbumsApiResponse
import com.mubashshir.lokalmusic.data.model.ArtistsApiResponse
import com.mubashshir.lokalmusic.data.model.PlaylistResponse
import com.mubashshir.lokalmusic.data.model.PlaylistsApiResponse
import com.mubashshir.lokalmusic.data.model.SongsApiResponce
import retrofit2.http.GET
import retrofit2.http.Query

interface SongApiService {

    @GET("api/search/songs")
    suspend fun searchSongs(
        @Query("query") query: String
    ): SongsApiResponce

    @GET("api/albums")
    suspend fun getAlbum(
        @Query("id") id: String
    ): AlbumResponse

    @GET("api/playlists")
    suspend fun getPlaylist(
        @Query("id") id: String
    ): PlaylistResponse

    @GET("api/search/artists")
    suspend fun searchArtists(
        @Query("query") query: String
    ): ArtistsApiResponse

    @GET("api/search/albums")
    suspend fun searchAlbums(
        @Query("query") query: String
    ): AlbumsApiResponse

    @GET("api/search/playlists")
    suspend fun searchPlaylists(
        @Query("query") query: String
    ): PlaylistsApiResponse
}