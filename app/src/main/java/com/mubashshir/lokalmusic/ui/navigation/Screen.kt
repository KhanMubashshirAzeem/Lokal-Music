package com.mubashshir.lokalmusic.ui.navigation

/**
 * Navigation routes for the app.
 * All routes should be defined here as sealed class objects.
 */
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Favorites : Screen("favorites")
    object Playlists : Screen("playlists")
    object Settings : Screen("settings")
    
    // Detail screens
    object Search : Screen("search")
    object FullPlayer : Screen("full_player")
    object ArtistDetail : Screen("artist_detail/{artistId}") {
        fun createRoute(artistId: String) = "artist_detail/$artistId"
    }
    object AlbumDetail : Screen("album_detail/{albumId}") {
        fun createRoute(albumId: String) = "album_detail/$albumId"
    }
}