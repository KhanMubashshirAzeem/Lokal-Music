package com.mubashshir.lokalmusic.ui.screens.search

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mubashshir.lokalmusic.data.model.Result
import com.mubashshir.lokalmusic.data.repository.SongRepository
import com.mubashshir.lokalmusic.player.PlayerController
import com.mubashshir.lokalmusic.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: SongRepository,
    private val playerController: PlayerController
) : ViewModel()
{

    // Using a StateFlow to track the query reactively
    private val _searchQueryFlow = MutableStateFlow("")

    // Keep this for the SearchBar UI component to read
    var searchQuery by mutableStateOf("")
        private set

    private val _uiState = MutableStateFlow<UiState<List<Result>>>(
        UiState.Success(emptyList())
    )
    val uiState: StateFlow<UiState<List<Result>>> =
        _uiState.asStateFlow()

    private val _currentSongId = MutableStateFlow<String?>(null)
    val currentSongId: StateFlow<String?> =
        _currentSongId.asStateFlow()
    val isPlaying: StateFlow<Boolean> = playerController.isPlaying

    init
    {
        // Observe player state
        viewModelScope.launch {
            playerController.currentSong.collectLatest { song ->
                _currentSongId.value = song?.id
            }
        }

        // Reactive Search Pipeline
        viewModelScope.launch {
            _searchQueryFlow
                .debounce(450) // Wait for user to stop typing
                .filter { query ->
                    if (query.isEmpty())
                    {
                        _uiState.value = UiState.Success(emptyList())
                        return@filter false
                    }
                    true
                }
                .distinctUntilChanged() // Don't search if query hasn't changed
                .flatMapLatest { query ->
                    _uiState.value = UiState.Loading
                    repository.searchSongs(query)
                }
                .flowOn(Dispatchers.IO)
                .collect { result ->
                    result.onSuccess { songs ->
                        _uiState.value = UiState.Success(songs)
                    }.onFailure { exception ->
                        _uiState.value = UiState.Error(
                            exception.message ?: "Search failed"
                        )
                    }
                }
        }
    }

    fun search(query: String)
    {
        searchQuery = query // Updates the UI text field immediately
        _searchQueryFlow.value =
            query // Triggers the reactive pipeline
    }

    fun clearSearch()
    {
        searchQuery = ""
        _searchQueryFlow.value = ""
        _uiState.value = UiState.Success(emptyList())
    }

    fun playSong(song: Result)
    {
        playerController.playSong(song)
    }
}