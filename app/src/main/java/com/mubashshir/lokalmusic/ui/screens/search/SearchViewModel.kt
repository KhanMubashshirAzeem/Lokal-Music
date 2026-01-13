package com.mubashshir.lokalmusic.ui.screens.search

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mubashshir.lokalmusic.data.model.Results
import com.mubashshir.lokalmusic.data.repository.SongRepository
import com.mubashshir.lokalmusic.data.repository.SongRepositoryImpl
import com.mubashshir.lokalmusic.player.PlayerController
import com.mubashshir.lokalmusic.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: SongRepository,
    private val playerController: PlayerController
) : ViewModel() {

    private val _searchQueryFlow = MutableStateFlow("")

    var searchQuery by mutableStateOf("")
        private set

    private val _uiState =
        MutableStateFlow<UiState<List<Results>>>(UiState.Success(emptyList()))
    val uiState: StateFlow<UiState<List<Results>>> = _uiState.asStateFlow()

    private val _currentSongId = MutableStateFlow<String?>(null)
    val currentSongId: StateFlow<String?> = _currentSongId.asStateFlow()

    val isPlaying: StateFlow<Boolean> = playerController.isPlaying

    val pagedSongs: StateFlow<PagingData<Results>> =
        _searchQueryFlow
            .debounce(450)
            .filter { it.isNotEmpty() }
            .distinctUntilChanged()
            .flatMapLatest { query ->
                if (repository is SongRepositoryImpl) {
                    repository.searchSongsPaged(query)
                } else {
                    flowOf(PagingData.empty())
                }
            }
            .cachedIn(viewModelScope)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = PagingData.empty()
            )

    init {
        viewModelScope.launch {
            playerController.currentSong.collectLatest { song ->
                _currentSongId.value = song?.id
            }
        }

        viewModelScope.launch {
            _searchQueryFlow
                .debounce(450)
                .filter { query ->
                    if (query.isEmpty()) {
                        _uiState.value = UiState.Success(emptyList())
                        false
                    } else {
                        true
                    }
                }
                .distinctUntilChanged()
                .flatMapLatest { query ->
                    _uiState.value = UiState.Loading
                    repository.searchSongs(query)
                }
                .flowOn(Dispatchers.IO)
                .collect { result ->
                    result.onSuccess { songs ->
                        _uiState.value = UiState.Success(songs)
                    }.onFailure { exception ->
                        _uiState.value =
                            UiState.Error(exception.message ?: "Search failed")
                    }
                }
        }
    }

    fun search(query: String) {
        searchQuery = query
        _searchQueryFlow.value = query
    }

    fun clearSearch() {
        searchQuery = ""
        _searchQueryFlow.value = ""
        _uiState.value = UiState.Success(emptyList())
    }

    fun playSong(song: Results) {
        playerController.playSong(song)
    }
}
