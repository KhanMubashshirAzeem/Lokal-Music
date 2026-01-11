package com.mubashshir.lokalmusic.data.repository

import com.mubashshir.lokalmusic.data.remote.SongApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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

                // response.data.results is the List of your custom Result model
                val songList =
                    response.data.results

                if (response.success)
                {
                    // Use kotlin.Result.success to wrap your list of custom models
                    emit(
                        kotlin.Result.success(
                            songList
                        )
                    )
                } else
                {
                    emit(
                        kotlin.Result.failure(
                            Exception("No songs found")
                        )
                    )
                }
            } catch (e: Exception)
            {
                emit(kotlin.Result.failure(e))
            }
        }
}