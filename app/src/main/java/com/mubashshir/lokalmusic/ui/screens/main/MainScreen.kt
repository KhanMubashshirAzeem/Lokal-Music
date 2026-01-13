package com.mubashshir.lokalmusic.ui.screens.main

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mubashshir.lokalmusic.ui.components.BottomNavItem
import com.mubashshir.lokalmusic.ui.components.ConnectivityBanner
import com.mubashshir.lokalmusic.ui.navigation.AppNavigation
import com.mubashshir.lokalmusic.ui.navigation.Screen
import com.mubashshir.lokalmusic.ui.screens.player.MiniPlayer

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(
    navController: NavHostController = rememberNavController(),
    mainViewModel: MainViewModel = hiltViewModel() // Inject MainViewModel
) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Favorites,
        BottomNavItem.Playlists,
        BottomNavItem.Settings
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Observe Network Status
    val networkStatus by mainViewModel.networkStatus.collectAsState()

    // Logic to hide BottomBar on detail screens
    val shouldShowBottomNav = currentDestination?.route?.let { route ->
        route != Screen.FullPlayer.route &&
                !route.startsWith("artist_detail") &&
                !route.startsWith("album_detail") &&
                route != Screen.Search.route
    } ?: true

    // Logic to show MiniPlayer
    val shouldShowMiniPlayer =
        currentDestination?.route != Screen.FullPlayer.route

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

            // Content
            Column(
                modifier = Modifier.padding(
                    innerPadding
                )
            ) {
                // Show Banner at the top of the content area
                ConnectivityBanner(
                    status = networkStatus,
                    modifier = Modifier
                        .fillMaxWidth()
                        .zIndex(1f)
                )

                // Navigation Graph
                Box(modifier = Modifier.weight(1f)) {
                    AppNavigation(
                        navController = navController,
                        paddingValues = androidx.compose.foundation.layout.PaddingValues() // Pass empty padding as we handled it in Column
                    )
                }
            }

            // MiniPlayer Overlay
            if (shouldShowMiniPlayer)
            {
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