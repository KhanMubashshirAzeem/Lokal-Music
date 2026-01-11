package com.mubashshir.lokalmusic.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mubashshir.lokalmusic.UiState
import com.mubashshir.lokalmusic.data.model.SimpleArtist
import com.mubashshir.lokalmusic.data.model.Result
import com.mubashshir.lokalmusic.data.repository.SongRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HorizontalItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val imageUrl: String
)

data class HomeData(
    val artists: List<HorizontalItem>,
    val mostPlayed: List<HorizontalItem>
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: SongRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<HomeData>>(UiState.Loading)
    val uiState: StateFlow<UiState<HomeData>> = _uiState.asStateFlow()

    init {
        fetchData()
    }

    private fun fetchData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            
            try {
                // Fetch both in parallel
                val artistsDeferred = async {
                    repository.searchArtists("top artists").first()
                }
                val songsDeferred = async {
                    repository.searchSongs("top songs").first()
                }
                
                val artistsResult = artistsDeferred.await()
                val songsResult = songsDeferred.await()
                
                when {
                    artistsResult.isSuccess && songsResult.isSuccess -> {
                        val artists = artistsResult.getOrNull()?.map { 
                            mapArtistToHorizontalItem(it) 
                        } ?: emptyList()
                        val mostPlayed = songsResult.getOrNull()?.map { 
                            mapSongToHorizontalItem(it) 
                        } ?: emptyList()
                        
                        _uiState.value = UiState.Success(
                            HomeData(artists = artists, mostPlayed = mostPlayed)
                        )
                    }
                    artistsResult.isFailure -> {
                        _uiState.value = UiState.Error(
                            artistsResult.exceptionOrNull()?.message ?: "Failed to load artists"
                        )
                    }
                    songsResult.isFailure -> {
                        _uiState.value = UiState.Error(
                            songsResult.exceptionOrNull()?.message ?: "Failed to load songs"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to load data")
            }
        }
    }

    private fun mapSongToHorizontalItem(song: Result): HorizontalItem {
        return HorizontalItem(
            id = song.id,
            title = song.name,
            subtitle = song.artists.primary.firstOrNull()?.name ?: song.label,
            imageUrl = song.image.find { it.quality == "500x500" }?.url
                ?: song.image.firstOrNull()?.url
                ?: ""
        )
    }

    private fun mapArtistToHorizontalItem(artist: SimpleArtist): HorizontalItem {
        return HorizontalItem(
            id = artist.id,
            title = artist.name,
            subtitle = artist.role.ifEmpty { "Artist" },
            imageUrl = artist.image.find { it.quality == "500x500" }?.url
                ?: artist.image.firstOrNull()?.url
                ?: ""
        )
    }
}