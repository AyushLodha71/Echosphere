package com.example.echosphere.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface EchosphereApi {

    @GET("stream/{youtubeId}")
    suspend fun getStream(
        @Path("youtubeId") youtubeId: String,
        @Query("title") title: String? = null,
        @Query("artist") artist: String? = null
    ): Response<StreamResponse>
}