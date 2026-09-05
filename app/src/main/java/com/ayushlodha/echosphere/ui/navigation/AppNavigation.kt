package com.ayushlodha.echosphere.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.ayushlodha.echosphere.ui.screens.HomeScreen
import com.ayushlodha.echosphere.ui.screens.LibraryScreen
import com.ayushlodha.echosphere.ui.screens.NowPlayingScreen
import com.ayushlodha.echosphere.ui.screens.SearchScreen
import com.ayushlodha.echosphere.viewmodel.PlayerViewModel
import com.ayushlodha.echosphere.viewmodel.PlaylistViewModel
import com.ayushlodha.echosphere.ui.screens.PlaylistDetailScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    playerViewModel: PlayerViewModel,
    playlistViewModel: PlaylistViewModel,
    modifier: Modifier = Modifier
) {

    NavHost(navController = navController, startDestination = Screen.Home.route, modifier = modifier) {

        composable(Screen.Home.route) {
            HomeScreen(navController, playerViewModel)
        }

        composable(Screen.Search.route) {
            SearchScreen(playerViewModel = playerViewModel)
        }

        composable(Screen.Library.route) {
            LibraryScreen(playlistViewModel = playlistViewModel, navController = navController)
        }

        composable(Screen.NowPlaying.route) {
            NowPlayingScreen(navController, playerViewModel, playlistViewModel)
        }

        composable(
            route = Screen.Playlist.route,
            arguments = listOf(navArgument("playlistId") { type = NavType.LongType })
        ) { backStackEntry ->
            val playlistId = backStackEntry.arguments?.getLong("playlistId") ?: return@composable
            PlaylistDetailScreen(playlistId, playlistViewModel, playerViewModel, navController)
        }

    }
}