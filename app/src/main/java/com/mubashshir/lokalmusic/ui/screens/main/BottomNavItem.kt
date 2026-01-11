package com.mubashshir.lokalmusic.ui.screens.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)
{
    object Home : BottomNavItem(
        "home",
        Icons.Default.Home,
        "Home"
    )

    object Favorites : BottomNavItem(
        "favorites",
        Icons.Default.Favorite,
        "Favorites"
    )

    object Playlists : BottomNavItem(
        "playlists",
        Icons.Default.List,
        "Playlists"
    )

    object Settings : BottomNavItem(
        "settings",
        Icons.Default.Settings,
        "Settings"
    )
}