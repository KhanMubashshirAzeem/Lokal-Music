package com.mubashshir.lokalmusic

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.mubashshir.lokalmusic.data.repository.SongRepository
import com.mubashshir.lokalmusic.ui.theme.LokalMusicTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.mubashshir.lokalmusic.data.model.Result as SongModel

@AndroidEntryPoint
class MainActivity : ComponentActivity()
{

    // Best practice: Inject the Interface (SongRepository)
    // rather than the implementation (SongRepositoryImpl)
    @Inject
    lateinit var songRepository: SongRepository

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Observe the search results flow
        lifecycleScope.launch {
            val searchQuery = "Believer"
            songRepository.searchSongs(searchQuery)
                .collectLatest { result ->
                    result.onSuccess { songs: List<SongModel> ->
                        Log.d(
                            "MainActivity",
                            "Search Success: ${songs.size} songs found"
                        )

                        songs.forEach { song ->
                            // These will now resolve correctly because 'song' is recognized as SongModel
                            Log.d(
                                "MainActivity",
                                "Song Name: ${song.name} | Album: ${song.album.name} | Year: ${song.year}"
                            )
                        }
                    }.onFailure { error ->
                        Log.e(
                            "MainActivity",
                            "Search Error: ${error.message}"
                        )
                    }
                }
        }

        setContent {
            LokalMusicTheme {
                // Your UI code will go here in the next layer
            }
        }
    }
}

