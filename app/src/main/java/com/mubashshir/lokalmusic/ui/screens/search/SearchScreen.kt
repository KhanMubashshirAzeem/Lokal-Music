package com.mubashshir.lokalmusic.ui.screens.search

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.mubashshir.lokalmusic.UiState
import com.mubashshir.lokalmusic.data.model.Result
import com.mubashshir.lokalmusic.ui.components.SearchBar
import com.mubashshir.lokalmusic.ui.components.SongList
import com.mubashshir.lokalmusic.ui.theme.PaddingMedium

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onNavigateBack: () -> Unit,
    onNavigateToFullPlayer: () -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentSongId by viewModel.currentSongId.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            SearchBar(
                query = viewModel.searchQuery,
                onQueryChange = { viewModel.search(it) },
                onClear = { viewModel.clearSearch() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(PaddingMedium)
            )

            when (uiState) {
                is com.mubashshir.lokalmusic.UiState.Loading -> {
                    // Loading state
                    Text(
                        "Searching...",
                        modifier = Modifier.padding(PaddingMedium),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                is com.mubashshir.lokalmusic.UiState.Success -> {
                    SongList(
                        songs = (uiState as UiState.Success<List<Result>>).data,
                        currentSongId = currentSongId,
                        isPlaying = isPlaying,
                        onSongClick = { song ->
                            viewModel.playSong(song)
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                is com.mubashshir.lokalmusic.UiState.Error -> {
                    Text(
                        "Error: ${(uiState as UiState.Error).message}",
                        modifier = Modifier.padding(PaddingMedium),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}