package com.github.barmiro.syshclient.data.top

import com.github.barmiro.syshclient.data.top.dto.TrackDTO
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
class TopRepository {

    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080/top/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val topApi = retrofit.create(TopApi::class.java)

    suspend fun getTracks() {
        println("Top track: " + topApi.getTopTracks("2024-01-01T00:00:00", "2024-12-31T23:59:59", "time").body().orEmpty()[0])
        println("Top album: " + topApi.getTopAlbums("2024-01-01T00:00:00", "2024-12-31T23:59:59", "time").body().orEmpty()[0])
        println("Top artist: " + topApi.getTopArtists("2024-01-01T00:00:00", "2024-12-31T23:59:59", "time").body().orEmpty()[0])
    }


}

