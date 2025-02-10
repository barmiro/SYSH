package com.github.barmiro.syshclient.data.stats

import com.github.barmiro.syshclient.data.top.dto.AlbumDTO
import com.github.barmiro.syshclient.data.top.dto.ArtistDTO
import com.github.barmiro.syshclient.data.top.dto.TrackDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface StatsApi {

    @GET("all")
    suspend fun fetchStatsAll(): Response<StatsDTO>

    @GET("range")
    suspend fun fetchStatsRange(
        @Query("start") start: String? = null,
        @Query("end") end: String? = null
    ): Response<StatsDTO>

    @GET("year/{year}")
    suspend fun fetchStatsYear(
        @Path("year") year: Int
    ): Response<StatsDTO>
}