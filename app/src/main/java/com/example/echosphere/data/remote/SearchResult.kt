package com.example.echosphere.data.remote

import com.example.echosphere.data.model.Song

data class SearchResult(
    val id: String,
    val title: String,
    val artist: String,
    val thumbnailId: String?
)

fun SearchResult.toSong(): Song {
    return Song(
        id = this.id,
        title = this.title,
        artist = this.artist,
        thumbnailId = this.thumbnailId,
        duration = 0L,
        streamUrl = null
    )
}