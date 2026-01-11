package com.mubashshir.lokalmusic.ui.screens.songs

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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.mubashshir.lokalmusic.ui.components.FullPlayer
import com.mubashshir.lokalmusic.ui.components.MiniPlayer
import com.mubashshir.lokalmusic.data.model.Result as SongModel

@Composable
fun SongScreen(
    viewModel: SongViewModel = hiltViewModel()
)
{
    val songs by viewModel.songs.collectAsState()
    val currentSong by viewModel.currentSong.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()

    var searchQuery by remember { mutableStateOf("arijit") }
    var isFullPlayerVisible by remember {
        mutableStateOf(
            false
        )
    }
    val keyboardController =
        LocalSoftwareKeyboardController.current

    // Handle back press to close Full Player
    BackHandler(enabled = isFullPlayerVisible) {
        isFullPlayerVisible = false
    }

    LaunchedEffect(Unit) {
        viewModel.search(searchQuery)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // --- LAYER 1: SEARCH & LIST ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Custom Search Component
            TextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search songs...") },
                singleLine = true,
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        null
                    )
                },
                shape = MaterialTheme.shapes.extraLarge,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            if (songs.isEmpty())
            {
                Box(
                    Modifier.fillMaxSize(),
                    Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else
            {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        bottom = if (currentSong != null) 80.dp else 16.dp
                    )
                ) {
                    items(songs) { song ->
                        SongItem(
                            song = song,
                            isPlaying = currentSong?.id == song.id && isPlaying,
                            onClick = {
                                viewModel.playSong(
                                    song
                                )
                                keyboardController?.hide()
                            }
                        )
                    }
                }
            }
        }

        // --- LAYER 2: OVERLAY PLAYER ---
        // This handles both the MiniPlayer and the FullPlayer transition
        AnimatedVisibility(
            visible = currentSong != null,
            enter = slideInVertically(
                initialOffsetY = { it }),
            exit = slideOutVertically(
                targetOffsetY = { it }),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            // Animate between Mini and Full versions
            AnimatedContent(
                targetState = isFullPlayerVisible,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "PlayerTransition"
            ) { showFull ->
                if (showFull)
                {
                    FullPlayer(
                        song = currentSong!!,
                        isPlaying = isPlaying,
                        onTogglePlayPause = { viewModel.togglePlayPause() },
                        onSkipNext = { /* TODO */ },
                        onSkipPrevious = { /* TODO */ },
                        onSeek = { /* TODO */ },
                        onClose = {
                            isFullPlayerVisible =
                                false
                        }
                    )
                } else
                {
                    MiniPlayer(
                        song = currentSong!!,
                        isPlaying = isPlaying,
                        onTogglePlayPause = { viewModel.togglePlayPause() },
                        onClick = {
                            isFullPlayerVisible =
                                true
                        },
                        modifier = Modifier
                            .navigationBarsPadding()
                            .padding(8.dp)
                            .clip(
                                RoundedCornerShape(
                                    12.dp
                                )
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun SongItem(
    song: SongModel,
    isPlaying: Boolean,
    onClick: () -> Unit
)
{
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(
                horizontal = 16.dp,
                vertical = 8.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = song.image.lastOrNull()?.url,
            contentDescription = null,
            modifier = Modifier
                .size(60.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(Color.LightGray),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = song.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1
            )
            Text(
                text = song.artists.primary.firstOrNull()?.name
                    ?: song.label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }

        Icon(
            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = null,
            tint = if (isPlaying) MaterialTheme.colorScheme.primary else Color.Gray,
            modifier = Modifier
                .padding(8.dp)
                .size(28.dp)
        )

        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.padding(8.dp)
        )
    }
}