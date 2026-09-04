package com.example.echosphere.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.echosphere.data.model.Song
import com.example.echosphere.data.remote.RetrofitClient
import com.example.echosphere.data.remote.YoutubeExtractor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    private val player = ExoPlayer.Builder(application).build()

    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> = _currentSong.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _status = MutableStateFlow("")
    val status: StateFlow<String> = _status.asStateFlow()

    init {
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.value = isPlaying
            }

            override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                android.util.Log.e("PlayerViewModel", "Playback error: ${error.message}", error)
                _status.value = "Playback error"
            }
        })
    }

    // Fetch the stream URL from the backend, then play it.
    fun play(song: Song) {

        if (_currentSong.value?.id == song.id) {
            if (!player.isPlaying) player.play()
            return
        }

        _currentSong.value = song
        _status.value = "Loading..."

        viewModelScope.launch {
            try {
                val response = RetrofitClient.api.getStream(
                    youtubeId = song.id,
                    title = song.title,
                    artist = song.artist
                )

                if (response.isSuccessful) {
                    val streamUrl = response.body()?.streamUrl
                    if (streamUrl != null) {
                        playUrl(streamUrl)
                        _status.value = ""
                    } else {
                        _status.value = "No URL in response"
                    }
                } else if (response.code() == 404) {
                    _status.value = "Extracting..."
                    val extractedUrl = YoutubeExtractor.getAudioUrl(song.id)
                    if (extractedUrl != null) {
                        playUrl(extractedUrl)
                        _status.value = ""
                    } else {
                        _status.value = "Could not extract audio"
                    }
                } else {
                    _status.value = "Server error: ${response.code()}"
                }
            } catch (e: Exception) {
                android.util.Log.e("PlayerViewModel", "Fetch error", e)
                _status.value = "Network error: ${e.message}"
            }
        }
    }

    private fun playUrl(url: String) {
        val mediaItem = MediaItem.fromUri(url)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }

    fun togglePlayPause() {
        if (player.isPlaying) {
            player.pause()
        } else {
            player.play()
        }
    }

    override fun onCleared() {
        super.onCleared()
        player.release()
    }
}