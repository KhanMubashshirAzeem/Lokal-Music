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
import com.mubashshir.lokalmusic.data.model.Results
import com.mubashshir.lokalmusic.data.model.getStreamUrl
import com.mubashshir.lokalmusic.service.LokalMusicService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@RequiresApi(Build.VERSION_CODES.O)
class PlayerController @Inject constructor(
    @param:ApplicationContext private val context: Context
)
{

    private var mediaController: MediaController? =
        null

    // --- State Flows ---

    private val _currentSong =
        MutableStateFlow<Results?>(null)
    val currentSong: StateFlow<Results?> =
        _currentSong.asStateFlow()

    private val _isPlaying =
        MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> =
        _isPlaying.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> =
        _duration.asStateFlow()

    private val _playbackPosition =
        MutableStateFlow(0L)
    val playbackPosition: StateFlow<Long> =
        _playbackPosition.asStateFlow()

    private val _shuffleMode =
        MutableStateFlow(false)
    val shuffleMode: StateFlow<Boolean> =
        _shuffleMode.asStateFlow()

    private val _repeatMode =
        MutableStateFlow(Player.REPEAT_MODE_OFF)
    val repeatMode: StateFlow<Int> =
        _repeatMode.asStateFlow()

    // --- Internal State ---

    // We keep a local copy of the queue to map MediaItems back to Result objects
    private var currentQueue: List<Results> =
        emptyList()

    private val scope =
        CoroutineScope(Dispatchers.Main)
    private var positionUpdateJob: Job? = null

    init
    {
        initialize()
    }

    private fun initialize()
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
                // When the player moves to the next song (automatically or manually),
                // we use the mediaId to find the Result object in our queue.
                mediaItem?.let { item ->
                    val song =
                        currentQueue.find { it.id == item.mediaId }
                    if (song != null)
                    {
                        _currentSong.value = song
                    }
                }
            }

            override fun onShuffleModeEnabledChanged(
                shuffleModeEnabled: Boolean
            )
            {
                _shuffleMode.value =
                    shuffleModeEnabled
            }

            override fun onRepeatModeChanged(
                repeatMode: Int
            )
            {
                _repeatMode.value = repeatMode
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
                delay(500) // Update every second
            }
        }
    }

    // --- Playback Controls ---

    fun playSong(song: Results)
    {
        // Playing a single song is just playing a queue of size 1
        playQueue(listOf(song), 0)
    }

    fun playQueue(
        songs: List<Results>,
        startIndex: Int = 0
    )
    {
        if (songs.isEmpty()) return

        currentQueue = songs
        _currentSong.value = songs[startIndex]

        val mediaItems = songs.map { song ->
            MediaItem.Builder()
                .setMediaId(song.id) // IMPORTANT: Set ID for mapping back later
                .setUri(song.downloadUrl.getStreamUrl()) // Use streaming URL
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
        }

        mediaController?.let { controller ->
            controller.setMediaItems(
                mediaItems,
                startIndex,
                0L
            )
            controller.prepare()
            controller.play()
        }
    }

    fun togglePlayPause()
    {
        mediaController?.let {
            if (it.isPlaying) it.pause() else it.play()
        }
    }

    fun next()
    {
        if (mediaController?.hasNextMediaItem() == true)
        {
            mediaController?.seekToNextMediaItem()
        }
    }

    fun previous()
    {
        if (mediaController?.hasPreviousMediaItem() == true)
        {
            mediaController?.seekToPreviousMediaItem()
        }
    }

    fun seekTo(positionMs: Float)
    {
        val position =
            (positionMs * (_duration.value)).toLong()
        mediaController?.seekTo(position)
    }

    // --- Shuffle & Repeat ---

    fun toggleShuffle()
    {
        mediaController?.let {
            val isEnabled = !it.shuffleModeEnabled
            it.shuffleModeEnabled = isEnabled
        }
    }

    fun toggleRepeat()
    {
        mediaController?.let {
            val nextMode = when (it.repeatMode)
            {
                Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
                Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
                Player.REPEAT_MODE_ONE -> Player.REPEAT_MODE_OFF
                else                   -> Player.REPEAT_MODE_OFF
            }
            it.repeatMode = nextMode
        }
    }

    fun release()
    {
        positionUpdateJob?.cancel()
        mediaController?.release()
    }
}