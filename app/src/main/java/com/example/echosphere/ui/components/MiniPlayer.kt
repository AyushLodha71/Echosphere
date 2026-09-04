package com.example.echosphere.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.echosphere.ui.navigation.Screen
import com.example.echosphere.viewmodel.PlayerViewModel

@Composable
fun MiniPlayer(navController: NavHostController, playerViewModel: PlayerViewModel) {

    // Observe the real current song + play state from the ViewModel
    val currentSong by playerViewModel.currentSong.collectAsState()
    val isPlaying by playerViewModel.isPlaying.collectAsState()

    // If nothing is playing yet, don't show the mini player at all
    if (currentSong == null) return

    val song = currentSong!!

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
                model = "https://i.ytimg.com/vi/${song.thumbnailId}/hqdefault.jpg",
                contentDescription = song.title,
                modifier = Modifier.size(56.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = song.artist,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            IconButton(onClick = { }) {
                Icon(Icons.Default.FavoriteBorder, contentDescription = "Like")
            }

            IconButton(onClick = { playerViewModel.togglePlayPause() }) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play"
                )
            }
        }

        LinearProgressIndicator(
            progress = { 0.3f },   // still fake progress; real progress comes later
            modifier = Modifier.fillMaxWidth()
        )
    }
}