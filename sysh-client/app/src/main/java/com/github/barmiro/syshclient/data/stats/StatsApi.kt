package com.github.barmiro.syshclient.data.stats

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

    @GET("series")
    suspend fun fetchStatsSeries(
        @Query("start") start: String? = null,
        @Query("end") end: String? = null,
        @Query("step") step: String? = null
    ): Response<List<StatsSeriesChunkDTO>>

    @GET("year/{year}")
    suspend fun fetchStatsYear(
        @Path("year") year: Int
    ): Response<StatsDTO>

    @GET("startup")
    suspend fun fetchStartupData(): Response<String>

}