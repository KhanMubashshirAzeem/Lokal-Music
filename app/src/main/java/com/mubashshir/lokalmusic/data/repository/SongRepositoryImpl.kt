package com.mubashshir.lokalmusic.data.repository

import android.util.Log
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

    private val TAG = "SongRepositoryImpl"

    override fun searchSongs(query: String): Flow<kotlin.Result<List<SongModel>>> =
        flow {
            Log.d(
                TAG,
                "searchSongs called with query: $query"
            )
            try
            {
                val response =
                    apiService.searchSongs(query)
                if (response.success)
                {
                    val results =
                        response.data.results
                    Log.d(
                        TAG,
                        "searchSongs success: Found ${results.size} songs"
                    )
                    emit(
                        kotlin.Result.success(
                            results
                        )
                    )
                } else
                {
                    Log.e(
                        TAG,
                        "searchSongs failed: API returned success=false"
                    )
                    emit(
                        kotlin.Result.failure(
                            Exception("API returned success=false")
                        )
                    )
                }
            } catch (e: Exception)
            {
                Log.e(
                    TAG,
                    "searchSongs exception: ${e.message}",
                    e
                )
                emit(kotlin.Result.failure(e))
            }
        }.flowOn(Dispatchers.IO)

    override fun getAlbum(albumId: String): Flow<kotlin.Result<AlbumResponse>> =
        flow {
            Log.d(
                TAG,
                "getAlbum called with ID: $albumId"
            )
            try
            {
                val response =
                    apiService.getAlbum(albumId)
                if (response.success && response.data != null)
                {
                    Log.d(
                        TAG,
                        "getAlbum success: ${response.data.name}"
                    )
                    emit(
                        kotlin.Result.success(
                            response
                        )
                    )
                } else
                {
                    Log.e(
                        TAG,
                        "getAlbum failed: Album not found or invalid response"
                    )
                    emit(
                        kotlin.Result.failure(
                            Exception("Album not found or invalid response")
                        )
                    )
                }
            } catch (e: Exception)
            {
                Log.e(
                    TAG,
                    "getAlbum exception: ${e.message}",
                    e
                )
                emit(kotlin.Result.failure(e))
            }
        }.flowOn(Dispatchers.IO)

    override fun getPlaylist(playlistId: String): Flow<kotlin.Result<PlaylistResponse>> =
        flow {
            Log.d(
                TAG,
                "getPlaylist called with ID: $playlistId"
            )
            try
            {
                val response =
                    apiService.getPlaylist(
                        playlistId
                    )
                if (response.success && response.data != null)
                {
                    Log.d(
                        TAG,
                        "getPlaylist success: ${response.data.name}"
                    )
                    emit(
                        kotlin.Result.success(
                            response
                        )
                    )
                } else
                {
                    Log.e(
                        TAG,
                        "getPlaylist failed: Playlist not found or invalid response"
                    )
                    emit(
                        kotlin.Result.failure(
                            Exception("Playlist not found or invalid response")
                        )
                    )
                }
            } catch (e: Exception)
            {
                Log.e(
                    TAG,
                    "getPlaylist exception: ${e.message}",
                    e
                )
                emit(kotlin.Result.failure(e))
            }
        }.flowOn(Dispatchers.IO)

    override fun searchArtists(query: String): Flow<kotlin.Result<List<SimpleArtist>>> =
        flow {
            Log.d(
                TAG,
                "searchArtists called with query: $query"
            )
            try
            {
                val response =
                    apiService.searchArtists(query)
                if (response.success)
                {
                    val results =
                        response.data.results
                    Log.d(
                        TAG,
                        "searchArtists success: Found ${results.size} artists"
                    )
                    emit(
                        kotlin.Result.success(
                            results
                        )
                    )
                } else
                {
                    Log.e(
                        TAG,
                        "searchArtists failed: API returned success=false"
                    )
                    emit(
                        kotlin.Result.failure(
                            Exception("API returned success=false")
                        )
                    )
                }
            } catch (e: Exception)
            {
                Log.e(
                    TAG,
                    "searchArtists exception: ${e.message}",
                    e
                )
                emit(kotlin.Result.failure(e))
            }
        }.flowOn(Dispatchers.IO)

    override fun searchAlbums(query: String): Flow<kotlin.Result<List<SimpleAlbum>>> =
        flow {
            Log.d(
                TAG,
                "searchAlbums called with query: $query"
            )
            try
            {
                val response =
                    apiService.searchAlbums(query)
                if (response.success)
                {
                    val results =
                        response.data.results
                    Log.d(
                        TAG,
                        "searchAlbums success: Found ${results.size} albums"
                    )
                    emit(
                        kotlin.Result.success(
                            results
                        )
                    )
                } else
                {
                    Log.e(
                        TAG,
                        "searchAlbums failed: API returned success=false"
                    )
                    emit(
                        kotlin.Result.failure(
                            Exception("API returned success=false")
                        )
                    )
                }
            } catch (e: Exception)
            {
                Log.e(
                    TAG,
                    "searchAlbums exception: ${e.message}",
                    e
                )
                emit(kotlin.Result.failure(e))
            }
        }.flowOn(Dispatchers.IO)

    override fun searchPlaylists(query: String): Flow<kotlin.Result<List<SimplePlaylist>>> =
        flow {
            Log.d(
                TAG,
                "searchPlaylists called with query: $query"
            )
            try
            {
                val response =
                    apiService.searchPlaylists(
                        query
                    )
                if (response.success)
                {
                    val results =
                        response.data.results
                    Log.d(
                        TAG,
                        "searchPlaylists success: Found ${results.size} playlists"
                    )
                    emit(
                        kotlin.Result.success(
                            results
                        )
                    )
                } else
                {
                    Log.e(
                        TAG,
                        "searchPlaylists failed: API returned success=false"
                    )
                    emit(
                        kotlin.Result.failure(
                            Exception("API returned success=false")
                        )
                    )
                }
            } catch (e: Exception)
            {
                Log.e(
                    TAG,
                    "searchPlaylists exception: ${e.message}",
                    e
                )
                emit(kotlin.Result.failure(e))
            }
        }.flowOn(Dispatchers.IO)

    override fun getArtistSongs(artistId: String): Flow<kotlin.Result<List<SongModel>>> =
        flow {
            Log.d(
                TAG,
                "getArtistSongs called with ID: $artistId"
            )
            try
            {
                val response =
                    apiService.getArtistSongs(
                        artistId
                    )
                if (response.success)
                {
                    val results =
                        response.data?.results
                            ?: emptyList()
                    Log.d(
                        TAG,
                        "getArtistSongs success: Found ${results.size} songs"
                    )
                    emit(
                        kotlin.Result.success(
                            results
                        )
                    )
                } else
                {
                    Log.e(
                        TAG,
                        "getArtistSongs failed: API returned success=false"
                    )
                    emit(
                        kotlin.Result.failure(
                            Exception("API returned success=false")
                        )
                    )
                }
            } catch (e: Exception)
            {
                Log.e(
                    TAG,
                    "getArtistSongs exception: ${e.message}",
                    e
                )
                emit(kotlin.Result.failure(e))
            }
        }.flowOn(Dispatchers.IO)
}