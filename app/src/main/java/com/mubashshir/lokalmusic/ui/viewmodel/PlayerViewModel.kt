package com.mubashshir.lokalmusic.ui.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.mubashshir.lokalmusic.player.PlayerController
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val playerController: PlayerController
) : ViewModel()
{

    val currentSong = playerController.currentSong
    val isPlaying = playerController.isPlaying
    val duration = playerController.duration
    val playbackPosition =
        playerController.playbackPosition

    fun togglePlayPause()
    {
        playerController.togglePlayPause()
    }

    fun skipNext()
    {
        playerController.next()
    }

    fun skipPrevious()
    {
        playerController.previous()
    }

    fun seekTo(positionMs: Float)
    {
        playerController.seekTo(positionMs)
    }
}
