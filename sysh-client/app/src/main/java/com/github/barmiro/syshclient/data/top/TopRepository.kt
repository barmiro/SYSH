package com.github.barmiro.syshclient.data.top

import com.github.barmiro.syshclient.data.top.dto.AlbumDTO
import com.github.barmiro.syshclient.data.top.dto.ArtistDTO
import com.github.barmiro.syshclient.data.top.dto.TrackDTO
import com.github.barmiro.syshclient.util.Resource
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TopRepository @Inject constructor() {

    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080/top/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val topApi = retrofit.create(TopApi::class.java)

    suspend fun test() {
        println("Top track: " + topApi.fetchTopTracks("2024-01-01T00:00:00", "2024-12-31T23:59:59", "time").body().orEmpty()[0])
        println("Top album: " + topApi.fetchTopAlbums("2024-01-01T00:00:00", "2024-12-31T23:59:59", "time").body().orEmpty()[0])
        println("Top artist: " + topApi.fetchTopArtists("2024-01-01T00:00:00", "2024-12-31T23:59:59", "time").body().orEmpty()[0])
    }

//    I know this is bad, but refactoring will be made later on,
//    when I know exactly which DTO contains what fields
    fun getTopTracks(
        start: String? = null,
        end: String? = null,
        sort: String? = null
    ): Flow<Resource<List<TrackDTO>>> {
        return flow {
            emit(Resource.Loading(true))
            val topTracks = try{
                topApi.fetchTopTracks(start, end, sort)
                    .body()
                    .orEmpty()
            } catch (e:IOException) {
                e.printStackTrace()
                emit(Resource.Error("Encountered IOException: " + e.message))
                emptyList()
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error("Encountered HttpException: " + e.code()))
                emptyList()
            }
            val isFetchSuccessful = topTracks.isNotEmpty()
            if (isFetchSuccessful) {
                emit(Resource.Success(topTracks))
            } else {
                emit(Resource.Error("Received list is empty"))
            }
            emit(Resource.Loading(false))
        }
    }
    fun getTopAlbums(
        start: String? = null,
        end: String? = null,
        sort: String? = null
    ): Flow<Resource<List<AlbumDTO>>> {
        return flow {
            emit(Resource.Loading(true))
            val topAlbums = try{
                topApi.fetchTopAlbums(start, end, sort)
                    .body()
                    .orEmpty()
            } catch (e:IOException) {
                e.printStackTrace()
                emit(Resource.Error("Encountered IOException: " + e.message))
                emptyList()
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error("Encountered HttpException: " + e.code()))
                emptyList()
            }
            val isFetchSuccessful = topAlbums.isNotEmpty()
            if (isFetchSuccessful) {
                emit(Resource.Success(topAlbums))
            } else {
                emit(Resource.Error("Received list is empty"))
            }
            emit(Resource.Loading(false))
        }
    }

    fun getTopArtists(
        start: String? = null,
        end: String? = null,
        sort: String? = null
    ): Flow<Resource<List<ArtistDTO>>> {
        return flow {
            emit(Resource.Loading(true))
            val topArtists = try{
                topApi.fetchTopArtists(start, end, sort)
                    .body()
                    .orEmpty()
            } catch (e:IOException) {
                e.printStackTrace()
                emit(Resource.Error("Encountered IOException: " + e.message))
                emptyList()
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error("Encountered HttpException: " + e.code()))
                emptyList()
            }
            val isFetchSuccessful = topArtists.isNotEmpty()
            if (isFetchSuccessful) {
                emit(Resource.Success(topArtists))
            } else {
                emit(Resource.Error("Received list is empty"))
            }
            emit(Resource.Loading(false))
        }
    }


}

