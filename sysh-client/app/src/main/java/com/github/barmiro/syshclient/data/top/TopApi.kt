package com.github.barmiro.syshclient.data.top

import com.github.barmiro.syshclient.data.top.dto.AlbumDTO
import com.github.barmiro.syshclient.data.top.dto.ArtistDTO
import com.github.barmiro.syshclient.data.top.dto.TrackDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TopApi {

    @GET("tracks{optionalRangePath}")
    suspend fun fetchTopTracks(
        @Path("optionalRangePath", encoded = true) rangePath: String? = "",
        @Query("start") start: String? = "2000-01-01T00:00:00",
        @Query("end") end: String? = "2038-01-1T00:00:00",
        @Query("sort") sort: String? = null
    ): Response<List<TrackDTO>>

    @GET("albums{optionalRangePath}")
    suspend fun fetchTopAlbums(
        @Path("optionalRangePath", encoded = true) rangePath: String? = "",
        @Query("start") start: String? = "2000-01-01T00:00:00",
        @Query("end") end: String? = "2038-01-01T00:00:00",
        @Query("sort") sort: String? = null
    ): Response<List<AlbumDTO>>

    @GET("artists")
    suspend fun fetchTopArtists(
        @Query("start") start: String? = null,
        @Query("end") end: String? = null,
        @Query("sort") sort: String? = null
    ): Response<List<ArtistDTO>>

    @GET("{entity}")
    suspend fun<T> fetchTopItems(
        @Path("entity") entity: String,
        @Query("start") start: String? = null,
        @Query("end") end: String? = null,
        @Query("sort") sort: String? = null
    ): Response<List<T>>
}