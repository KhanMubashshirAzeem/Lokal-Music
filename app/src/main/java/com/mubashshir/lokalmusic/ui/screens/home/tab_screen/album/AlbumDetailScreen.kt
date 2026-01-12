package com.mubashshir.lokalmusic.ui.screens.home.tab_screen.album

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
import com.mubashshir.lokalmusic.ui.components.ErrorView
import com.mubashshir.lokalmusic.ui.components.LoadingView
import com.mubashshir.lokalmusic.ui.components.SongList
import com.mubashshir.lokalmusic.ui.screens.album.AlbumDetailViewModel
import com.mubashshir.lokalmusic.ui.theme.PaddingMedium
import com.mubashshir.lokalmusic.util.UiState

@RequiresApi(Build.VERSION_CODES.O)
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

    Column(modifier = Modifier.fillMaxSize()) {
        // Custom Top Bar instead of Scaffold's TopAppBar
        CustomTopBar(
            title = "Album",
            onNavigateBack = onNavigateBack
        )

        when (val state = uiState)
        {
            is UiState.Loading ->
            {
                LoadingView(modifier = Modifier.fillMaxSize())
            }

            is UiState.Success ->
            {
                val data = state.data
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding
                            (PaddingMedium)
                ) {
                    DetailScreenHeader(
                        imageUrl = data.albumImageUrl,
                        title = data.albumName,
                        subtitle = data.artistName,
                        tertiaryInfo = data.year.takeIf { it.isNotEmpty() },
                        imageSize = 240.dp,
                        onPlayClick = { viewModel.playAlbum() },
                        onShuffleClick = { viewModel.shuffleAlbum() }
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
                            .padding(PaddingMedium)
                    )
                }
            }

            is UiState.Error   ->
            {
                ErrorView(
                    message = state.message,
                    modifier = Modifier.fillMaxSize(),
                    onRetry = {
                        viewModel.loadAlbum(
                            albumId
                        )
                    }
                )
            }
        }
    }
}
