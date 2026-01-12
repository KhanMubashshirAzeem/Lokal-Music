package com.mubashshir.lokalmusic.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.mubashshir.lokalmusic.R
import com.mubashshir.lokalmusic.ui.theme.PaddingMedium
import com.mubashshir.lokalmusic.ui.theme.PrimaryOrange

/**
 * A reusable header for detail screens like Artist and Album.
 *
 * @param imageUrl URL for the main image.
 * @param title The main title (e.g., album or artist name).
 * @param subtitle The text to display below the title.
 * @param tertiaryInfo Optional third line of text (e.g., year or song count).
 * @param imageSize The size of the AsyncImage.
 * @param onPlayClick Lambda for the "Play" button action.
 * @param onShuffleClick Lambda for the "Shuffle" button action.
 */
@Composable
fun DetailScreenHeader(
    imageUrl: String?,
    title: String,
    subtitle: String,
    tertiaryInfo: String?,
    imageSize: Dp,
    onPlayClick: () -> Unit,
    onShuffleClick: () -> Unit,
    modifier: Modifier = Modifier
)
{
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(PaddingMedium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = title,
            modifier = Modifier
                .size(imageSize)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.ic_place_holder),
            error = painterResource(R.drawable.ic_place_holder)
        )
        Spacer(
            modifier = Modifier.height(
                PaddingMedium
            )
        )
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        tertiaryInfo?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(
            modifier = Modifier.height(
                PaddingMedium
            )
        )
        PlayShuffleButtons(
            onPlayClick = onPlayClick,
            onShuffleClick = onShuffleClick
        )
    }
}

/**
 * A row containing standardized "Play" and "Shuffle" buttons.
 */
@Composable
fun PlayShuffleButtons(
    onPlayClick: () -> Unit,
    onShuffleClick: () -> Unit,
    modifier: Modifier = Modifier
)
{
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(
            PaddingMedium
        )
    ) {
        Button(
            onClick = onPlayClick,
            modifier = Modifier.weight(1f)
        ) {
            Text("Play")
        }
        Button(
            onClick = onShuffleClick,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryOrange.copy(
                    alpha = 0.7f
                )
            )
        ) {
            Text("Shuffle")
        }
    }
}
