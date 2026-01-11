package com.mubashshir.lokalmusic.ui.screens.artist

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mubashshir.lokalmusic.UiState
import com.mubashshir.lokalmusic.data.model.Result
import com.mubashshir.lokalmusic.data.repository.SongRepository
import com.mubashshir.lokalmusic.player.PlayerController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ArtistDetailData(
    val artistName: String,
    val artistImageUrl: String,
    val songs: List<Result>,
    val songCount: Int
)

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class ArtistDetailViewModel @Inject constructor(
    private val repository: SongRepository,
    private val playerController: PlayerController
) : ViewModel()
{

    private val _uiState =
        MutableStateFlow<UiState<ArtistDetailData>>(
            UiState.Loading
        )
    val uiState: StateFlow<UiState<ArtistDetailData>> =
        _uiState.asStateFlow()

    private val _currentSongId =
        MutableStateFlow<String?>(null)
    val currentSongId: StateFlow<String?> =
        _currentSongId.asStateFlow()

    val isPlaying: StateFlow<Boolean> =
        playerController.isPlaying

    init
    {
        viewModelScope.launch {
            playerController.currentSong.collectLatest { song ->
                _currentSongId.value = song?.id
            }
        }
    }

    fun loadArtist(artistId: String)
    {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            repository.getArtistSongs(artistId)
                .collectLatest { result ->
                    result.onSuccess { songs ->
                        if (songs.isNotEmpty())
                        {
                            val firstSong =
                                songs.first()
                            _uiState.value =
                                UiState.Success(
                                    ArtistDetailData(
                                        artistName = firstSong.artists.primary.firstOrNull()?.name
                                            ?: "Unknown Artist",
                                        artistImageUrl = firstSong.image.find { it.quality == "500x500" }?.url
                                            ?: "",
                                        songs = songs,
                                        songCount = songs.size
                                    )
                                )
                        } else
                        {
                            _uiState.value =
                                UiState.Error("No songs found for this artist")
                        }
                    }.onFailure {
                        _uiState.value =
                            UiState.Error(
                                it.message
                                    ?: "Failed to load artist"
                            )
                    }
                }
        }
    }

    fun playSong(song: Result)
    {
        playerController.playSong(song)
    }
}