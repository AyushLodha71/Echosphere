package com.ayushlodha.echosphere.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.ayushlodha.echosphere.data.model.Song
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    // --- Playlists ---

    @Insert
    suspend fun createPlaylist(playlist: Playlist): Long

    @Query("SELECT * FROM playlists WHERE id != :excludeId ORDER BY createdAt DESC")
    fun getAllPlaylists(excludeId: Long): Flow<List<Playlist>>

    @Query("DELETE FROM playlists WHERE id = :playlistId")
    suspend fun deletePlaylist(playlistId: Long)

    // --- Songs ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSong(song: Song)

    // --- Cross-ref (add song to playlist) ---

    @Query("SELECT COALESCE(MAX(position), -1) + 1 FROM playlist_song_cross_ref WHERE playlistId = :playlistId")
    suspend fun getNextPosition(playlistId: Long): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCrossRef(crossRef: PlaylistSongCrossRef)

    @Transaction
    suspend fun addSongToPlaylist(playlistId: Long, song: Song) {
        upsertSong(song)
        val position = getNextPosition(playlistId)
        insertCrossRef(PlaylistSongCrossRef(playlistId, song.id, position))
    }

    // --- Read a playlist's songs, in order ---

    @Query(
        """
        SELECT songs.* FROM songs
        INNER JOIN playlist_song_cross_ref
            ON songs.id = playlist_song_cross_ref.songId
        WHERE playlist_song_cross_ref.playlistId = :playlistId
        ORDER BY playlist_song_cross_ref.position ASC
        """
    )
    fun getSongsInPlaylist(playlistId: Long): Flow<List<Song>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlaylistWithId(playlist: Playlist)

    @Query("SELECT * FROM playlists WHERE id = :playlistId LIMIT 1")
    fun getPlaylist(playlistId: Long): Flow<Playlist?>

    @Query("SELECT EXISTS(SELECT 1 FROM playlist_song_cross_ref WHERE playlistId = :playlistId AND songId = :songId)")
    fun isSongInPlaylist(playlistId: Long, songId: String): Flow<Boolean>

    // --- Remove a song from a playlist ---

    @Query("DELETE FROM playlist_song_cross_ref WHERE playlistId = :playlistId AND songId = :songId")
    suspend fun removeSongFromPlaylist(playlistId: Long, songId: String)
}