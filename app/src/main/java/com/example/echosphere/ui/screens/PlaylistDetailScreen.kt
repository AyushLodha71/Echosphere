package com.example.echosphere.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.echosphere.viewmodel.PlayerViewModel
import com.example.echosphere.viewmodel.PlaylistViewModel

@Composable
fun PlaylistDetailScreen(
    playlistId: Long,
    playlistViewModel: PlaylistViewModel,
    playerViewModel: PlayerViewModel,
    navController: NavController
) {
    val songs by playlistViewModel.getSongsInPlaylist(playlistId)
        .collectAsState(initial = emptyList())

    val playlist by playlistViewModel.getPlaylist(playlistId)
        .collectAsState(initial = null)

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Back")
            }
            Text(
                text = playlist?.name ?: "",
                style = MaterialTheme.typography.titleLarge
            )
        }

        if (songs.isEmpty()) {
            Text("No songs yet", modifier = Modifier.padding(16.dp))
        } else {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                itemsIndexed(songs) { index, song ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                playerViewModel.play(songs, index)
                            }
                            .padding(vertical = 8.dp)
                    ) {
                        AsyncImage(
                            model = "https://i.ytimg.com/vi/${song.thumbnailId}/hqdefault.jpg",
                            contentDescription = song.title,
                            modifier = Modifier.size(56.dp)
                        )
                        Column(modifier = Modifier.padding(start = 12.dp)) {
                            Text(
                                text = song.title,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = song.artist,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}