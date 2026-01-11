package com.mubashshir.lokalmusic.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mubashshir.lokalmusic.UiState
import com.mubashshir.lokalmusic.data.model.Result
import com.mubashshir.lokalmusic.data.model.SimpleArtist
import com.mubashshir.lokalmusic.data.repository.SongRepository
import com.mubashshir.lokalmusic.player.PlayerController
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
    val mostPlayed: List<HorizontalItem>,
    val recentlyPlayed: List<HorizontalItem>
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: SongRepository,
    private val playerController: PlayerController // 1. Inject PlayerController
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<HomeData>>(UiState.Loading)
    val uiState: StateFlow<UiState<HomeData>> = _uiState.asStateFlow()

    // 2. Local cache to store full Song objects so we can play them by ID
    private val songCache =
        mutableMapOf<String, Result>()

    init {
        fetchData()
    }

    private fun fetchData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            try {
                val artistsDeferred = async {
                    repository.searchArtists("top artists")
                        .first()
                }
                val songsDeferred = async {
                    repository.searchSongs("top songs")
                        .first()
                }
                val recentlyDeferred = async {
                    repository.searchSongs("recently played")
                        .first()
                }

                val artistsResult = artistsDeferred.await()
                val songsResult = songsDeferred.await()
                val recentlyResult =
                    recentlyDeferred.await()

                if (artistsResult.isSuccess && songsResult.isSuccess)
                {

                    val artists =
                        artistsResult.getOrNull()
                            ?.map {
                                mapArtistToHorizontalItem(
                                    it
                                )
                            } ?: emptyList()

                    // Process Top Songs
                    val mostPlayed =
                        songsResult.getOrNull()
                            ?.map { song ->
                                songCache[song.id] =
                                    song // Cache the song
                                mapSongToHorizontalItem(
                                    song
                                )
                            } ?: emptyList()

                    // Process Recently Played
                    val recentlyPlayed =
                        recentlyResult.getOrNull()
                            ?.map { song ->
                                songCache[song.id] =
                                    song // Cache the song
                                mapSongToHorizontalItem(
                                    song
                                )
                            } ?: emptyList()

                    _uiState.value =
                        UiState.Success(
                            HomeData(
                                artists = artists,
                                mostPlayed = mostPlayed,
                                recentlyPlayed = recentlyPlayed
                        )
                        )
                } else
                {
                    val errorMessage =
                        artistsResult.exceptionOrNull()?.message
                            ?: songsResult.exceptionOrNull()?.message
                            ?: "Failed to load data"
                    _uiState.value =
                        UiState.Error(errorMessage)
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to load data")
            }
        }
    }

    // 3. New function to play song by ID
    fun playSong(songId: String)
    {
        val song = songCache[songId]
        if (song != null)
        {
            playerController.playSong(song)
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