package com.mubashshir.lokalmusic.ui.screens.artist

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
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<ArtistDetailData>>(UiState.Loading)
    val uiState: StateFlow<UiState<ArtistDetailData>> = _uiState.asStateFlow()

    private val _currentSongId = MutableStateFlow<String?>(null)
    val currentSongId: StateFlow<String?> = _currentSongId.asStateFlow()

    val isPlaying: StateFlow<Boolean> = playerController.isPlaying

    init {
        viewModelScope.launch {
            playerController.currentSong.collectLatest { song ->
                _currentSongId.value = song?.id
            }
        }
    }

    // CHANGED: The argument is now treated as a 'query' (Artist Name)
    fun loadArtist(query: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            // CHANGED: Use searchSongs with the query (Name) instead of getArtistSongs(ID)
            repository.searchSongs(query)
                .collectLatest { result ->
                    result.onSuccess { songs ->
                        if (songs.isNotEmpty()) {
                            // We extract artist info from the first song in the result
                            val firstSong = songs.first()

                            // Try to find the exact artist match in the song's artists, otherwise default to first
                            val artistObj = firstSong.artists.primary.find {
                                it.name.equals(query, ignoreCase = true)
                            } ?: firstSong.artists.primary.firstOrNull()

                            _uiState.value = UiState.Success(
                                ArtistDetailData(
                                    artistName = artistObj?.name ?: query,
                                    artistImageUrl = firstSong.image.find { it.quality == "500x500" }?.url
                                        ?: firstSong.image.firstOrNull()?.url ?: "",
                                    songs = songs,
                                    songCount = songs.size
                                )
                            )
                        } else {
                            _uiState.value = UiState.Error("No songs found for '$query'")
                        }
                    }.onFailure {
                        _uiState.value = UiState.Error(it.message ?: "Failed to load artist")
                    }
                }
        }
    }
    // UPDATED: Plays the specific song but includes the whole list in the queue
    fun playSong(song: Result) {
        val currentState = _uiState.value
        if (currentState is UiState.Success) {
            val songs = currentState.data.songs
            val index = songs.indexOfFirst { it.id == song.id }
            if (index != -1) {
                playerController.playQueue(songs, index)
            }
        }
    }

    // NEW: Plays all songs from the start
    fun playArtist() {
        val currentState = _uiState.value
        if (currentState is UiState.Success) {
            val songs = currentState.data.songs
            if (songs.isNotEmpty()) {
                playerController.playQueue(songs, 0)
            }
        }
    }

    // NEW: Shuffles the songs and plays
    fun shuffleArtist() {
        val currentState = _uiState.value
        if (currentState is UiState.Success) {
            val songs = currentState.data.songs
            if (songs.isNotEmpty()) {
                playerController.playQueue(songs.shuffled(), 0)
            }
        }
    }
}