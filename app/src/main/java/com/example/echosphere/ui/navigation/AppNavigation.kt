package com.example.echosphere.ui.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import androidx.compose.ui.Modifier
import com.example.echosphere.ui.screens.HomeScreen
import com.example.echosphere.ui.screens.NowPlayingScreen

@Composable
fun AppNavigation(navController: NavHostController, modifier: Modifier = Modifier) {
    
    NavHost(navController = navController, startDestination = Screen.Home.route, modifier = modifier) {

        composable(Screen.Home.route) {
            HomeScreen(navController)
        }

        composable(Screen.Search.route) {
            Text("Search Screen")
        }

        composable(Screen.Library.route) {
            Text("Library Screen")
        }

        composable(Screen.NowPlaying.route) {
            NowPlayingScreen(navController)
        }

    }
}