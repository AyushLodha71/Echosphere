package com.ayushlodha.echosphere.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.schabi.newpipe.extractor.NewPipe
import org.schabi.newpipe.extractor.ServiceList
import org.schabi.newpipe.extractor.stream.StreamInfo

object YoutubeExtractor {

    // Make sure NewPipe is initialized exactly once with our Downloader.
    private var initialized = false

    private fun ensureInit() {
        if (!initialized) {
            NewPipe.init(NewPipeDownloader())
            initialized = true
        }
    }

    // Given a YouTube video ID, extract the best audio stream URL.
    // Runs on the IO dispatcher because it's blocking network work.
    suspend fun getAudioUrl(youtubeId: String): String? = withContext(Dispatchers.IO) {
        ensureInit()

        val videoUrl = "https://www.youtube.com/watch?v=$youtubeId"

        // Fetch full stream info for the video
        val streamInfo = StreamInfo.getInfo(ServiceList.YouTube, videoUrl)

        // Pick the best audio-only stream (highest bitrate)
        val audioStreams = streamInfo.audioStreams
        if (audioStreams.isNullOrEmpty()) {
            return@withContext null
        }

        val best = audioStreams.maxByOrNull { it.averageBitrate }
        return@withContext best?.content   // the actual audio URL
    }
}