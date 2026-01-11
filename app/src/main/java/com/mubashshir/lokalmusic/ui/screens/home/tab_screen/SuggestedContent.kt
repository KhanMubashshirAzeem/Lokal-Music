package com.mubashshir.lokalmusic.ui.screens.home.tab_screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.mubashshir.lokalmusic.R
import com.mubashshir.lokalmusic.UiState
import com.mubashshir.lokalmusic.ui.screens.home.HomeViewModel
import com.mubashshir.lokalmusic.ui.screens.home.HorizontalItem

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SuggestedContent(
    homeViewModel: HomeViewModel = hiltViewModel(),
    onArtistClick: (String) -> Unit = {},
    onAlbumClick: (String) -> Unit = {}
)
{
    val homeUiState by homeViewModel.uiState.collectAsState()

    when (val state = homeUiState)
    {
        is UiState.Loading ->
        {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Loading...")
            }
        }

        is UiState.Success ->
        {
            val data = state.data
            SuggestedContentSuccess(
                artists = data.artists,
                mostPlayed = data.mostPlayed,
                onArtistClick = onArtistClick,
                onAlbumClick = onAlbumClick
            )
        }

        is UiState.Error   ->
        {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Error: ${state.message}",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun SuggestedContentSuccess(
    artists: List<HorizontalItem>,
    mostPlayed: List<HorizontalItem>,
    onArtistClick: (String) -> Unit,
    onAlbumClick: (String) -> Unit
)
{
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Recently Played - Placeholder
        SectionHeader(
            title = "Recently Played",
            onSeeAllClick = {})
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No recently played songs yet",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        SectionHeader(
            title = "Artists",
            onSeeAllClick = {})
        HorizontalCarousel(
            items = artists,
            onItemClick = { item ->
                onArtistClick(
                    item.id
                )
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        SectionHeader(
            title = "Most Played",
            onSeeAllClick = {})
        HorizontalCarousel(
            items = mostPlayed,
            onItemClick = { item ->
                onAlbumClick(
                    item.id
                )
            }
        )

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
fun SectionHeader(
    title: String,
    onSeeAllClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "See All",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.clickable { onSeeAllClick() }
        )
    }
}

@Composable
fun HorizontalCarousel(
    items: List<HorizontalItem>,
    onItemClick: (HorizontalItem) -> Unit
)
{
    if (items.isEmpty())
    {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No items available",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(items) { item ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(140.dp)
                    .clickable { onItemClick(item) }
            ) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.title,
                    modifier = Modifier
                        .size(140.dp)
                        .clip(
                            RoundedCornerShape(
                                16.dp
                            )
                        ),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(
                        R.drawable.ic_place_holder
                    ),
                    error = painterResource(R.drawable.ic_place_holder)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = item.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}