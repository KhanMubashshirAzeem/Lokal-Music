// ui/screens/artists/ArtistsScreen.kt (new)
package com.mubashshir.lokalmusic.ui.screens.artist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

// Dummy artists
val artistList = listOf(
    "Ariana Grande" to "1 Album | 20 Songs",
    "The Weeknd" to "1 Album | 16 Songs",
    // more
)

@Composable
fun ArtistsScreen()
{
    LazyColumn {
        items(artistList) { artist ->
            Row {
                // Artist image
                Column {
                    Text(artist.first)
                    Text(artist.second)
                }
            }
        }
    }
}