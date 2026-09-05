package com.ayushlodha.echosphere

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ayushlodha.echosphere.ui.components.BottomNav
import com.ayushlodha.echosphere.ui.components.MiniPlayer
import com.ayushlodha.echosphere.ui.navigation.AppNavigation
import com.ayushlodha.echosphere.ui.navigation.Screen
import com.ayushlodha.echosphere.ui.theme.EchosphereTheme
import com.ayushlodha.echosphere.viewmodel.PlayerViewModel
import com.ayushlodha.echosphere.viewmodel.PlaylistViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EchosphereTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val playerViewModel: PlayerViewModel = viewModel()
                val playlistViewModel: PlaylistViewModel = viewModel()

                Scaffold(modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        Column {
                            if (currentRoute != Screen.NowPlaying.route) {
                                MiniPlayer(navController, playerViewModel)
                                BottomNav(navController)
                            }
                        }
                    }) { innerPadding ->
                    AppNavigation(
                        navController = navController,
                        playerViewModel = playerViewModel,
                        playlistViewModel = playlistViewModel,
                        modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}