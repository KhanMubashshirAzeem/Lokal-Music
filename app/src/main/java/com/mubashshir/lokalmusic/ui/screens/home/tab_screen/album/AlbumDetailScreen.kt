package com.mubashshir.lokalmusic.ui.screens.album

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.mubashshir.lokalmusic.R
import com.mubashshir.lokalmusic.UiState
import com.mubashshir.lokalmusic.ui.components.SongList
import com.mubashshir.lokalmusic.ui.theme.PaddingLarge
import com.mubashshir.lokalmusic.ui.theme.PaddingMedium
import com.mubashshir.lokalmusic.ui.theme.PrimaryOrange

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumDetailScreen(
    albumId: String,
    onNavigateBack: () -> Unit,
    viewModel: AlbumDetailViewModel = hiltViewModel()
)
{
    val uiState by viewModel.uiState.collectAsState()
    val currentSongId by viewModel.currentSongId.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()

    LaunchedEffect(albumId) {
        viewModel.loadAlbum(albumId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Album") },
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
                Text(
                    "Loading...",
                    modifier = Modifier.padding(
                        PaddingMedium
                    ),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            is UiState.Success ->
            {
                val data = state.data
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    // Album Header
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(PaddingLarge),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AsyncImage(
                            model = data.albumImageUrl,
                            contentDescription = data.albumName,
                            modifier = Modifier
                                .size(300.dp)
                                .clip(
                                    RoundedCornerShape(
                                        16.dp
                                    )
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
                            text = data.albumName,
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            text = data.artistName,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (data.year.isNotEmpty())
                        {
                            Text(
                                text = data.year,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(
                                PaddingMedium
                            )
                        )

                        // Play and Shuffle buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(
                                PaddingMedium
                            )
                        ) {
                            Button(
                                onClick = { viewModel.playAlbum() },
                                modifier = Modifier.weight(
                                    1f
                                )
                            ) {
                                Text("Play")
                            }
                            Button(
                                onClick = { viewModel.shuffleAlbum() },
                                modifier = Modifier.weight(
                                    1f
                                ),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PrimaryOrange.copy(
                                        alpha = 0.7f
                                    )
                                )
                            ) {
                                Text("Shuffle")
                            }
                        }
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

            is UiState.Error   ->
            {
                Text(
                    "Error: ${state.message}",
                    modifier = Modifier.padding(
                        PaddingMedium
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}