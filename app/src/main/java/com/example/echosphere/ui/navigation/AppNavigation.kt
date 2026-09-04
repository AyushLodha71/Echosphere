package com.example.echosphere.ui.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import androidx.compose.ui.Modifier
import com.example.echosphere.ui.screens.HomeScreen
import com.example.echosphere.ui.screens.NowPlayingScreen
import com.example.echosphere.ui.screens.SearchScreen
import com.example.echosphere.viewmodel.PlayerViewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    playerViewModel: PlayerViewModel,
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
            Text("Library Screen")
        }

        composable(Screen.NowPlaying.route) {
            NowPlayingScreen(navController, playerViewModel)
        }

    }
}