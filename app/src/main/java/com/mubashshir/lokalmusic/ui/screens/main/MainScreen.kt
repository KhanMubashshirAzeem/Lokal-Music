// ui/screens/main/MainScreen.kt
package com.mubashshir.lokalmusic.ui.screens.main

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mubashshir.lokalmusic.ui.components.BottomNavItem
import com.mubashshir.lokalmusic.ui.components.MiniPlayer
import com.mubashshir.lokalmusic.ui.navigation.Screen
import com.mubashshir.lokalmusic.ui.screens.FavoritesScreen
import com.mubashshir.lokalmusic.ui.screens.PlaylistsScreen
import com.mubashshir.lokalmusic.ui.screens.SettingsScreen
import com.mubashshir.lokalmusic.ui.screens.album.AlbumDetailScreen
import com.mubashshir.lokalmusic.ui.screens.home.HomeScreen
import com.mubashshir.lokalmusic.ui.screens.home.tab_screen.artists.ArtistDetailScreen
import com.mubashshir.lokalmusic.ui.screens.search.SearchScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(
    navController: NavHostController = rememberNavController()
)
{
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Favorites,
        BottomNavItem.Playlists,
        BottomNavItem.Settings
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination =
        navBackStackEntry?.destination

    // Hide bottom nav for full-screen destinations
    val shouldShowBottomNav =
        currentDestination?.route?.let { route ->
            route != Screen.FullPlayer.route &&
                    !route.startsWith(
                        Screen.ArtistDetail.route.split(
                            "/"
                        )[0]
                    ) &&
                    !route.startsWith(
                        Screen.AlbumDetail.route.split(
                            "/"
                        )[0]
                    ) &&
                    route != Screen.Search.route
        } ?: true

    Scaffold(
        bottomBar = {
            if (shouldShowBottomNav)
            {
                NavigationBar() {
                    items.forEach { item ->
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    item.icon,
                                    contentDescription = null
                                )
                            },
                            label = { Text(item.label) },
                            selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                            onClick = {
                                navController.navigate(
                                    item.route
                                ) {
                                    popUpTo(
                                        navController.graph.findStartDestination().id
                                    ) {
                                        saveState =
                                            true
                                    }
                                    launchSingleTop =
                                        true
                                    restoreState =
                                        true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            NavHost(
                navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.padding(
                    innerPadding
                )
            ) {
                composable(Screen.Home.route) {
                    HomeScreen(
                        onNavigateToSearch = {
                            navController.navigate(
                                Screen.Search.route
                            )
                        },
                        onNavigateToArtist = { artistId ->
                            navController.navigate(
                                Screen.ArtistDetail.createRoute(
                                    artistId
                                )
                            )
                        },
                        onNavigateToAlbum = { albumId ->
                            navController.navigate(
                                Screen.AlbumDetail.createRoute(
                                    albumId
                                )
                            )
                        },
                        onNavigateToFullPlayer = {
                            navController.navigate(
                                Screen.FullPlayer.route
                            )
                        }
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
                composable(Screen.Search.route) {
                    SearchScreen(
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToFullPlayer = {
                            navController.navigate(
                                Screen.FullPlayer.route
                            )
                        }
                    )
                }
                composable(
                    route = Screen.ArtistDetail.route,
                    arguments = listOf(
                        navArgument(
                            "artistId"
                        ) {
                            type =
                                NavType.StringType
                        })
                ) { backStackEntry ->
                    val artistId =
                        backStackEntry.arguments?.getString(
                            "artistId"
                        ) ?: ""
                    ArtistDetailScreen(
                        artistId = artistId,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable(
                    route = Screen.AlbumDetail.route,
                    arguments = listOf(
                        navArgument(
                            "albumId"
                        ) {
                            type =
                                NavType.StringType
                        })
                ) { backStackEntry ->
                    val albumId =
                        backStackEntry.arguments?.getString(
                            "albumId"
                        ) ?: ""
                    AlbumDetailScreen(
                        albumId = albumId,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable(Screen.FullPlayer.route) {
                    com.mubashshir.lokalmusic.ui.components.FullPlayerScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }

            // MiniPlayer - only show when not on full-screen destinations
            if (shouldShowBottomNav)
            {
                MiniPlayer(
                    onNavigateToFullPlayer = {
                        navController.navigate(
                            Screen.FullPlayer.route
                        )
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(innerPadding)
                )
            }
        }
    }
}
