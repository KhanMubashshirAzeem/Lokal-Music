package com.mubashshir.lokalmusic.ui.screens.home.tab_screen.artists

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.mubashshir.lokalmusic.R
import com.mubashshir.lokalmusic.data.model.SimpleArtist
import com.mubashshir.lokalmusic.ui.theme.PaddingMedium
import com.mubashshir.lokalmusic.ui.theme.PaddingSmall
import com.mubashshir.lokalmusic.ui.theme.PrimaryOrange

@RequiresApi(Build.VERSION_CODES.O)

@Composable
fun ArtistsContent(
    viewModel: ArtistsViewModel = hiltViewModel(),
    onNavigateToArtist: (String) -> Unit = {}
) {
    val artists by viewModel.artists.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val sortOption by viewModel.sortOption.collectAsState()

    var showSortMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ) {
        // Header with count and sort
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = PaddingMedium,
                    vertical = PaddingSmall
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${artists.size} artists",
                style = MaterialTheme.typography.titleMedium
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { showSortMenu = true }
            ) {
                Text(
                    text = sortOption.displayName,
                    color = PrimaryOrange,
                    style = MaterialTheme.typography.bodyMedium
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = PrimaryOrange
                )
            }

            DropdownMenu(
                expanded = showSortMenu,
                onDismissRequest = { showSortMenu = false }
            ) {
                ArtistSortOption.entries.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.displayName) },
                        onClick = {
                            viewModel.setSortOption(option)
                            showSortMenu = false
                        },
                        leadingIcon = if (sortOption == option) {
                            {
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    tint = PrimaryOrange
                                )
                            }
                        } else null
                    )
                }
            }
        }

        // Content
        when {
            isLoading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Loading artists...")
                }
            }
            error != null -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Error: $error")
                }
            }
            artists.isEmpty() -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("No artists found")
                }
            }
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = PaddingMedium)
                ) {
                    items(artists) { artist ->
//                        // We are getting the
//                        // artist id correctly
//                        Log.d(
//                            "Artist_id", artist.id
//                        )
                        ArtistItem(
                            artist = artist,
                            onClick = {
                                onNavigateToArtist(
                                    artist.id
                                )
                            }
                        )
                        Spacer(modifier = Modifier.height(PaddingMedium))
                    }
                    item {
                        Spacer(modifier = Modifier.height(100.dp)) // Space for mini player
                    }
                }
            }
        }
    }
}

@Composable
private fun ArtistItem(
    artist: SimpleArtist,
    onClick: () -> Unit = {}
)
{
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = artist.image.find { it.quality == "500x500" }?.url
                ?: artist.image.firstOrNull()?.url
                ?: "",
            contentDescription = artist.name,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.ic_place_holder),
            error = painterResource(R.drawable.ic_place_holder)
        )

        Spacer(modifier = Modifier.width(PaddingMedium))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = artist.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = artist.role.ifEmpty { "Artist" },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = "More options",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .padding(start = PaddingSmall)
                .clickable { /* Show menu */ }
        )
    }
}