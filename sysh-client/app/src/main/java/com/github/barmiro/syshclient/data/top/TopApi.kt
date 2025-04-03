package com.github.barmiro.syshclient.data.top

import com.github.barmiro.syshclient.data.top.dto.AlbumDTO
import com.github.barmiro.syshclient.data.top.dto.ArtistDTO
import com.github.barmiro.syshclient.data.top.dto.TrackDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.time.LocalDateTime

interface TopApi {

    @GET("tracks{optionalRangePath}")
    suspend fun fetchTopTracks(
        @Path("optionalRangePath", encoded = true) rangePath: String? = "",
        @Query("start") start: LocalDateTime? = LocalDateTime.of(2000, 1, 1, 0, 0),
        @Query("end") end: LocalDateTime? = LocalDateTime.of(2038, 1, 1, 0, 0),
        @Query("sort") sort: String? = null,
        @Query("offset") offset: Int,
        @Query("size") size: Int
    ): Response<List<TrackDTO>>

    @GET("albums{optionalRangePath}")
    suspend fun fetchTopAlbums(
        @Path("optionalRangePath", encoded = true) rangePath: String? = "",
        @Query("start") start: LocalDateTime? = LocalDateTime.of(2000, 1, 1, 0, 0),
        @Query("end") end: LocalDateTime? = LocalDateTime.of(2038, 1, 1, 0, 0),
        @Query("sort") sort: String? = null,
        @Query("offset") offset: Int,
        @Query("size") size: Int
    ): Response<List<AlbumDTO>>

    @GET("artists")
    suspend fun fetchTopArtists(
        @Query("start") start: LocalDateTime? = null,
        @Query("end") end: LocalDateTime? = null,
        @Query("sort") sort: String? = null,
        @Query("offset") offset: Int,
        @Query("size") size: Int
    ): Response<List<ArtistDTO>>
}