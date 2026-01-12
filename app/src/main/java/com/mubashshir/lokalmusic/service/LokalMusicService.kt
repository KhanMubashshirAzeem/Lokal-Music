package com.mubashshir.lokalmusic.service

import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LokalMusicService : MediaSessionService() {

    @Inject
    lateinit var player: ExoPlayer

    private var mediaSession: MediaSession? = null

    override fun onCreate() {
        super.onCreate()

        // Build the MediaSession with a Callback to handle connections
        mediaSession =
            MediaSession.Builder(this, player)
                .setCallback(
                    CustomMediaSessionCallback()
                )
                .build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }

    // Custom Callback to handle connection requests
    private inner class CustomMediaSessionCallback :
        MediaSession.Callback
    {
        override fun onConnect(
            session: MediaSession,
            controller: MediaSession.ControllerInfo
        ): MediaSession.ConnectionResult
        {
            // Accept all connection requests from the app and system
            return MediaSession.ConnectionResult.AcceptedResultBuilder(
                session
            ).build()
        }
    }
}