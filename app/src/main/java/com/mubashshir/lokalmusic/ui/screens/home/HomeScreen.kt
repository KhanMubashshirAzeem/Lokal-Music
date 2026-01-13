package com.mubashshir.lokalmusic.ui.screens.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mubashshir.lokalmusic.ui.screens.base.UiEvent
import com.mubashshir.lokalmusic.ui.screens.home.tab_screen.album.AlbumsContent
import com.mubashshir.lokalmusic.ui.screens.home.tab_screen.artists.ArtistsContent
import com.mubashshir.lokalmusic.ui.screens.home.tab_screen.songs.SongScreen
import com.mubashshir.lokalmusic.ui.screens.home.tab_screen.suggestion.MostPlayedTab
import com.mubashshir.lokalmusic.ui.screens.home.tab_screen.suggestion.RecentlyPlayedTab
import com.mubashshir.lokalmusic.ui.screens.home.tab_screen.suggestion.SuggestedContent
import com.mubashshir.lokalmusic.ui.theme.PrimaryOrange
import com.mubashshir.lokalmusic.util.UiState
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToSearch: () -> Unit = {},
    onNavigateToArtist: (String) -> Unit = {},
    onNavigateToAlbum: (String) -> Unit = {},
    onNavigateToFullPlayer: () -> Unit = {},
    onUnauthorized: () -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }

    // 🔥 Global UI events (error, unauthorized)
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event)
            {
                is UiEvent.ShowError ->
                {
                    snackbarHostState.showSnackbar(event.message)
                }

                UiEvent.Unauthorized ->
                {
                    onUnauthorized()
                }
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { padding ->
        HomeContent(
            modifier = modifier.padding(padding),
            viewModel = viewModel,
            onNavigateToSearch = onNavigateToSearch,
            onNavigateToArtist = onNavigateToArtist,
            onNavigateToAlbum = onNavigateToAlbum,
            onNavigateToFullPlayer = onNavigateToFullPlayer
        )
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun HomeContent(
    modifier: Modifier,
    viewModel: HomeViewModel,
    onNavigateToSearch: () -> Unit,
    onNavigateToArtist: (String) -> Unit,
    onNavigateToAlbum: (String) -> Unit,
    onNavigateToFullPlayer: () -> Unit
)
{
    val tabs = listOf(
        "Suggested",
        "Songs",
        "Artists",
        "Albums",
        "Recently Played",
        "Most Played"
    )

    val pagerState = rememberPagerState { tabs.size }
    val scope = rememberCoroutineScope()

    val uiState by viewModel.uiState.collectAsState()
    val currentSongId by viewModel.currentSongId.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {

        HomeTopBar(onNavigateToSearch)

        HomeTabs(
            tabs = tabs,
            pagerState = pagerState,
            onTabClick = { index ->
                scope.launch { pagerState.animateScrollToPage(index) }
            }
        )

        HomePager(
            pagerState = pagerState,
            uiState = uiState,
            currentSongId = currentSongId,
            isPlaying = isPlaying,
            viewModel = viewModel,
            onNavigateToArtist = onNavigateToArtist,
            onNavigateToAlbum = onNavigateToAlbum,
            onPageNavigate = { page ->
                scope.launch {
                    pagerState.animateScrollToPage(page)
                }
            }
        )

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBar(
    onNavigateToSearch: () -> Unit
)
{
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = PrimaryOrange
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Mume",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        },
        actions = {
            IconButton(onClick = onNavigateToSearch) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Search",
                    tint = PrimaryOrange
                )
            }
        }
    )
}

@Composable
private fun HomeTabs(
    tabs: List<String>,
    pagerState: PagerState,
    onTabClick: (Int) -> Unit
)
{
    ScrollableTabRow(
        selectedTabIndex = pagerState.currentPage,
        edgePadding = 0.dp,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                modifier = Modifier.tabIndicatorOffset(
                    tabPositions[pagerState.currentPage]
                ),
                color = PrimaryOrange
            )
        }
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = pagerState.currentPage == index,
                onClick = { onTabClick(index) },
                text = {
                    Text(
                        title,
                        color = if (pagerState.currentPage == index)
                            PrimaryOrange
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )
        }
    }
}



@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun HomePager(
    pagerState: PagerState,
    uiState: UiState<HomeData>,
    currentSongId: String?,
    isPlaying: Boolean,
    viewModel: HomeViewModel,
    onNavigateToArtist: (String) -> Unit,
    onNavigateToAlbum: (String) -> Unit,
    onPageNavigate: (Int) -> Unit // ✅ added
) {
    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { page ->
        when (page) {

            0 -> SuggestedContent(
                homeViewModel = viewModel,
                onArtistClick = onNavigateToArtist,
                onAlbumClick = onNavigateToAlbum,
                onSongClick = { songId ->
                    viewModel.playSong(songId)
                },
                onSeeAllArtists = {
                    onPageNavigate(2)
                },
                onSeeAllRecentlyPlayed = {
                    onPageNavigate(4)
                },
                onSeeAllMostPlayed = {
                    onPageNavigate(5)
                }
            )

            1 -> SongScreen()

            2 -> ArtistsContent(
                onNavigateToArtist = onNavigateToArtist
            )

            3 -> AlbumsContent(
                onNavigateToAlbum = onNavigateToAlbum
            )

            4 -> {
                // Recently Played Tab
                val songs =
                    if (uiState is UiState.Success)
                        uiState.data.recentlyPlayedSongs
                    else emptyList()

                RecentlyPlayedTab(
                    songs = songs,
                    currentSongId = currentSongId,
                    isPlaying = isPlaying,
                    onSongClick = { song ->
                        viewModel.playTrackList(
                            songs,
                            song
                        )
                    }
                )
            }

            5 -> {
                // Most Played Tab
                val songs =
                    if (uiState is UiState.Success)
                        uiState.data.mostPlayedSongs
                    else emptyList()

                MostPlayedTab(
                    songs = songs,
                    currentSongId = currentSongId,
                    isPlaying = isPlaying,
                    onSongClick = { song ->
                        viewModel.playTrackList(
                            songs,
                            song
                        )
                    }
                )
            }
        }
    }
}
