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
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mubashshir.lokalmusic.ui.components.BottomNavItem
import com.mubashshir.lokalmusic.ui.components.MiniPlayer
import com.mubashshir.lokalmusic.ui.navigation.AppNavigation
import com.mubashshir.lokalmusic.ui.navigation.Screen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(
    navController: NavHostController = rememberNavController()
) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Favorites,
        BottomNavItem.Playlists,
        BottomNavItem.Settings
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Logic to hide BottomBar on detail screens
    val shouldShowBottomNav = currentDestination?.route?.let { route ->
        route != Screen.FullPlayer.route &&
                !route.startsWith("artist_detail") && // Simplified check
                !route.startsWith("album_detail") &&  // Simplified check
                route != Screen.Search.route
    } ?: true

    Scaffold(
        bottomBar = {
            if (shouldShowBottomNav) {
                NavigationBar {
                    items.forEach { item ->
                        val isSelected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = null) },
                            label = { Text(item.label) },
                            selected = isSelected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // 1. Load the Navigation Graph
            AppNavigation(
                navController = navController,
                paddingValues = innerPadding
            )

            // 2. Overlay the MiniPlayer at the bottom
            if (shouldShowBottomNav) {
                MiniPlayer(
                    onNavigateToFullPlayer = {
                        navController.navigate(Screen.FullPlayer.route)
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = innerPadding.calculateBottomPadding())
                )
            }
        }
    }
}