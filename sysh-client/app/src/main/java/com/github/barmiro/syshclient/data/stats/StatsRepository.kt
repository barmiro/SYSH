package com.github.barmiro.syshclient.data.stats

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StatsRepository @Inject constructor() {

    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080/stats/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val statsApi = retrofit.create(StatsApi::class.java)

    suspend fun getStats() {
        println("Stats all: " + statsApi.fetchStatsAll().body())
        println("Stats range: " + statsApi.fetchStatsRange("2024-01-01T00:00:00", "2024-12-31T23:59:59").body())
        println("Stats year: " + statsApi.fetchStatsYear(2024).body())
    }


}

