package com.github.barmiro.syshclient.data.top

import com.github.barmiro.syshclient.domain.top.TopAlbum
import com.github.barmiro.syshclient.domain.top.TopArtist
import com.github.barmiro.syshclient.domain.top.TopTrack
import com.github.barmiro.syshclient.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okio.IOException
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.net.ConnectException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TopRepository @Inject constructor() {

    val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.0.100:5754/top/")
        .addConverterFactory(
            Json.asConverterFactory(
                "application/json; charset=UTF8".toMediaType()))
        .build()

    val topApi = retrofit.create(TopApi::class.java)


//    I know this is bad, but refactoring will be made later on,
//    when I know exactly which DTO contains what fields
    fun getTopTracks(
        start: String? = null,
        end: String? = null,
        sort: String? = null
    ): Flow<Resource<List<TopTrack>>> {
        return flow {
            emit(Resource.Loading(true))
            try {
                val topTracks = topApi.fetchTopTracks(start, end, sort)
                    .body()
                    .orEmpty()
                val isFetchSuccessful = topTracks.isNotEmpty()
                if (isFetchSuccessful) {
                    emit(Resource.Success(
                        data = topTracks.map { it.toTopTrack() }
                    ))
                } else {
                    emit(Resource.Error("No results found"))
                }
            } catch (e:IOException) {
                e.printStackTrace()
                emit(Resource.Error("IOException:\n" + e.message))
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error("HttpException:\n" + e.code()))
            } catch (e: ConnectException) {
                e.printStackTrace()
                emit(Resource.Error("ConnectException:\n" + e.message))
            } catch (e: Exception) {
                e.printStackTrace()
                emit(Resource.Error("Exception:\n" + e.message))
            }

            emit(Resource.Loading(false))
        }
    }
    fun getTopAlbums(
        start: String? = null,
        end: String? = null,
        sort: String? = null
    ): Flow<Resource<List<TopAlbum>>> {
        return flow {
            emit(Resource.Loading(true))
            try {
                val topAlbums = topApi.fetchTopAlbums(start, end, sort)
                    .body()
                    .orEmpty()
                val isFetchSuccessful = topAlbums.isNotEmpty()
                if (isFetchSuccessful) {
                    emit(Resource.Success(
                        data = topAlbums.map { it.toTopAlbum() }
                    ))
                } else {
                    emit(Resource.Error("No results found"))
                }
            } catch (e:IOException) {
                e.printStackTrace()
                emit(Resource.Error("IOException:\n" + e.message))
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error("HttpException:\n" + e.code()))
            } catch (e: ConnectException) {
                e.printStackTrace()
                emit(Resource.Error("ConnectException:\n" + e.message))
            } catch (e: Exception) {
                e.printStackTrace()
                emit(Resource.Error("Exception:\n" + e.message))
            }

            emit(Resource.Loading(false))
        }
    }

    fun getTopArtists(
        start: String? = null,
        end: String? = null,
        sort: String? = null
    ): Flow<Resource<List<TopArtist>>> {
        return flow {
            emit(Resource.Loading(true))
            try {
                val topArtists = topApi.fetchTopArtists(start, end, sort)
                    .body()
                    .orEmpty()
                val isFetchSuccessful = topArtists.isNotEmpty()
                if (isFetchSuccessful) {
                    emit(Resource.Success(
                        data = topArtists.map { it.toTopArtist() }
                    ))
                } else {
                    emit(Resource.Error("No results found"))
                }
            } catch (e:IOException) {
                e.printStackTrace()
                emit(Resource.Error("IOException:\n" + e.message))
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error("HttpException:\n" + e.code()))
            } catch (e: ConnectException) {
                e.printStackTrace()
                emit(Resource.Error("ConnectException:\n" + e.message))
            } catch (e: Exception) {
                e.printStackTrace()
                emit(Resource.Error("Exception:\n" + e.message))
            }

            emit(Resource.Loading(false))
        }
    }



}

