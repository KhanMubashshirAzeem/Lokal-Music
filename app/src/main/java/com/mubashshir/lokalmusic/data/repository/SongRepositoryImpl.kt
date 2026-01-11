package com.mubashshir.lokalmusic.data.repository

import com.mubashshir.lokalmusic.data.model.AlbumResponse
import com.mubashshir.lokalmusic.data.model.PlaylistResponse
import com.mubashshir.lokalmusic.data.model.SimpleAlbum
import com.mubashshir.lokalmusic.data.model.SimpleArtist
import com.mubashshir.lokalmusic.data.model.SimplePlaylist
import com.mubashshir.lokalmusic.data.remote.SongApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import com.mubashshir.lokalmusic.data.model.Result as SongModel

class SongRepositoryImpl @Inject constructor(
    private val apiService: SongApiService
) : SongRepository
{

    override fun searchSongs(query: String): Flow<kotlin.Result<List<SongModel>>> =
        flow {
            try
            {
                val response =
                    apiService.searchSongs(query)
                if (response.success)
                {
                    emit(
                        kotlin.Result.success(
                            response.data.results
                        )
                    )
                } else
                {
                    emit(
                        kotlin.Result.failure(
                            Exception("API returned success=false")
                        )
                    )
                }
            } catch (e: Exception)
            {
                emit(kotlin.Result.failure(e))
            }
        }.flowOn(Dispatchers.IO)

    override fun getAlbum(albumId: String): Flow<kotlin.Result<AlbumResponse>> =
        flow {
            try
            {
                val response =
                    apiService.getAlbum(albumId)
                if (response.success && response.data != null)
                {
                    emit(
                        kotlin.Result.success(
                            response
                        )
                    )
                } else
                {
                    emit(
                        kotlin.Result.failure(
                            Exception("Album not found or invalid response")
                        )
                    )
                }
            } catch (e: Exception)
            {
                emit(kotlin.Result.failure(e))
            }
        }.flowOn(Dispatchers.IO)

    override fun getPlaylist(playlistId: String): Flow<kotlin.Result<PlaylistResponse>> =
        flow {
            try
            {
                val response =
                    apiService.getPlaylist(
                        playlistId
                    )
                if (response.success && response.data != null)
                {
                    emit(
                        kotlin.Result.success(
                            response
                        )
                    )
                } else
                {
                    emit(
                        kotlin.Result.failure(
                            Exception("Playlist not found or invalid response")
                        )
                    )
                }
            } catch (e: Exception)
            {
                emit(kotlin.Result.failure(e))
            }
        }.flowOn(Dispatchers.IO)

    override fun searchArtists(query: String): Flow<kotlin.Result<List<SimpleArtist>>> =
        flow {
            try
            {
                val response =
                    apiService.searchArtists(query)
                if (response.success)
                {
                    emit(
                        kotlin.Result.success(
                            response.data.results
                        )
                    )
                } else
                {
                    emit(
                        kotlin.Result.failure(
                            Exception("API returned success=false")
                        )
                    )
                }
            } catch (e: Exception)
            {
                emit(kotlin.Result.failure(e))
            }
        }.flowOn(Dispatchers.IO)

    override fun searchAlbums(query: String): Flow<kotlin.Result<List<SimpleAlbum>>> =
        flow {
            try
            {
                val response =
                    apiService.searchAlbums(query)
                if (response.success)
                {
                    emit(
                        kotlin.Result.success(
                            response.data.results
                        )
                    )
                } else
                {
                    emit(
                        kotlin.Result.failure(
                            Exception("API returned success=false")
                        )
                    )
                }
            } catch (e: Exception)
            {
                emit(kotlin.Result.failure(e))
            }
        }.flowOn(Dispatchers.IO)

    override fun searchPlaylists(query: String): Flow<kotlin.Result<List<SimplePlaylist>>> =
        flow {
            try
            {
                val response =
                    apiService.searchPlaylists(
                        query
                    )
                if (response.success)
                {
                    emit(
                        kotlin.Result.success(
                            response.data.results
                        )
                    )
                } else
                {
                    emit(
                        kotlin.Result.failure(
                            Exception("API returned success=false")
                        )
                    )
                }
            } catch (e: Exception)
            {
                emit(kotlin.Result.failure(e))
            }
        }.flowOn(Dispatchers.IO)
}