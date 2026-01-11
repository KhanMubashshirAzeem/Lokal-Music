
package com.mubashshir.lokalmusic.ui.screens.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class HorizontalItem(
    val title: String,
    val subtitle: String,
    val imageUrl: String
)

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _recentlyPlayed = MutableStateFlow<List<HorizontalItem>>(emptyList())
    val recentlyPlayed = _recentlyPlayed.asStateFlow()

    private val _artists = MutableStateFlow<List<HorizontalItem>>(emptyList())
    val artists = _artists.asStateFlow()

    private val _mostPlayed = MutableStateFlow<List<HorizontalItem>>(emptyList())
    val mostPlayed = _mostPlayed.asStateFlow()

    init {
        loadDummyData()
    }

    private fun loadDummyData() {
        _recentlyPlayed.value = listOf(
            HorizontalItem(
                "Shades of Love - Ania Szarmach..",
                "Ania Szarmach",
                "https://example.com/shades.jpg" // placeholder - use real URLs later
            ),
            HorizontalItem(
                "Without You - The Kid LAROI",
                "The Kid LAROI",
                "https://example.com/nova.jpg"
            ),
            HorizontalItem(
                "Save Your Tears The Weeknd",
                "The Weeknd",
                "https://example.com/astronaut.jpg"
            )
        )

        _artists.value = listOf(
            HorizontalItem("Ariana Grande", "DANGEROUS WOMAN", "https://example.com/ariana.jpg"),
            HorizontalItem("The Weeknd", "STARBOY", "https://example.com/weeknd.jpg"),
            HorizontalItem("Acidrap", "", "https://example.com/acidrap.jpg")
        )

        _mostPlayed.value = listOf(
            HorizontalItem("Ania", "Thinking Bout You", "https://example.com/ania.jpg"),
            HorizontalItem("The Weeknd", "Dawn FM", "https://example.com/dawn.jpg"),
            HorizontalItem("Romantic Echoes", "Fly Me To The Sun", "https://example.com/romantic.jpg")
        )
    }
}