package com.mubashshir.lokalmusic.ui.screens.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mubashshir.lokalmusic.ui.common.SearchBar
import com.mubashshir.lokalmusic.ui.screens.home.tab_screen.album.AlbumsContent
import com.mubashshir.lokalmusic.ui.screens.home.tab_screen.artists.ArtistsContent
import com.mubashshir.lokalmusic.ui.screens.home.tab_screen.SuggestedContent
import com.mubashshir.lokalmusic.ui.screens.home.tab_screen.songs.SongScreen
import com.mubashshir.lokalmusic.ui.theme.PrimaryOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
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
        "Albums",
        "Folders"
    )

    var isSearchActive by remember {
        mutableStateOf(
            false
        )
    }

    var searchQuery by remember {
        mutableStateOf("")
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Search bar that appears when search is active
        AnimatedVisibility(
            visible = isSearchActive,
            enter = slideInVertically { -it } + fadeIn(),
            exit = slideOutVertically { -it } + fadeOut()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 8.dp,
                        vertical = 8.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    isSearchActive = false
                }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
                SearchBar(
                    query = searchQuery,
                    onQueryChange = {
                        searchQuery = it
                    },
                    onClear = {
                        searchQuery = ""
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Top app bar - hidden when search is active
        AnimatedVisibility(
            visible = !isSearchActive,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
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
                            "Mume",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        isSearchActive = true
                    }) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search",
                            tint = PrimaryOrange
                        )
                    }
                }
            )
        }

        // Tabs - hidden when search is active
        AnimatedVisibility(
            visible = !isSearchActive,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = {
                            selectedTabIndex =
                                index
                        },
                        text = {
                            Text(
                                title,
                                color = if (selectedTabIndex == index) PrimaryOrange else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                }
            }
        }

        // Search results placeholder - shown when search is active
        AnimatedVisibility(
            visible = isSearchActive,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
            ) {
                if (searchQuery.isEmpty())
                {
                    Text(
                        "Enter search query",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else
                {
                    Text(
                        "Search results for: $searchQuery",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    // TODO: Implement search results screen
                }
            }
        }

        // Tab content - hidden when search is active
        AnimatedVisibility(
            visible = !isSearchActive,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            AnimatedContent(
                targetState = selectedTabIndex,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                modifier = Modifier.fillMaxSize()
            ) { tabIndex ->
                when (tabIndex)
                {
                    0 -> SuggestedContent(
                        viewModel
                    )

                    1 -> SongScreen()
                    2 -> ArtistsContent()
                    3 -> AlbumsContent()
                    else ->
                    {
                    }
                }
            }
        }
    }
}