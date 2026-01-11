package com.mubashshir.lokalmusic.data.repository

// Import your custom Result model with an alias to avoid conflict with kotlin.Result
import kotlinx.coroutines.flow.Flow
import com.mubashshir.lokalmusic.data.model.Result as SongModel

interface SongRepository
{
    // Change List<Data> to List<SongModel>
    fun searchSongs(query: String): Flow<kotlin.Result<List<SongModel>>>
}