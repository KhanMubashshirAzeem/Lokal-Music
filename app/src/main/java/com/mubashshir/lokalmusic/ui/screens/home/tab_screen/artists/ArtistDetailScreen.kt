package com.mubashshir.lokalmusic.ui.screens.home.tab_screen.artists

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.mubashshir.lokalmusic.R
import com.mubashshir.lokalmusic.UiState
import com.mubashshir.lokalmusic.ui.components.SongList
import com.mubashshir.lokalmusic.ui.screens.artist.ArtistDetailViewModel
import com.mubashshir.lokalmusic.ui.theme.PaddingLarge
import com.mubashshir.lokalmusic.ui.theme.PaddingMedium

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistDetailScreen(
    artistId: String,
    onNavigateBack: () -> Unit,
    viewModel: ArtistDetailViewModel = hiltViewModel()
)
{
    val uiState by viewModel.uiState.collectAsState()
    val currentSongId by viewModel.currentSongId.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()

    LaunchedEffect(artistId) {
        viewModel.loadArtist(artistId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Artist") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        when (val state = uiState)
        {
            is UiState.Loading ->
            {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Loading...",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            is UiState.Success ->
            {
                val data = state.data

                // CHECK: Handle the case where the API returns success but no songs
                if (data.songs.isEmpty())
                {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                modifier = Modifier.size(
                                    64.dp
                                ),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(
                                modifier = Modifier.height(
                                    16.dp
                                )
                            )
                            Text(
                                text = "No songs found for this artist",
                                style = MaterialTheme.typography.titleMedium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else
                {
                    // Regular View (when songs exist)
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        // Artist Header
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    PaddingLarge
                                ),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            AsyncImage(
                                model = data.artistImageUrl,
                                contentDescription = data.artistName,
                                modifier = Modifier
                                    .size(200.dp)
                                    .clip(
                                        CircleShape
                                    ),
                                contentScale = ContentScale.Crop,
                                placeholder = painterResource(
                                    R.drawable.ic_place_holder
                                ),
                                error = painterResource(
                                    R.drawable.ic_place_holder
                                )
                            )
                            Spacer(
                                modifier = Modifier.height(
                                    PaddingMedium
                                )
                            )
                            Text(
                                text = data.artistName,
                                style = MaterialTheme.typography.headlineMedium,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "${data.songCount} ${if (data.songCount == 1) "song" else "songs"}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Songs List
                        SongList(
                            songs = data.songs,
                            currentSongId = currentSongId,
                            isPlaying = isPlaying,
                            onSongClick = { song ->
                                viewModel.playSong(
                                    song
                                )
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            is UiState.Error   ->
            {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Error: ${state.message}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}