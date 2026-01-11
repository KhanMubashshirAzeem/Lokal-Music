package com.mubashshir.lokalmusic.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.mubashshir.lokalmusic.ui.theme.PrimaryOrange
import com.mubashshir.lokalmusic.ui.viewmodel.PlayerViewModel

/**
 * Full-screen player screen that uses PlayerViewModel.
 * This is a separate navigation route, not rendered inside Scaffold.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FullPlayerScreen(
    onNavigateBack: () -> Unit,
    playerViewModel: PlayerViewModel = hiltViewModel()
)
{
    val currentSong by playerViewModel.currentSong.collectAsState()
    val isPlaying by playerViewModel.isPlaying.collectAsState()
    val playbackPosition by playerViewModel.playbackPosition.collectAsState()
    val duration by playerViewModel.duration.collectAsState()

    if (currentSong == null)
    {
        // No song playing, go back
        onNavigateBack()
        return
    }

    val progress = if (duration > 0)
    {
        playbackPosition.toFloat() / duration.toFloat()
    } else
    {
        0f
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header (Close Button)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Close Player",
                        modifier = Modifier.size(
                            32.dp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Big Album Art
            AsyncImage(
                model = currentSong!!.image.find { it.quality == "500x500" }?.url
                    ?: currentSong!!.image.firstOrNull()?.url,
                contentDescription = "Album Art",
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(24.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Title and Artist
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = currentSong!!.name,
                    style = MaterialTheme.typography.headlineMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = currentSong!!.artists.primary.firstOrNull()?.name
                        ?: currentSong!!.label,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Seekbar
            Slider(
                value = progress,
                onValueChange = { newProgress ->
                    playerViewModel.seekTo(
                        newProgress
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    formatTime(playbackPosition),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    formatTime(duration),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Controls Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { playerViewModel.skipPrevious() },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        Icons.Default.SkipPrevious,
                        contentDescription = "Previous",
                        modifier = Modifier.size(
                            36.dp
                        )
                    )
                }

                // Main Play/Pause Button
                Surface(
                    shape = RoundedCornerShape(50),
                    color = PrimaryOrange,
                    modifier = Modifier
                        .size(72.dp)
                        .clickable { playerViewModel.togglePlayPause() }
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(
                            16.dp
                        )
                    )
                }

                IconButton(
                    onClick = { playerViewModel.skipNext() },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        Icons.Default.SkipNext,
                        contentDescription = "Next",
                        modifier = Modifier.size(
                            36.dp
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

private fun formatTime(timeMs: Long): String
{
    val totalSeconds = (timeMs / 1000).toInt()
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(
        "%02d:%02d",
        minutes,
        seconds
    )
}