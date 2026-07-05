package com.example.echosphere.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.echosphere.data.model.Song
import com.example.echosphere.ui.navigation.Screen

@Composable
fun MiniPlayer(navController: NavHostController) {

    val nowPlayingSong = Song(id = "1", title = "Blinding Lights", artist = "The Weeknd", thumbnailId = "4NRXx6U8ABQ", duration = 200000, streamUrl = null)
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    navController.navigate(Screen.NowPlaying.route)
                }
        ) {
            AsyncImage(
                model = "https://i.ytimg.com/vi/${nowPlayingSong.thumbnailId}/hqdefault.jpg",
                contentDescription = nowPlayingSong.title,
                modifier = Modifier
                    .size(56.dp)
            )

            Column(
                modifier = Modifier.weight(1f)    // takes remaining space, pushes button to edge
            ) {
                Text(
                    text = nowPlayingSong.title,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = nowPlayingSong.artist,
                    style = MaterialTheme.typography.bodySmall
                )
            }


            IconButton(onClick = { }) {
                Icon(Icons.Default.FavoriteBorder, contentDescription = "Like")
            }

            IconButton(onClick = { }) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Play")
            }
        }

        LinearProgressIndicator(
            progress = { 0.3f },    // fake 30% progress for now
            modifier = Modifier.fillMaxWidth()
        )

    }

}