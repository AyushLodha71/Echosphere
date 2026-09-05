package com.example.echosphere.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "songs")
data class Song(
    @PrimaryKey val id: String,
    val title: String,
    val artist: String,
    val thumbnailId: String?,
    val duration: Long,
    val streamUrl: String?
)