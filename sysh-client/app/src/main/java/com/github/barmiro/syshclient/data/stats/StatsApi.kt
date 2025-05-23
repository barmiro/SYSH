package com.github.barmiro.syshclient.data.stats

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.time.LocalDateTime

interface StatsApi {

    @GET("all")
    suspend fun fetchStatsAll(): Response<StatsDTO>

    @GET("range")
    suspend fun fetchStatsRange(
        @Query("start") start: LocalDateTime? = null,
        @Query("end") end: LocalDateTime? = null
    ): Response<StatsDTO>

    @GET("series")
    suspend fun fetchStatsSeries(
        @Query("start") start: LocalDateTime? = null,
        @Query("end") end: LocalDateTime? = null,
        @Query("step") step: String? = null
    ): Response<List<StatsSeriesChunkDTO>>

    @GET("hourly")
    suspend fun fetchHourlyStats(
        @Query("start") start: LocalDateTime? = null,
        @Query("end") end: LocalDateTime? = null
    ): Response<List<HourlyStatsDTO>>

    @GET("home")
    suspend fun fetchHomeStats(
    ): Response<HomeStatsDTO>

    @GET("year/{year}")
    suspend fun fetchStatsYear(
        @Path("year") year: Int
    ): Response<StatsDTO>

    @GET("startup")
    suspend fun fetchStartupData(): Response<FirstStreamDateDTO>

}