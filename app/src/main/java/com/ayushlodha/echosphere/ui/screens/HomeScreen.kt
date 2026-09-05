package com.ayushlodha.echosphere.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import coil.compose.AsyncImage
import com.ayushlodha.echosphere.data.model.Song
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.width
import androidx.navigation.NavController
import com.ayushlodha.echosphere.ui.navigation.Screen
import com.ayushlodha.echosphere.viewmodel.PlayerViewModel

@Composable
fun HomeScreen(navController: NavController, playerViewModel: PlayerViewModel) {

    val fakeSongs = listOf(
        Song(id = "dQw4w9WgXcQ", title = "Never Gonna Give You Up", artist = "Rick Astley", thumbnailId = "dQw4w9WgXcQ", duration = 200000, streamUrl = null),
        Song(id = "9bZkp7q19f0", title = "Gangnam Style", artist = "PSY", thumbnailId = "9bZkp7q19f0", duration = 252000, streamUrl = null),
        Song(id = "kJQP7kiw5Fk", title = "Despacito", artist = "Luis Fonsi", thumbnailId = "kJQP7kiw5Fk", duration = 282000, streamUrl = null),
    Song(id = "eVli-tstM5E", title = "Espresso", artist = "Sabrina Carpenter", thumbnailId = "eVli-tstM5E", duration = 175000, streamUrl = null)
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(fakeSongs) { song ->
            Column(
                modifier = Modifier
                    .width(120.dp)
                    .clickable {
                        playerViewModel.play(song)
                        navController.navigate(Screen.NowPlaying.route)
                    }
            ) {
                AsyncImage(
                    model = "https://i.ytimg.com/vi/${song.thumbnailId}/hqdefault.jpg",
                    contentDescription = song.title,
                    modifier = Modifier
                        .size(120.dp)
                )
                Text(song.title)
                Text(song.artist)
            }
        }
    }
}