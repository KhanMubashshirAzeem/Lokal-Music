// ui/screens/home/HomeScreen.kt
package com.mubashshir.lokalmusic.ui.screens.home

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mubashshir.lokalmusic.ui.screens.home.tab_screen.AlbumsContent
import com.mubashshir.lokalmusic.ui.screens.home.tab_screen.ArtistsContent
import com.mubashshir.lokalmusic.ui.screens.home.tab_screen.FoldersContent
import com.mubashshir.lokalmusic.ui.screens.home.tab_screen.SuggestedContent
import com.mubashshir.lokalmusic.ui.screens.songs.SongScreen
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

    // Placeholder for search state (expand as needed)
    var isSearchActive by remember {
        mutableStateOf(
            false
        )
    }

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
                        "Mume",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            },
            actions = {
                IconButton(onClick = {
                    isSearchActive =
                        !isSearchActive
                }) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search"
                    )
                }
            }
        )

        // Scrollable Tab Row
        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            edgePadding = 0.dp // Adjust if needed
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = {
                        selectedTabIndex = index
                    },
                    text = { Text(title) }
                )
            }
        }

        // Tab Content
        AnimatedContent(
            targetState = selectedTabIndex,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            modifier = Modifier.fillMaxSize()
        ) { tabIndex ->
            when (tabIndex)
            {
                0 -> SuggestedContent(viewModel) // Existing "Suggested" content
                1 -> SongScreen() // Implement as per
                // previous SongsTab
                2 -> ArtistsContent() // Placeholder
                3 -> AlbumsContent() // Placeholder
                4 -> FoldersContent() // Placeholder
                else ->
                {
                }
            }
        }
    }
}