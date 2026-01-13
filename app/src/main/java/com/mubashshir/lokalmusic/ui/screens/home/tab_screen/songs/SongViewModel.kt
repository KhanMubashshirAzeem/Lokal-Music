package com.mubashshir.lokalmusic.ui.screens.home.tab_screen.songs

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mubashshir.lokalmusic.data.model.Results
import com.mubashshir.lokalmusic.data.repository.SongRepository
import com.mubashshir.lokalmusic.data.repository.SongRepositoryImpl
import com.mubashshir.lokalmusic.player.PlayerController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class SongViewModel @Inject constructor(
    private val repository: SongRepository,
    private val playerController: PlayerController
) : ViewModel()
{

    private val _query = MutableStateFlow("arijit")

    val songs: Flow<PagingData<Results>> =
        _query
            .flatMapLatest { query ->
                if (repository is SongRepositoryImpl)
                {
                    repository.searchSongsPaged(query)
                } else
                {
                    flowOf(PagingData.empty())
                }
            }
            .cachedIn(viewModelScope)

    private val _currentSongId = MutableStateFlow<String?>(null)
    val currentSongId: StateFlow<String?> =
        _currentSongId.asStateFlow()

    val isPlaying: StateFlow<Boolean> = playerController.isPlaying

    init
    {
        viewModelScope.launch {
            playerController.currentSong.collectLatest { song ->
                _currentSongId.value = song?.id
            }
        }
    }

    fun searchSongs(query: String)
    {
        _query.value = query
    }

    fun playSong(song: Results)
    {
        playerController.playSong(song)
    }
}
