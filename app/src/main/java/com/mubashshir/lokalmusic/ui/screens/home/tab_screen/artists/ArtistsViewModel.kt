package com.mubashshir.lokalmusic.ui.screens.home.tab_screen.artists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mubashshir.lokalmusic.data.model.SimpleArtist
import com.mubashshir.lokalmusic.data.repository.SongRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class ArtistSortOption(val displayName: String) {
    ASCENDING("Ascending"),
    DESCENDING("Descending"),
    DATE_ADDED("Date Added"),
    NAME("Name")
}

@HiltViewModel
class ArtistsViewModel @Inject constructor(
    private val repository: SongRepository
) : ViewModel() {

    private val _artists = MutableStateFlow<List<SimpleArtist>>(emptyList())
    val artists = _artists.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _sortOption = MutableStateFlow(ArtistSortOption.DATE_ADDED)
    val sortOption = _sortOption.asStateFlow()

    init {
        fetchArtists()
    }

    fun fetchArtists(query: String = "popular artists") {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repository.searchArtists(query)
                .collectLatest { result ->
                    result.onSuccess { artistsList ->
                        _artists.value = artistsList
                        applySort()
                        _isLoading.value = false
                    }.onFailure { exception ->
                        _error.value = exception.message ?: "Failed to load artists"
                        _isLoading.value = false
                    }
                }
        }
    }

    fun setSortOption(option: ArtistSortOption) {
        _sortOption.value = option
        applySort()
    }

    private fun applySort() {
        val sorted = when (_sortOption.value) {
            ArtistSortOption.ASCENDING -> _artists.value.sortedBy { it.name.lowercase() }
            ArtistSortOption.DESCENDING -> _artists.value.sortedByDescending { it.name.lowercase() }
            ArtistSortOption.DATE_ADDED -> _artists.value // API doesn't provide date, keep original order
            ArtistSortOption.NAME -> _artists.value.sortedBy { it.name.lowercase() }
        }
        _artists.value = sorted
    }
}