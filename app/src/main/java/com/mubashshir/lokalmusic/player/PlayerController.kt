package com.mubashshir.lokalmusic.player

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import com.mubashshir.lokalmusic.data.model.Result
import com.mubashshir.lokalmusic.data.model.getStreamUrl
import com.mubashshir.lokalmusic.service.LokalMusicService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@RequiresApi(Build.VERSION_CODES.O)
class PlayerController @Inject constructor(
    @ApplicationContext private val context: Context
)
{

    private var mediaController: MediaController? =
        null

    private val _currentSong =
        MutableStateFlow<Result?>(null)
    val currentSong: StateFlow<Result?> =
        _currentSong

    private val _isPlaying =
        MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration

    private val _playbackPosition =
        MutableStateFlow(0L)
    val playbackPosition: StateFlow<Long> =
        _playbackPosition

    private val scope =
        CoroutineScope(Dispatchers.Main)
    private var positionUpdateJob: Job? = null

    init
    {
        initialize()
    }

    private fun initialize()
    {
        // FIXED: Removed manual startForegroundService call to prevent ANR/Crash.
        // The MediaController will bind to the service automatically.

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
            attachPlayerListener()
            startPositionUpdates()
        }, MoreExecutors.directExecutor())
    }

    private fun attachPlayerListener()
    {
        mediaController?.addListener(object :
            Player.Listener
        {
            override fun onIsPlayingChanged(
                isPlaying: Boolean
            )
            {
                _isPlaying.value = isPlaying
            }

            override fun onPlaybackStateChanged(
                state: Int
            )
            {
                if (state == Player.STATE_READY)
                {
                    _duration.value =
                        mediaController?.duration
                            ?: 0L
                }
            }

            override fun onMediaItemTransition(
                mediaItem: MediaItem?,
                reason: Int
            )
            {
                // Update current song when track changes
                updateCurrentSongFromQueue()
            }
        })
    }

    private fun startPositionUpdates()
    {
        positionUpdateJob?.cancel()
        positionUpdateJob = scope.launch {
            while (isActive)
            {
                mediaController?.let { controller ->
                    if (controller.isPlaying)
                    {
                        _playbackPosition.value =
                            controller.currentPosition
                    }
                }
                delay(1000) // Update every second
            }
        }
    }

    private fun updateCurrentSongFromQueue()
    {
        // This is called when queue changes - for now we rely on playSong/playQueue
        // to set currentSong. In a full implementation, you'd track the queue here.
    }

    fun playSong(song: Result)
    {
        _currentSong.value = song

        val streamUrl =
            song.downloadUrl.getStreamUrl()
        if (streamUrl.isEmpty()) return

        val mediaItem = MediaItem.Builder()
            .setUri(streamUrl)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(song.name)
                    .setArtist(song.artists.primary.firstOrNull()?.name)
                    .setAlbumTitle(song.album.name)
                    .setArtworkUri(
                        Uri.parse(
                            song.image.find { it.quality == "500x500" }?.url
                                ?: song.image.firstOrNull()?.url
                                ?: ""
                        )
                    )
                    .build()
            )
            .build()

        mediaController?.setMediaItem(mediaItem)
        mediaController?.prepare()
        mediaController?.play()
    }

    fun playQueue(
        songs: List<Result>,
        startIndex: Int = 0
    )
    {
        if (songs.isEmpty()) return

        val mediaItems = songs.map { song ->
            MediaItem.Builder()
                .setUri(song.downloadUrl.getStreamUrl())
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(song.name)
                        .setArtist(song.artists.primary.firstOrNull()?.name)
                        .setAlbumTitle(song.album.name)
                        .build()
                )
                .build()
        }

        _currentSong.value = songs[startIndex]

        mediaController?.setMediaItems(
            mediaItems,
            startIndex,
            0L
        )
        mediaController?.prepare()
        mediaController?.play()
    }

    fun togglePlayPause()
    {
        if (_isPlaying.value) mediaController?.pause()
        else mediaController?.play()
    }

    fun next()
    {
        mediaController?.seekToNextMediaItem()
    }

    fun previous()
    {
        mediaController?.seekToPreviousMediaItem()
    }

    fun seekTo(positionMs: Float)
    {
        val position =
            (positionMs * (_duration.value)).toLong()
        mediaController?.seekTo(position)
    }

    fun release()
    {
        positionUpdateJob?.cancel()
        mediaController?.release()
    }
}