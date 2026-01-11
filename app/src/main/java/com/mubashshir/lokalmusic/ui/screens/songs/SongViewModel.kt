package com.mubashshir.lokalmusic.ui.screens.songs

import android.content.ComponentName
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import com.mubashshir.lokalmusic.data.model.Result
import com.mubashshir.lokalmusic.data.model.getStreamUrl
import com.mubashshir.lokalmusic.data.repository.SongRepository
import com.mubashshir.lokalmusic.service.LokalMusicService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongViewModel @Inject constructor(
    private val repository: SongRepository,
    @ApplicationContext private val context: Context
) : ViewModel()
{

    // --- UI STATE ---
    private val _songs =
        MutableStateFlow<List<Result>>(
            emptyList()
        )
    val songs = _songs.asStateFlow()

    private val _currentSong =
        MutableStateFlow<Result?>(null)
    val currentSong = _currentSong.asStateFlow()

    private val _isPlaying =
        MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    // --- MEDIA CONTROLLER ---
    private var mediaController: MediaController? =
        null

    init
    {
        setupMediaController()
    }

    private fun setupMediaController()
    {
        val sessionToken = SessionToken(
            context,
            ComponentName(
                context,
                LokalMusicService::class.java
            )
        )
        val controllerFuture =
            MediaController.Builder(
                context,
                sessionToken
            ).buildAsync()

        controllerFuture.addListener({
            mediaController =
                controllerFuture.get()
            // Sync initial state
            _isPlaying.value =
                mediaController?.isPlaying == true
            // Add listener for playback changes
            mediaController?.addListener(object :
                Player.Listener
            {
                override fun onIsPlayingChanged(
                    isPlaying: Boolean
                )
                {
                    _isPlaying.value = isPlaying
                }
                // Handle song completion, etc.
            })
        }, MoreExecutors.directExecutor())
    }

    fun search(query: String)
    {
        viewModelScope.launch {
            repository.searchSongs(query)
                .collectLatest { result ->
                    result.onSuccess {
                        _songs.value = it
                    }.onFailure {

                    }
                }
        }
    }

    fun playSong(song: Result)
    {
        _currentSong.value = song
        val streamUrl =
            song.downloadUrl.getStreamUrl()

        if (streamUrl.isNotEmpty())
        {
            val mediaItem = MediaItem.Builder()
                .setUri(streamUrl)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(song.name)
                        .setArtist(
                            song.artists.primary.firstOrNull()?.name
                                ?: "Unknown"
                        )
                        .build()
                )
                .build()

            mediaController?.setMediaItem(
                mediaItem
            )
            mediaController?.prepare()
            mediaController?.play()
        }
    }

    fun togglePlayPause()
    {
        if (_isPlaying.value) mediaController?.pause() else mediaController?.play()
    }
}