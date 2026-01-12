package com.mubashshir.lokalmusic.ui.screens.home.tab_screen.artists

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mubashshir.lokalmusic.ui.components.CustomTopBar
import com.mubashshir.lokalmusic.ui.components.DetailScreenHeader
import com.mubashshir.lokalmusic.ui.components.EmptyView
import com.mubashshir.lokalmusic.ui.components.ErrorView
import com.mubashshir.lokalmusic.ui.components.LoadingView
import com.mubashshir.lokalmusic.ui.components.SongList
import com.mubashshir.lokalmusic.ui.screens.artist.ArtistDetailViewModel
import com.mubashshir.lokalmusic.ui.theme.PaddingMedium
import com.mubashshir.lokalmusic.util.UiState

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ArtistDetailScreen(
    artistId: String,
    onNavigateBack: () -> Unit,
    viewModel: ArtistDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentSongId by viewModel.currentSongId.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()

    LaunchedEffect(artistId) {
        viewModel.loadArtist(artistId)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Custom Top Bar instead of Scaffold's TopAppBar
        CustomTopBar(
            title = "Artist",
            onNavigateBack = onNavigateBack
        )

        when (val state = uiState) {
            is UiState.Loading -> {
                LoadingView(modifier = Modifier.fillMaxSize())
            }

            is UiState.Success -> {
                val data = state.data

                if (data.songs.isEmpty()) {
                    EmptyView(
                        message = "No songs found for this artist",
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        DetailScreenHeader(
                            imageUrl = data.artistImageUrl,
                            title = data.artistName,
                            subtitle = "${data.songCount} ${if (data.songCount == 1) "song" else "songs"}",
                            tertiaryInfo = null, // No tertiary info for artist
                            imageSize = 200.dp,
                            onPlayClick = { viewModel.playArtist() },
                            onShuffleClick = { viewModel.shuffleArtist() }
                        )

                        SongList(
                            songs = data.songs,
                            currentSongId = currentSongId,
                            isPlaying = isPlaying,
                            onSongClick = { song ->
                                viewModel.playSong(
                                    song
                                )
                            },
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(
                                    PaddingMedium
                                )
                        )
                    }
                }
            }

            is UiState.Error -> {
                ErrorView(
                    message = state.message,
                    modifier = Modifier.fillMaxSize(),
                    onRetry = {
                        viewModel.loadArtist(
                            artistId
                        )
                    }
                )
            }
        }
    }
}
