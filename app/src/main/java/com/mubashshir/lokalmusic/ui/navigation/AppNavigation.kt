package com.mubashshir.lokalmusic.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mubashshir.lokalmusic.ui.components.FullPlayerScreen
import com.mubashshir.lokalmusic.ui.screens.FavoritesScreen
import com.mubashshir.lokalmusic.ui.screens.PlaylistsScreen
import com.mubashshir.lokalmusic.ui.screens.SettingsScreen
import com.mubashshir.lokalmusic.ui.screens.home.HomeScreen
import com.mubashshir.lokalmusic.ui.screens.home.tab_screen.album.AlbumDetailScreen
import com.mubashshir.lokalmusic.ui.screens.home.tab_screen.artists.ArtistDetailScreen
import com.mubashshir.lokalmusic.ui.screens.search.SearchScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(
    navController: NavHostController,
    paddingValues: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = Modifier.padding(paddingValues)
    ) {
        // --- Bottom Nav Tabs ---
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                onNavigateToArtist = { artistId ->
                    navController.navigate(Screen.ArtistDetail.createRoute(artistId))
                },
                onNavigateToAlbum = { albumId ->
                    navController.navigate(Screen.AlbumDetail.createRoute(albumId))
                },
                onNavigateToFullPlayer = { navController.navigate(Screen.FullPlayer.route) }
            )
        }

        composable(Screen.Favorites.route) {
            FavoritesScreen()
        }

        composable(Screen.Playlists.route) {
            PlaylistsScreen()
        }

        composable(Screen.Settings.route) {
            SettingsScreen()
        }

        // --- Detail Screens ---

        composable(Screen.Search.route) {
            SearchScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToFullPlayer = { navController.navigate(Screen.FullPlayer.route) }
            )
        }

        composable(
            route = Screen.ArtistDetail.route,
            arguments = listOf(navArgument("artistId") { type = NavType.StringType })
        ) { backStackEntry ->
            val artistId = backStackEntry.arguments?.getString("artistId") ?: ""
            ArtistDetailScreen(
                artistId = artistId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.AlbumDetail.route,
            arguments = listOf(navArgument("albumId") { type = NavType.StringType })
        ) { backStackEntry ->
            val albumId = backStackEntry.arguments?.getString("albumId") ?: ""
            AlbumDetailScreen(
                albumId = albumId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.FullPlayer.route) {
            FullPlayerScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}