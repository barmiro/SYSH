package com.github.barmiro.syshclient.data.top

import com.github.barmiro.syshclient.data.top.dto.AlbumDTO
import com.github.barmiro.syshclient.data.top.dto.ArtistDTO
import com.github.barmiro.syshclient.data.top.dto.TrackDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface TopApi {

    @GET("tracks")
    suspend fun getTopTracks(
        @Query("start") start: String? = null,
        @Query("end") end: String? = null,
        @Query("sort") sort: String? = null
    ): Response<List<TrackDTO>>

    @GET("albums")
    suspend fun getTopAlbums(
        @Query("start") start: String? = null,
        @Query("end") end: String? = null,
        @Query("sort") sort: String? = null
    ): Response<List<AlbumDTO>>

    @GET("artists")
    suspend fun getTopArtists(
        @Query("start") start: String? = null,
        @Query("end") end: String? = null,
        @Query("sort") sort: String? = null
    ): Response<List<ArtistDTO>>
}