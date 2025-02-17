package com.github.barmiro.syshclient.data.stats

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StatsRepository @Inject constructor() {

    val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.0.147:8080/stats/")
        .addConverterFactory(
            Json.asConverterFactory(
                "application/json; charset=UTF8".toMediaType()))
        .build()

    val statsApi = retrofit.create(StatsApi::class.java)

    suspend fun getStats() {
        println("Stats all: " + statsApi.fetchStatsAll().body())
        println("Stats range: " + statsApi.fetchStatsRange("2024-01-01T00:00:00", "2024-12-31T23:59:59").body())
        println("Stats year: " + statsApi.fetchStatsYear(2024).body())
    }


}

