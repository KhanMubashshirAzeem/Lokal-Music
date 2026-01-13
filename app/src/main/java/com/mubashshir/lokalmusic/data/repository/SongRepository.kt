package com.mubashshir.lokalmusic.data.repository

import com.mubashshir.lokalmusic.data.model.AlbumResponse
import com.mubashshir.lokalmusic.data.model.PlaylistResponse
import com.mubashshir.lokalmusic.data.model.SimpleAlbum
import com.mubashshir.lokalmusic.data.model.SimpleArtist
import com.mubashshir.lokalmusic.data.model.SimplePlaylist
import kotlinx.coroutines.flow.Flow
import com.mubashshir.lokalmusic.data.model.Result as SongModel

interface SongRepository {
    fun searchSongs(
        query: String, name:
        String? = null
    ): Flow<kotlin.Result<List<SongModel>>>

    fun getAlbum(albumId: String): Flow<kotlin.Result<AlbumResponse>>

    fun getPlaylist(playlistId: String): Flow<kotlin.Result<PlaylistResponse>>

    fun searchArtists(query: String): Flow<kotlin.Result<List<SimpleArtist>>>

    fun searchAlbums(query: String): Flow<kotlin.Result<List<SimpleAlbum>>>

    fun searchPlaylists(query: String): Flow<kotlin.Result<List<SimplePlaylist>>>

    fun getArtistSongs(artistId: String): Flow<kotlin.Result<List<SongModel>>>
}