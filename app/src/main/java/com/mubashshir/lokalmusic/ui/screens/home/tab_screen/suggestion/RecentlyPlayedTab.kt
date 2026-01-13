package com.mubashshir.lokalmusic.ui.screens.home.tab_screen.suggestion

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mubashshir.lokalmusic.data.model.Result
import com.mubashshir.lokalmusic.ui.components.SongList

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RecentlyPlayedTab(
    songs: List<Result>,
    currentSongId: String?,
    isPlaying: Boolean,
    onSongClick: (Result) -> Unit
) {
    if (songs.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No recently played songs.")
        }
    } else {
        SongList(
            songs = songs,
            currentSongId = currentSongId,
            isPlaying = isPlaying,
            onSongClick = onSongClick,
            modifier = Modifier.fillMaxSize()
        )
    }
}