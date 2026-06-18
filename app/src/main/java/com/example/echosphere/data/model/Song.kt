package com.example.echosphere.data.model

data class Song(
    val id: String,
    val title: String,
    val artist: String,
    val thumbnailUrl: String?,
    val duration: Long,
    val streamUrl: String?
)