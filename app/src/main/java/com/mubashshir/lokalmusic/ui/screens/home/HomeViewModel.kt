package com.mubashshir.lokalmusic.ui.screens.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mubashshir.lokalmusic.data.model.Results
import com.mubashshir.lokalmusic.data.model.SimpleArtist
import com.mubashshir.lokalmusic.data.repository.SongRepository
import com.mubashshir.lokalmusic.player.PlayerController
import com.mubashshir.lokalmusic.ui.screens.base.BaseViewModel
import com.mubashshir.lokalmusic.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
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
    val recentlyPlayed: List<HorizontalItem>,
    val mostPlayedSongs: List<Results> = emptyList(),
    val recentlyPlayedSongs: List<Results> = emptyList()
)

@RequiresApi(Build.VERSION_CODES.O)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: SongRepository,
    private val playerController: PlayerController
) : BaseViewModel() {

    private val _uiState = MutableStateFlow<UiState<HomeData>>(UiState.Loading)
    val uiState: StateFlow<UiState<HomeData>> = _uiState.asStateFlow()

    private val _currentSongId =
        MutableStateFlow<String?>(null)
    val currentSongId: StateFlow<String?> =
        _currentSongId.asStateFlow()

    val isPlaying = playerController.isPlaying

    init {
        fetchData()

        // Observe current song to highlight in lists
        viewModelScope.launch {
            playerController.currentSong.collectLatest { song ->
                _currentSongId.value = song?.id
            }
        }
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
                    repository.searchSongs(
                        "?",
                        "recently played"
                    ).first()
                }

                val mostDeferred = async {
                    repository.searchSongs("most played")
                        .first()
                }

                val artistsResult = artistsDeferred.await()
                val songsResult = songsDeferred.await()
                val recentlyResult =
                    recentlyDeferred.await()
                val mostResult =
                    mostDeferred.await()

                if (artistsResult.isSuccess && songsResult.isSuccess)
                {

                    val artists =
                        artistsResult.getOrNull()
                            ?.map {
                                mapArtistToHorizontalItem(
                                    it
                                )
                            } ?: emptyList()

                    // Store full Result objects
                    val mostPlayedFull =
                        mostResult.getOrNull()
                            ?: emptyList()
                    val recentlyPlayedFull =
                        recentlyResult.getOrNull()
                            ?: emptyList()

                    // Map to HorizontalItems for the Carousel
                    val mostPlayedItems =
                        mostPlayedFull.map {
                            mapSongToHorizontalItem(
                                it
                            )
                        }
                    val recentlyPlayedItems =
                        recentlyPlayedFull.map {
                            mapSongToHorizontalItem(
                                it
                            )
                        }

                    _uiState.value =
                        UiState.Success(
                            HomeData(
                                artists = artists,
                                mostPlayed = mostPlayedItems,
                                recentlyPlayed = recentlyPlayedItems,
                                mostPlayedSongs = mostPlayedFull,      // Pass full list
                                recentlyPlayedSongs = recentlyPlayedFull // Pass full list
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
                handleThrowable(e)
                _uiState.value = UiState.Error(e.message ?: "Failed to load data")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun playSong(songId: String)
    {
        val state = _uiState.value
        if (state is UiState.Success)
        {
            val song =
                state.data.mostPlayedSongs.find { it.id == songId }
                    ?: state.data.recentlyPlayedSongs.find { it.id == songId }

            if (song != null)
            {
                playerController.playSong(song)
            }
        }
    }

    // Play from the "See All" list (supports Next/Previous)
    @RequiresApi(Build.VERSION_CODES.O)
    fun playTrackList(
        songs: List<Results>,
        selectedSong: Results
    )
    {
        val index =
            songs.indexOfFirst { it.id == selectedSong.id }
        if (index != -1)
        {
            playerController.playQueue(
                songs,
                index
            )
        }
    }

    private fun mapSongToHorizontalItem(song: Results): HorizontalItem {
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