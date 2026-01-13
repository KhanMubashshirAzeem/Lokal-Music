package com.mubashshir.lokalmusic.ui.screens.home.tab_screen.songs

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class SongViewModel @Inject constructor(
    private val repository: SongRepository,
    private val playerController: PlayerController
) : ViewModel()
{

    private val _songs =
        MutableStateFlow<List<Result>>(emptyList())
    val songs: StateFlow<List<Result>> =
        _songs.asStateFlow()

    private val _currentSongId =
        MutableStateFlow<String?>(null)
    val currentSongId: StateFlow<String?> =
        _currentSongId.asStateFlow()

    val isPlaying: StateFlow<Boolean> =
        playerController.isPlaying

    init
    {
        // Observe current song from PlayerController
        viewModelScope.launch {
            playerController.currentSong.collectLatest { song ->
                _currentSongId.value = song?.id
            }
        }
    }

    fun searchSongs(query: String)
    {
        viewModelScope.launch {
            repository.searchSongs(query)
                .collectLatest { result ->
                    result.onSuccess {
                        _songs.value = it
                    }.onFailure {
                        // Handle error
                    }
                }
        }
    }

    // UPDATED: Plays the song queue starting from this song to enable Next/Prev
    fun playSong(song: Result)
    {
        val currentList = _songs.value
        val index =
            currentList.indexOfFirst { it.id == song.id }

        if (index != -1)
        {
            // Pass the full list and the specific index to start playing
            playerController.playQueue(
                currentList,
                index
            )
        } else
        {
            // Fallback if song not in list (unlikely in this screen)
            playerController.playSong(song)
        }
    }

    fun playAllSongs()
    {
        if (_songs.value.isNotEmpty())
        {
            playerController.playQueue(
                _songs.value,
                0
            )
        }
    }
}