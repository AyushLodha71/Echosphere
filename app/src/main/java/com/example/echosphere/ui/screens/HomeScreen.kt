package com.example.echosphere.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import coil.compose.AsyncImage
import com.example.echosphere.data.model.Song
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.width
import androidx.navigation.NavController
import com.example.echosphere.ui.navigation.Screen

@Composable
fun HomeScreen(navController: NavController) {

    val fakeSongs = listOf(
        Song(id = "1", title = "Blinding Lights", artist = "The Weeknd", thumbnailId = "4NRXx6U8ABQ", duration = 200000, streamUrl = null),
        Song(id = "2", title = "APT.", artist = "ROSÉ & Bruno Mars", thumbnailId = "ekr2nIex040", duration = 177000, streamUrl = null),
        Song(id = "3", title = "Espresso", artist = "Sabrina Carpenter", thumbnailId = "eVli-tstM5E", duration = 175000, streamUrl = null)
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(fakeSongs) { song ->
            Column(
                modifier = Modifier
                    .width(120.dp)
                    .clickable {
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