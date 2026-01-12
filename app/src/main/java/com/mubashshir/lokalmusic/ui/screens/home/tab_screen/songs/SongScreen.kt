package com.mubashshir.lokalmusic.ui.screens.home.tab_screen.songs

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.mubashshir.lokalmusic.data.model.Result
import com.mubashshir.lokalmusic.ui.theme.PaddingMedium
import com.mubashshir.lokalmusic.ui.theme.PaddingSmall
import com.mubashshir.lokalmusic.ui.theme.PrimaryOrange

enum class SortOption(val displayName: String) {
    ASCENDING("Ascending"),
    DESCENDING("Descending"),
    ARTIST("Artist"),
    ALBUM("Album"),
    YEAR("Year"),
    DATE_ADDED("Date Added"),
    DATE_MODIFIED("Date Modified"),
    COMPOSER("Composer")
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SongScreen(
    viewModel: SongViewModel = hiltViewModel()
) {
    val songs by viewModel.songs.collectAsState()
    val currentSongId by viewModel.currentSongId.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()

    var currentSort by remember { mutableStateOf(SortOption.ASCENDING) }
    var showSortMenu by remember { mutableStateOf(false) }

    // Load songs on first composition
    LaunchedEffect(Unit) {
        viewModel.searchSongs("arijit")
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Sort Row
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
                text = "${songs.size} songs",
                style = MaterialTheme.typography.titleMedium
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    showSortMenu = true
                }
            ) {
                Text(
                    text = currentSort.displayName,
                    color = PrimaryOrange
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = PrimaryOrange
                )
            }

            DropdownMenu(
                expanded = showSortMenu,
                onDismissRequest = {
                    showSortMenu = false
                }
            ) {
                SortOption.entries.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.displayName) },
                        onClick = {
                            currentSort = option
                            showSortMenu = false
                        }
                    )
                }
            }
        }

        // Song List
        LazyColumn(
            contentPadding = PaddingValues(
                PaddingMedium
            )
        ) {
            items(songs) { song ->
                SongItem(
                    song = song,
                    isSelected = song.id == currentSongId,
                    isPlaying = isPlaying && song.id == currentSongId,
                    onClick = {
                        viewModel.playSong(
                            song
                        )
                    }
                )
            }
            // Extra space for mini player
            item {
                Spacer(
                    modifier = Modifier.height(
                        80.dp
                    )
                )
            }
        }
    }
}

@Composable
private fun SongItem(
    song: Result,
    isSelected: Boolean,
    isPlaying: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(
                if (isSelected) Color.LightGray.copy(
                    alpha = 0.3f
                ) else Color.Transparent
            )
            .padding(PaddingMedium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = song.image.find { it.quality == "500x500" }?.url
                ?: song.image.firstOrNull()?.url,
            contentDescription = song.name,
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = song.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = if (isSelected) PrimaryOrange else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = song.artists.primary.firstOrNull()?.name ?: song.label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Text(
            text = formatDuration(song.duration),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(end = 8.dp)
        )

        if (isSelected)
        {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = null,
                tint = PrimaryOrange,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(24.dp)
            )
        }

        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

private fun formatDuration(duration: Int): String {
    val minutes = duration / 60
    val seconds = duration % 60
    return String.format("%02d:%02d", minutes, seconds)
}