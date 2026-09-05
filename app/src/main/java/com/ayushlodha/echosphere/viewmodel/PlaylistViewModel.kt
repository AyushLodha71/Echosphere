package com.ayushlodha.echosphere.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ayushlodha.echosphere.data.local.AppDatabase
import com.ayushlodha.echosphere.data.local.Playlist
import com.ayushlodha.echosphere.data.model.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class PlaylistViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        const val LIKED_PLAYLIST_ID = 1L
    }

    private val dao = AppDatabase.getInstance(application).playlistDao()

    val playlists: Flow<List<Playlist>> = dao.getAllPlaylists(LIKED_PLAYLIST_ID)

    init {
        viewModelScope.launch {
            dao.insertPlaylistWithId(
                Playlist(id = LIKED_PLAYLIST_ID, name = "Liked Songs")
            )
        }
    }

    fun createPlaylist(name: String) {
        viewModelScope.launch {
            dao.createPlaylist(Playlist(name = name))
        }
    }

    fun addSongToPlaylist(playlistId: Long, song: Song) {
        viewModelScope.launch {
            dao.addSongToPlaylist(playlistId, song)
        }
    }

    fun getPlaylist(playlistId: Long): Flow<Playlist?> = dao.getPlaylist(playlistId)

    fun getSongsInPlaylist(playlistId: Long): Flow<List<Song>> =
        dao.getSongsInPlaylist(playlistId)

    fun isSongLiked(songId: String): Flow<Boolean> =
        dao.isSongInPlaylist(LIKED_PLAYLIST_ID, songId)

    fun toggleLike(song: Song, currentlyLiked: Boolean) {
        viewModelScope.launch {
            if (currentlyLiked) {
                dao.removeSongFromPlaylist(LIKED_PLAYLIST_ID, song.id)
            } else {
                dao.addSongToPlaylist(LIKED_PLAYLIST_ID, song)
            }
        }
    }

    fun deletePlaylist(playlistId: Long) {
        viewModelScope.launch {
            dao.deletePlaylist(playlistId)
        }
    }
}