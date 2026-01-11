package com.mubashshir.lokalmusic.ui.screens.home.tab_screen.songs

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
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
import com.mubashshir.lokalmusic.ui.components.FullPlayer
import com.mubashshir.lokalmusic.ui.components.MiniPlayer
import com.mubashshir.lokalmusic.ui.theme.PaddingMedium
import com.mubashshir.lokalmusic.ui.theme.PaddingSmall

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

@Composable
fun SongScreen(
    viewModel: SongViewModel = hiltViewModel()
) {
    val songs by viewModel.songs.collectAsState()
    val currentSong by viewModel.currentSong.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()

    // State to toggle between Mini and Full player
    var showFullPlayer by remember { mutableStateOf(false) }

    var searchQuery by remember {
        mutableStateOf("arijit") }
    var currentSort by remember { mutableStateOf(SortOption.ASCENDING) }
    var showSortMenu by remember { mutableStateOf(false) }

    // Handle back button to close Full Player instead of app
    BackHandler(showFullPlayer) {
        showFullPlayer = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            viewModel.searchSongs(searchQuery)
//             Search Bar
//            TextField(
//                value = searchQuery,
//                onValueChange = {
//                    searchQuery = it
//                    viewModel.searchSongs(it)
//                },
//                placeholder = { Text("Search songs") },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(PaddingMedium)
//            )

            // Sort Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = PaddingMedium, vertical = PaddingSmall),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${songs.size} songs",
                    style = MaterialTheme.typography.titleMedium
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { showSortMenu = true }
                ) {
                    Text(
                        text = currentSort.displayName,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                DropdownMenu(
                    expanded = showSortMenu,
                    onDismissRequest = { showSortMenu = false }
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
                contentPadding = PaddingValues(PaddingMedium)
            ) {
                items(songs) { song ->
                    SongItem(
                        song = song,
                        isSelected = song.id == currentSong?.id,
                        isPlaying = isPlaying && song.id == currentSong?.id,
                        onClick = { viewModel.playSong(song) }
                    )
                }
            }
        }

        // Mini Player
        // Visible only when a song is selected AND Full Player is NOT showing
        AnimatedVisibility(
            visible = currentSong != null && !showFullPlayer,
            enter = slideInVertically { it },
            exit = slideOutVertically { it },
            modifier = Modifier
                .align(Alignment.BottomCenter)
        ) {
            currentSong?.let { song ->
                MiniPlayer(
                    song = song,
                    isPlaying = isPlaying,
                    onTogglePlayPause = { viewModel.togglePlayPause() },
                    onClick = {
                        // FIX: Set state to TRUE to open FullPlayer
                        showFullPlayer = true
                    },
                    modifier = Modifier
                )
            }
        }

        // Full Player
        // Transitions in when showFullPlayer is true
        AnimatedContent(
            targetState = showFullPlayer,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            modifier = Modifier.fillMaxSize()
        ) { isFull ->
            if (isFull) {
                currentSong?.let { song ->
                    FullPlayer(
                        song = song,
                        isPlaying = isPlaying,
                        onClose = {
                            // FIX: Set state to FALSE to close FullPlayer
                            showFullPlayer = false
                        },
                        onTogglePlayPause = { viewModel.togglePlayPause() },
                        onSkipPrevious = { /* Implement if queue exists */ },
                        onSkipNext = { /* Implement if queue exists */ },
                        modifier = Modifier,
                        onSeek = {}
                    )
                }
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
            .background(if (isSelected) Color.LightGray else Color.Transparent)
            .padding(PaddingMedium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = song.image.firstOrNull { it.quality == "500x500" }?.url
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
                overflow = TextOverflow.Ellipsis
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
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Icon(
            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = null,
            tint = if (isPlaying) MaterialTheme.colorScheme.primary else Color.Gray,
            modifier = Modifier
                .padding(start = 8.dp)
                .size(24.dp)
        )

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