package com.example.echosphere.viewmodel

import android.app.Application
import android.content.ComponentName
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.echosphere.PlaybackService
import com.example.echosphere.data.model.Song
import com.example.echosphere.data.remote.RetrofitClient
import com.example.echosphere.data.remote.YoutubeExtractor
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    private var controller: MediaController? = null
    private val controllerFuture: ListenableFuture<MediaController>

    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> = _currentSong.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _status = MutableStateFlow("")
    val status: StateFlow<String> = _status.asStateFlow()

    private var queue: List<Song> = emptyList()
    private var currentIndex: Int = 0

    init {
        val sessionToken = SessionToken(
            application,
            ComponentName(application, PlaybackService::class.java)
        )
        controllerFuture = MediaController.Builder(application, sessionToken).buildAsync()
        controllerFuture.addListener(
            {
                val newController = controllerFuture.get()
                controller = newController

                newController.addListener(object : Player.Listener {
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        _isPlaying.value = isPlaying
                    }

                    override fun onMediaItemTransition(
                        mediaItem: MediaItem?,
                        reason: Int
                    ) {
                        _currentSong.value = mediaItem?.let { songFromMediaItem(it) }

                        // On auto-advance, move our index with the player and look ahead.
                        if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO) {
                            currentIndex += 1
                            prepareNext()
                        }
                    }

                    override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                        android.util.Log.e("PlayerViewModel", "Playback error: ${error.message}", error)
                        _status.value = "Playback error"
                    }
                })

                // Reconnecting to a live session: repopulate state from the controller.
                val existing = newController.currentMediaItem
                if (existing != null) {
                    _currentSong.value = songFromMediaItem(existing)
                    _isPlaying.value = newController.isPlaying
                }
            },
            MoreExecutors.directExecutor()
        )
    }

    // Rebuild a Song from a MediaItem (duration/streamUrl are not recoverable).
    private fun songFromMediaItem(item: MediaItem): Song {
        val meta = item.mediaMetadata
        return Song(
            id = item.mediaId,
            title = meta.title?.toString() ?: "",
            artist = meta.artist?.toString() ?: "",
            thumbnailId = item.mediaId,
            duration = 0L,
            streamUrl = null
        )
    }

    // Play a single song as a one-item queue.
    fun play(song: Song) {
        play(listOf(song), 0)
    }

    // Set the queue and start playing from startIndex.
    fun play(songs: List<Song>, startIndex: Int) {
        if (songs.isEmpty()) return

        queue = songs
        currentIndex = startIndex

        val song = songs[startIndex]

        if (_currentSong.value?.id == song.id && controller?.isPlaying == false) {
            controller?.play()
            return
        }

        resolveAndPlay(song)
    }

    // Resolve a song's stream URL (cache hit -> fileUrl, 404 -> NewPipe); null on failure.
    private suspend fun resolveUrl(song: Song): String? {
        return try {
            val response = RetrofitClient.api.getStream(
                youtubeId = song.id,
                title = song.title,
                artist = song.artist
            )

            if (response.isSuccessful) {
                response.body()?.streamUrl
            } else if (response.code() == 404) {
                YoutubeExtractor.getAudioUrl(song.id)
            } else {
                null
            }
        } catch (e: Exception) {
            android.util.Log.e("PlayerViewModel", "Resolve error for ${song.id}", e)
            null
        }
    }

    // Resolve one song, play it now, then look ahead to the next.
    private fun resolveAndPlay(song: Song) {
        _currentSong.value = song
        _status.value = "Loading..."

        viewModelScope.launch {
            val url = resolveUrl(song)
            if (url != null) {
                playUrl(url, song)
                _status.value = ""
                prepareNext()
            } else {
                _status.value = "Could not play song"
            }
        }
    }

    // Start playback of a resolved URL as the sole current item.
    private fun playUrl(url: String, song: Song) {
        val mediaItem = buildMediaItem(url, song)
        controller?.setMediaItem(mediaItem)
        controller?.prepare()
        controller?.play()
    }

    // Build a MediaItem (uri + mediaId + metadata) from a resolved URL and song.
    private fun buildMediaItem(url: String, song: Song): MediaItem {
        val metadata = MediaMetadata.Builder()
            .setTitle(song.title)
            .setArtist(song.artist)
            .setArtworkUri(Uri.parse("https://i.ytimg.com/vi/${song.id}/hqdefault.jpg"))
            .build()

        return MediaItem.Builder()
            .setUri(url)
            .setMediaId(song.id)
            .setMediaMetadata(metadata)
            .build()
    }

    // Resolve the next queued song and append it so auto-advance is seamless.
    private fun prepareNext() {
        val nextIndex = currentIndex + 1
        if (nextIndex >= queue.size) return

        val nextSong = queue[nextIndex]

        viewModelScope.launch {
            val url = resolveUrl(nextSong)
            if (url != null) {
                val item = buildMediaItem(url, nextSong)
                controller?.addMediaItem(item)
            }
        }
    }

    // Toggle between play and pause.
    fun togglePlayPause() {
        if (controller?.isPlaying == true) {
            controller?.pause()
        } else {
            controller?.play()
        }
    }

    // Release the controller when the ViewModel is destroyed.
    override fun onCleared() {
        super.onCleared()
        MediaController.releaseFuture(controllerFuture)
        controller = null
    }
}