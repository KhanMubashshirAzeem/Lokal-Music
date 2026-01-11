package com.mubashshir.lokalmusic.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.mubashshir.lokalmusic.data.model.Result
import com.mubashshir.lokalmusic.ui.theme.PaddingMedium
import com.mubashshir.lokalmusic.ui.theme.PrimaryOrange

/**
 * Reusable song list composable used across Home, Search, Artist, Album screens.
 * 
 * @param songs List of songs to display
 * @param currentSongId ID of currently playing song (null if none)
 * @param isPlaying Whether playback is currently active
 * @param onSongClick Called when a song is clicked - should start playback
 * @param onMoreClick Optional callback for more options menu
 */
@Composable
fun SongList(
    songs: List<Result>,
    currentSongId: String? = null,
    isPlaying: Boolean = false,
    onSongClick: (Result) -> Unit,
    onMoreClick: ((Result) -> Unit)? = null,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        items(songs) { song ->
            SongListItem(
                song = song,
                isSelected = song.id == currentSongId,
                isPlaying = isPlaying && song.id == currentSongId,
                onClick = { onSongClick(song) },
                onMoreClick = onMoreClick?.let { { it(song) } }
            )
        }
    }
}

@Composable
private fun SongListItem(
    song: Result,
    isSelected: Boolean,
    isPlaying: Boolean,
    onClick: () -> Unit,
    onMoreClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(PaddingMedium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = song.image.find { it.quality == "500x500" }?.url
                ?: song.image.firstOrNull()?.url,
            contentDescription = song.name,
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = song.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = if (isSelected) PrimaryOrange else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = song.artists.primary.firstOrNull()?.name ?: song.label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Text(
            text = formatDuration(song.duration),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(end = 8.dp)
        )

        if (isSelected) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = null,
                tint = PrimaryOrange,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(24.dp)
            )
        }

        if (onMoreClick != null) {
            IconButton(onClick = onMoreClick) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More options",
                    tint = Color.Gray
                )
            }
        }
    }
}

private fun formatDuration(duration: Int): String {
    val minutes = duration / 60
    val seconds = duration % 60
    return String.format("%02d:%02d", minutes, seconds)
}