package com.mubashshir.lokalmusic.ui.screens.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mubashshir.lokalmusic.ui.screens.home.tab_screen.SuggestedContent
import com.mubashshir.lokalmusic.ui.screens.home.tab_screen.album.AlbumsContent
import com.mubashshir.lokalmusic.ui.screens.home.tab_screen.artists.ArtistsContent
import com.mubashshir.lokalmusic.ui.screens.home.tab_screen.songs.SongScreen
import com.mubashshir.lokalmusic.ui.theme.PrimaryOrange

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToSearch: () -> Unit = {},
    onNavigateToArtist: (String) -> Unit = {},
    onNavigateToAlbum: (String) -> Unit = {},
    onNavigateToFullPlayer: () -> Unit = {}
)
{
    var selectedTabIndex by remember {
        mutableIntStateOf(
            0
        )
    }

    val tabs = listOf(
        "Suggested",
        "Songs",
        "Artists",
        "Albums"
    )

    Column(modifier = modifier.fillMaxSize()) {
        // Top App Bar
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = PrimaryOrange
                    )
                    Spacer(
                        modifier = Modifier.width(
                            8.dp
                        )
                    )
                    Text(
                        text = "Mume",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            },
            actions = {
                IconButton(onClick = onNavigateToSearch) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = PrimaryOrange
                    )
                }
            }
        )

        // Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = {
                        selectedTabIndex = index
                    },
                    text = {
                        Text(
                            text = title,
                            color = if (selectedTabIndex == index)
                                PrimaryOrange
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }
        }

        // Content
        AnimatedContent(
            targetState = selectedTabIndex,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            modifier = Modifier.fillMaxSize(),
            label = "tab_animation"
        ) { tabIndex ->
            when (tabIndex)
            {
                0 -> SuggestedContent(
                    homeViewModel = viewModel,
                    onArtistClick = onNavigateToArtist,
                    onAlbumClick = onNavigateToAlbum
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