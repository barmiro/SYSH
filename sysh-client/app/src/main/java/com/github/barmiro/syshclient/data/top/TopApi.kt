package com.github.barmiro.syshclient.data.top

import com.github.barmiro.syshclient.data.top.dto.TrackDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface TopApi {

    @GET("/top/tracks")
    suspend fun getTopTracks(
        @Query("start") start: String,
        @Query("end") end: String
    ): Response<List<TrackDTO>>
}