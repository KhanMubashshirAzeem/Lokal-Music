package com.mubashshir.lokalmusic.ui.screens.home.tab_screen.album

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mubashshir.lokalmusic.data.model.SimpleAlbum
import com.mubashshir.lokalmusic.data.repository.SongRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class AlbumSortOption(val displayName: String) {
    ASCENDING("Ascending"),
    DESCENDING("Descending"),
    ARTIST("Artist"),
    ALBUM("Album"),
    YEAR("Year"),
    DATE_ADDED("Date Added"),
    DATE_MODIFIED("Date Modified")
}

@HiltViewModel
class AlbumsViewModel @Inject constructor(
    private val repository: SongRepository
) : ViewModel() {

    private val _albums = MutableStateFlow<List<SimpleAlbum>>(emptyList())
    val albums = _albums.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _sortOption = MutableStateFlow(AlbumSortOption.DATE_MODIFIED)
    val sortOption = _sortOption.asStateFlow()

    init {
        fetchAlbums()
    }

    fun fetchAlbums(query: String = "popular albums") {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repository.searchAlbums(query)
                .collectLatest { result ->
                    result.onSuccess { albumsList ->
                        _albums.value = albumsList
                        applySort()
                        _isLoading.value = false
                    }.onFailure { exception ->
                        _error.value = exception.message ?: "Failed to load albums"
                        _isLoading.value = false
                    }
                }
        }
    }

    fun setSortOption(option: AlbumSortOption) {
        _sortOption.value = option
        applySort()
    }

    private fun applySort() {
        val sorted = when (_sortOption.value) {
            AlbumSortOption.ASCENDING -> _albums.value.sortedBy { it.name.lowercase() }
            AlbumSortOption.DESCENDING -> _albums.value.sortedByDescending { it.name.lowercase() }
            AlbumSortOption.ARTIST -> _albums.value.sortedBy { 
                it.artists.primary.firstOrNull()?.name?.lowercase() ?: ""
            }
            AlbumSortOption.ALBUM -> _albums.value.sortedBy { it.name.lowercase() }
            AlbumSortOption.YEAR -> _albums.value.sortedByDescending { it.year }
            AlbumSortOption.DATE_ADDED -> _albums.value // API doesn't provide date, keep original order
            AlbumSortOption.DATE_MODIFIED -> _albums.value // API doesn't provide date, keep original order
        }
        _albums.value = sorted
    }
}