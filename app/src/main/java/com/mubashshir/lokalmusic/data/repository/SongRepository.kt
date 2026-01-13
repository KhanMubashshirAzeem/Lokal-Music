package com.mubashshir.lokalmusic.data.repository

import com.mubashshir.lokalmusic.data.model.AlbumResponse
import com.mubashshir.lokalmusic.data.model.SimpleAlbum
import com.mubashshir.lokalmusic.data.model.SimpleArtist
import kotlinx.coroutines.flow.Flow
import com.mubashshir.lokalmusic.data.model.Results

interface SongRepository {

    fun searchSongs(
        query: String,
        name: String? = null,
    ): Flow<Result<List<Results>>>

    fun getAlbum(
        albumId: String
    ): Flow<Result<AlbumResponse>>

    fun searchArtists(
        query: String,
    ): Flow<Result<List<SimpleArtist>>>

    fun searchAlbums(
        query: String,
    ): Flow<Result<List<SimpleAlbum>>>
}
