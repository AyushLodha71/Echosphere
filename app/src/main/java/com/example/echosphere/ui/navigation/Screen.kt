package com.example.echosphere.ui.navigation


sealed class Screen(val route: String) {
    object Home: Screen("home")
    object Search: Screen("search")
    object Library: Screen("library")
    object NowPlaying: Screen("nowlaying")
}