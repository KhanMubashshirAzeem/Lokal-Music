// ui/screens/main/MainScreen.kt
package com.mubashshir.lokalmusic.ui.screens.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mubashshir.lokalmusic.ui.common.BottomNavItem
import com.mubashshir.lokalmusic.ui.screens.favorites.FavoritesScreen
import com.mubashshir.lokalmusic.ui.screens.home.HomeScreen
import com.mubashshir.lokalmusic.ui.screens.playlists.PlaylistsScreen
import com.mubashshir.lokalmusic.ui.screens.settings.SettingsScreen


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

    Scaffold(
        bottomBar = {
            NavigationBar() {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination =
                    navBackStackEntry?.destination
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
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination = BottomNavItem.Home.route,
            Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Home.route) { HomeScreen() } // Implement below
            composable(BottomNavItem.Favorites.route) { FavoritesScreen() }
            composable(BottomNavItem.Playlists.route) { PlaylistsScreen() }
            composable(BottomNavItem.Settings.route) { SettingsScreen() }
        }
    }
}
