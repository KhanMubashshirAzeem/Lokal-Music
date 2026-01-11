package com.mubashshir.lokalmusic.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mubashshir.lokalmusic.data.model.Result
import com.mubashshir.lokalmusic.data.model.SimpleArtist
import com.mubashshir.lokalmusic.data.repository.SongRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HorizontalItem(
    val title: String,
    val subtitle: String,
    val imageUrl: String
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: SongRepository
) : ViewModel()
{

    private val _recentlyPlayed = MutableStateFlow<List<HorizontalItem>>(emptyList())
    val recentlyPlayed = _recentlyPlayed.asStateFlow()

    private val _artists = MutableStateFlow<List<HorizontalItem>>(emptyList())
    val artists = _artists.asStateFlow()

    private val _mostPlayed = MutableStateFlow<List<HorizontalItem>>(emptyList())
    val mostPlayed = _mostPlayed.asStateFlow()

    init {
        fetchRecentlyPlayed()
        fetchArtists()
        fetchMostPlayed()
    }

    private fun fetchRecentlyPlayed()
    {
        viewModelScope.launch {
            repository.searchSongs("new songs")
                .collect { result ->
                    result.onSuccess { songs ->
                        _recentlyPlayed.value =
                            songs.map {
                                mapSongToHorizontalItem(
                                    it
                                )
                            }
                    }
                }
        }
    }

    private fun fetchArtists()
    {
        viewModelScope.launch {
            repository.searchArtists("top artists")
                .collect { result ->
                    result.onSuccess { artistsList ->
                        _artists.value =
                            artistsList.map {
                                mapArtistToHorizontalItem(
                                    it
                                )
                            }
                    }
                }
        }
    }

    private fun fetchMostPlayed()
    {
        viewModelScope.launch {
            repository.searchSongs("top songs")
                .collect { result ->
                    result.onSuccess { songs ->
                        _mostPlayed.value =
                            songs.map {
                                mapSongToHorizontalItem(
                                    it
                                )
                            }
                    }
                }
        }
    }

    private fun mapSongToHorizontalItem(song: Result): HorizontalItem
    {
        return HorizontalItem(
            title = song.name,
            subtitle = song.artists.primary.firstOrNull()?.name
                ?: song.label,
            imageUrl = song.image.find { it.quality == "500x500" }?.url
                ?: song.image.lastOrNull()?.url
                ?: ""
        )
    }

    private fun mapArtistToHorizontalItem(artist: SimpleArtist): HorizontalItem
    {
        return HorizontalItem(
            title = artist.name,
            subtitle = artist.role,
            imageUrl = artist.image.find { it.quality == "500x500" }?.url
                ?: artist.image.lastOrNull()?.url
                ?: ""
        )
    }
}