package com.mubashshir.lokalmusic.ui.screens.album

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mubashshir.lokalmusic.util.UiState
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

data class AlbumDetailData(
    val albumName: String,
    val artistName: String,
    val albumImageUrl: String,
    val year: String,
    val songs: List<Result>
)

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class AlbumDetailViewModel @Inject constructor(
    private val repository: SongRepository,
    private val playerController: PlayerController
) : ViewModel()
{

    private val _uiState =
        MutableStateFlow<UiState<AlbumDetailData>>(
            UiState.Loading
        )
    val uiState: StateFlow<UiState<AlbumDetailData>> =
        _uiState.asStateFlow()

    private val _currentSongId =
        MutableStateFlow<String?>(null)
    val currentSongId: StateFlow<String?> =
        _currentSongId.asStateFlow()

    val isPlaying: StateFlow<Boolean> =
        playerController.isPlaying

    private var albumSongs: List<Result> =
        emptyList()

    init
    {
        viewModelScope.launch {
            playerController.currentSong.collectLatest { song ->
                _currentSongId.value = song?.id
            }
        }
    }

    fun loadAlbum(albumId: String)
    {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            repository.getAlbum(albumId)
                .collectLatest { result ->
                    result.onSuccess { albumResponse ->
                        val album =
                            albumResponse.data
                        if (album != null && album.songs.isNotEmpty())
                        {
                            albumSongs =
                                album.songs
                            val firstSong =
                                album.songs.first()

                            _uiState.value =
                                UiState.Success(
                                    AlbumDetailData(
                                        albumName = album.name,
                                        artistName = firstSong.artists.primary.firstOrNull()?.name
                                            ?: "Unknown Artist",
                                        albumImageUrl = album.image.find { it.quality == "500x500" }?.url
                                            ?: "",
                                        year = album.year
                                            ?: "",
                                        songs = album.songs
                                    )
                                )
                        } else
                        {
                            _uiState.value =
                                UiState.Error("Album not found or empty")
                        }
                    }.onFailure {
                        _uiState.value =
                            UiState.Error(
                                it.message
                                    ?: "Failed to load album"
                            )
                    }
                }
        }
    }

    fun playSong(song: Result)
    {
        playerController.playSong(song)
    }

    fun playAlbum()
    {
        if (albumSongs.isNotEmpty())
        {
            playerController.playQueue(
                albumSongs,
                0
            )
        }
    }

    fun shuffleAlbum()
    {
        if (albumSongs.isNotEmpty())
        {
            playerController.playQueue(
                albumSongs.shuffled(),
                0
            )
        }
    }
}