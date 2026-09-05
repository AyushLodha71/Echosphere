package com.example.echosphere.ui.screens
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import coil.compose.AsyncImage
import com.example.echosphere.data.model.Song
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Slider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.pointer.pointerInput
import androidx.navigation.NavController
import com.example.echosphere.viewmodel.PlayerViewModel
import com.example.echosphere.viewmodel.PlaylistViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.material.icons.filled.Pause
import androidx.compose.foundation.clickable
import androidx.compose.material3.AlertDialog
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NowPlayingScreen(
    navController: NavController,
    playerViewModel: PlayerViewModel,
    playlistViewModel: PlaylistViewModel
) {
    val currentSong by playerViewModel.currentSong.collectAsState()
    val isPlaying by playerViewModel.isPlaying.collectAsState()
    if (currentSong == null) {
        Text("Nothing playing")
        return
    }
    val nowPlayingSong = currentSong!!
    val isLiked by playlistViewModel.isSongLiked(nowPlayingSong.id).collectAsState(initial = false)
    val repeatOne by playerViewModel.repeatOne.collectAsState()
    val sleepTimerMinutes by playerViewModel.sleepTimerMinutes.collectAsState()
    val userPlaylists by playlistViewModel.playlists.collectAsState(initial = emptyList())
    var showSleepDialog by remember { mutableStateOf(false) }
    var showAddToPlaylistDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    // -----------------------------------------
    // Bottom sheet state
    // showSheet   → controls whether sheet is visible
    // sheetState  → tracks expanded/collapsed state
    // selectedTab → which tab is active (0=Playlist, 1=Lyrics, 2=Related)
    // -----------------------------------------
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    var selectedTab by remember { mutableStateOf(0) }
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ){
        // ┌-----------------------------┐
        // │  ↓ (back)          ⋮ (more) │
        // └-----------------------------┘
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Back")
            }
            IconButton(onClick = { showAddToPlaylistDialog = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "More")
            }
        }
        // ┌-----------------------------┐
        // │                             │
        // │        [Album Art]          │
        // │                             │
        // └-----------------------------┘
        AsyncImage(
            model = "https://i.ytimg.com/vi/${nowPlayingSong.thumbnailId}/hqdefault.jpg",
            contentDescription = nowPlayingSong.title,
            modifier = Modifier.size(400.dp)
        )
        // ┌-----------------------------┐
        // │  Song Title             ♡   │
        // │  Artist Name                │
        // └-----------------------------┘
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = nowPlayingSong.title,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = nowPlayingSong.artist,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            IconButton(onClick = { playlistViewModel.toggleLike(nowPlayingSong, isLiked) }) {
                Icon(
                    imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isLiked) "Unlike" else "Like"
                )
            }
        }
        // ┌-----------------------------┐
        // │  ----●------------------    │  ← progress slider
        // │  0:00                3:21   │  ← timestamps
        // └-----------------------------┘
        Slider(
            value = 0.3f,
            onValueChange = { }
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "0:00",
            )
            Text(
                text = "3:21",
            )
        }
        // ┌-----------------------------┐
        // │  🔀   ⏮   ▶   ⏭   ⏱     │  ← playback controls
        // └-----------------------------┘
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            IconButton(onClick = { }) {
                Icon(Icons.Default.Shuffle, contentDescription = "Shuffle")
            }
            IconButton(onClick = { playerViewModel.playPrevious() }) {
                Icon(Icons.Default.SkipPrevious, contentDescription = "PreviousSong")
            }
            IconButton(onClick = { playerViewModel.togglePlayPause() }) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play"
                )
            }
            IconButton(onClick = { playerViewModel.playNext() }) {
                Icon(Icons.Default.SkipNext, contentDescription = "NextSong")
            }
            IconButton(onClick = { playerViewModel.toggleRepeatOne() }) {
                Icon(
                    Icons.Default.Repeat,
                    contentDescription = if (repeatOne) "Repeat one on" else "Repeat one off",
                    tint = if (repeatOne)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurface
                )
            }
            IconButton(onClick = { showSleepDialog = true }) {
                Icon(
                    Icons.Default.Timer,
                    contentDescription = if (sleepTimerMinutes != null) "Sleep timer on" else "Sleep timer",
                    tint = if (sleepTimerMinutes != null)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurface
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        // ┌-----------------------------┐
        // │          ━━━━               │  ← drag handle (visual hint)
        // │  Playlist  Lyrics  Related  │  ← tab triggers
        // └-----------------------------┘
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectVerticalDragGestures { _, dragAmount ->
                        if (dragAmount < -10) {
                            showSheet = true
                        }
                    }
                }
        ) {
            HorizontalDivider(
                modifier = Modifier
                    .width(40.dp)
                    .padding(vertical = 8.dp),
                thickness = 4.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                listOf("Playlist", "Lyrics", "Related").forEachIndexed { index, label ->
                    TextButton(
                        onClick = { selectedTab = index; showSheet = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.titleSmall,
                            color = if (selectedTab == index)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
    if (showAddToPlaylistDialog) {
        AlertDialog(
            onDismissRequest = { showAddToPlaylistDialog = false },
            title = { Text("Add to playlist") },
            text = {
                if (userPlaylists.isEmpty()) {
                    Text("No playlists yet. Create one in your library first.")
                } else {
                    Column {
                        userPlaylists.forEach { playlist ->
                            Text(
                                text = playlist.name,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        playlistViewModel.addSongToPlaylist(playlist.id, nowPlayingSong)
                                        Toast.makeText(
                                            context,
                                            "Added to ${playlist.name}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        showAddToPlaylistDialog = false
                                    }
                                    .padding(vertical = 12.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = { },
            dismissButton = {
                TextButton(onClick = { showAddToPlaylistDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    if (showSleepDialog) {
        AlertDialog(
            onDismissRequest = { showSleepDialog = false },
            title = { Text("Sleep timer") },
            text = {
                Column {
                    listOf(15, 30, 45, 60).forEach { minutes ->
                        Text(
                            text = "$minutes minutes",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    playerViewModel.setSleepTimer(minutes)
                                    Toast.makeText(context, "Sleep timer set for $minutes min", Toast.LENGTH_SHORT).show()
                                    showSleepDialog = false
                                }
                                .padding(vertical = 12.dp)
                        )
                    }
                    if (sleepTimerMinutes != null) {
                        Text(
                            text = "Turn off",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    playerViewModel.cancelSleepTimer()
                                    showSleepDialog = false
                                }
                                .padding(vertical = 12.dp)
                        )
                    }
                }
            },
            confirmButton = { },
            dismissButton = {
                TextButton(onClick = { showSleepDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    // -----------------------------------------
    // Bottom Sheet
    // -----------------------------------------
    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState
        ) {
            HorizontalDivider(
                modifier = Modifier
                    .width(40.dp)
                    .padding(vertical = 8.dp),
                thickness = 4.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                listOf("Playlist", "Lyrics", "Related").forEachIndexed { index, label ->
                    TextButton(
                        onClick = { selectedTab = index; showSheet = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.titleSmall,
                            color = if (selectedTab == index)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
            when (selectedTab) {
                0 -> PlaylistTab()
                1 -> LyricsTab()
                2 -> RelatedTab()
            }
        }
    }
}
// -----------------------------------------
// Tab content composables
// -----------------------------------------
@Composable
fun PlaylistTab() {
    Text("Playlist coming soon", modifier = Modifier.padding(16.dp))
}
@Composable
fun LyricsTab() {
    Text("Lyrics coming soon", modifier = Modifier.padding(16.dp))
}
@Composable
fun RelatedTab() {
    Text("Related coming soon", modifier = Modifier.padding(16.dp))
}