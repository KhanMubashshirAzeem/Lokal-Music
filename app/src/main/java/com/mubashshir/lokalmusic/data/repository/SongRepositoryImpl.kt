package com.mubashshir.lokalmusic.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mubashshir.lokalmusic.data.model.AlbumResponse
import com.mubashshir.lokalmusic.data.model.Results
import com.mubashshir.lokalmusic.data.model.SimpleAlbum
import com.mubashshir.lokalmusic.data.model.SimpleArtist
import com.mubashshir.lokalmusic.data.paging.SongPagingSource
import com.mubashshir.lokalmusic.data.remote.SongApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class SongRepositoryImpl @Inject constructor(
    private val apiService: SongApiService
) : SongRepository
{

    override fun searchSongs(
        query: String,
        name: String?,
    ): Flow<Result<List<Results>>> = flow {
        try
        {
            val response = apiService.searchSongs(query, name)
            if (response.success)
            {
                emit(Result.success(response.data.results))
            } else
            {
                emit(Result.failure(Exception("API returned success=false")))
            }
        } catch (e: Exception)
        {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    fun searchSongsPaged(
        query: String,
        name: String? = null,
        pageSize: Int = 20
    ): Flow<PagingData<Results>>
    {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                SongPagingSource(
                    songApiService = apiService,
                    query = query,
                    name = name
                )
            }
        ).flow
    }

    override fun getAlbum(
        albumId: String
    ): Flow<Result<AlbumResponse>> = flow {
            try
            {
                val response = apiService.getAlbum(albumId)
                if (response.success && response.data != null)
                {
                    emit(Result.success(response))
                } else
                {
                    emit(Result.failure(Exception("Album not found or invalid response")))
                }
            } catch (e: Exception)
            {
                emit(Result.failure(e))
            }
        }.flowOn(Dispatchers.IO)

    override fun searchArtists(
        query: String,
    ): Flow<Result<List<SimpleArtist>>> = flow {
            try
            {
                val response = apiService.searchArtists(query)
                if (response.success)
                {
                    emit(Result.success(response.data.results))
                } else
                {
                    emit(Result.failure(Exception("API returned success=false")))
                }
            } catch (e: Exception)
            {
                emit(Result.failure(e))
            }
        }.flowOn(Dispatchers.IO)

    override fun searchAlbums(
        query: String,
    ): Flow<Result<List<SimpleAlbum>>> = flow {
            try
            {
                val response = apiService.searchAlbums(query)
                if (response.success)
                {
                    emit(Result.success(response.data.results))
                } else
                {
                    emit(Result.failure(Exception("API returned success=false")))
                }
            } catch (e: Exception)
            {
                emit(Result.failure(e))
            }
        }.flowOn(Dispatchers.IO)
}
