package com.mubashshir.lokalmusic.ui.screens.search

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

@RequiresApi(Build.VERSION_CODES.O)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: SongRepository,
    private val playerController: PlayerController
) : ViewModel()
{

    var searchQuery = ""
        private set

    private val _uiState =
        MutableStateFlow<UiState<List<Result>>>(
            UiState.Loading
        )
    val uiState: StateFlow<UiState<List<Result>>> =
        _uiState.asStateFlow()

    private val _currentSongId =
        MutableStateFlow<String?>(null)
    val currentSongId: StateFlow<String?> =
        _currentSongId.asStateFlow()
    val isPlaying: StateFlow<Boolean> =
        playerController.isPlaying

    init
    {
        // Observe player state
        viewModelScope.launch {
            playerController.currentSong.collectLatest { song ->
                _currentSongId.value = song?.id
            }
        }
    }

    fun search(query: String)
    {
        if (query == searchQuery) return

        searchQuery = query
        if (query.isEmpty())
        {
            _uiState.value =
                UiState.Success(emptyList())
            return
        }

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.searchSongs(query)
                .collectLatest { result ->
                    result.onSuccess { songs ->
                        _uiState.value =
                            UiState.Success(songs)
                    }.onFailure { exception ->
                        _uiState.value =
                            UiState.Error(
                                exception.message
                                    ?: "Search failed"
                            )
                    }
                }
        }
    }

    fun clearSearch()
    {
        searchQuery = ""
        _uiState.value =
            UiState.Success(emptyList())
    }

    fun playSong(song: Result)
    {
        playerController.playSong(song)
    }
}