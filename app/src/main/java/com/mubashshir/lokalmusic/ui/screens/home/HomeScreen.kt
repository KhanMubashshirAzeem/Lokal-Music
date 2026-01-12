package com.mubashshir.lokalmusic.ui.screens.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mubashshir.lokalmusic.ui.screens.home.tab_screen.SuggestedContent
import com.mubashshir.lokalmusic.ui.screens.home.tab_screen.album.AlbumsContent
import com.mubashshir.lokalmusic.ui.screens.home.tab_screen.artists.ArtistsContent
import com.mubashshir.lokalmusic.ui.screens.home.tab_screen.songs.SongScreen
import com.mubashshir.lokalmusic.ui.theme.PrimaryOrange
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToSearch: () -> Unit = {},
    onNavigateToArtist: (String) -> Unit = {}, // This String is now the Artist Name
    onNavigateToAlbum: (String) -> Unit = {},
    onNavigateToFullPlayer: () -> Unit = {}
) {
    val tabs = listOf(
        "Suggested",
        "Songs",
        "Artists",
        "Albums",
        "Recently Played",
        "Most Played"
    )
    val pagerState =
        rememberPagerState { tabs.size }
    val scope = rememberCoroutineScope()

    Column(modifier = modifier.fillMaxSize()) {

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
                        text = "Mume",
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

        ScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(
                                index
                            )
                        }
                    },
                    text = {
                        Text(
                            text = title,
                            color = if (pagerState.currentPage == index) PrimaryOrange else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page)
            {
                0 -> SuggestedContent(
                    homeViewModel = viewModel,
                    onArtistClick = onNavigateToArtist,
                    onAlbumClick = onNavigateToAlbum,
                    onSongClick = { songId ->
                        viewModel.playSong(
                            songId
                        )
                    },
                    onSeeAllArtists = {
                        // Switch to "Artists" tab (index 2)
                        scope.launch {
                            pagerState.animateScrollToPage(
                                2
                            )
                        }
                    }
                )

                1 -> SongScreen()

                2 -> ArtistsContent(
                    onNavigateToArtist = onNavigateToArtist
                )

                3 -> AlbumsContent(
                    onNavigateToAlbum = onNavigateToAlbum
                )

            }
        }
    }
}