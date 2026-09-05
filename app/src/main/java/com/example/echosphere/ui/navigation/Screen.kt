package com.example.echosphere.ui.navigation


sealed class Screen(val route: String) {
    object Home: Screen("home")
    object Search: Screen("search")
    object Library: Screen("library")
    object NowPlaying: Screen("now_playing")
    object Playlist : Screen("playlist/{playlistId}") {
        fun createRoute(playlistId: Long) = "playlist/$playlistId"
    }
}