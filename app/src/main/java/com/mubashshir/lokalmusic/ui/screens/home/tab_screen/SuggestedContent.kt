// ui/screens/home/tab_screen/SuggestedContent.kt
package com.mubashshir.lokalmusic.ui.screens.home.tab_screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import coil.compose.AsyncImage
import com.mubashshir.lokalmusic.R
import com.mubashshir.lokalmusic.ui.screens.home.HomeViewModel
import com.mubashshir.lokalmusic.ui.screens.home.HorizontalItem

@Composable
fun SuggestedContent(viewModel: HomeViewModel) {
    val recentlyPlayed by viewModel.recentlyPlayed.collectAsState()
    val artists by viewModel.artists.collectAsState()
    val mostPlayed by viewModel.mostPlayed.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        // Header spacing
        Spacer(modifier = Modifier.height(12.dp))

        // Recently Played
        SectionHeader(title = "Recently Played", onSeeAllClick = { /* navigate */ })
        HorizontalCarousel(items = recentlyPlayed)

        Spacer(modifier = Modifier.height(24.dp))

        // Artists
        SectionHeader(title = "Artists", onSeeAllClick = { /* navigate to Artists */ })
        HorizontalCarousel(items = artists)

        Spacer(modifier = Modifier.height(24.dp))

        // Most Played
        SectionHeader(title = "Most Played", onSeeAllClick = { /* navigate */ })
        HorizontalCarousel(items = mostPlayed)

        Spacer(modifier = Modifier.height(100.dp)) // space for mini player
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
            color = MaterialTheme.colorScheme.primary, // orange
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.clickable { onSeeAllClick() }
        )
    }
}

@Composable
fun HorizontalCarousel(items: List<HorizontalItem>) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(items) { item ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(140.dp)
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
                    placeholder = painterResource(R.drawable.ic_place_holder) // add default
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