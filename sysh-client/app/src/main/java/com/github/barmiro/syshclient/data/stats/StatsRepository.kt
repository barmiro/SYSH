package com.github.barmiro.syshclient.data.stats

import com.github.barmiro.syshclient.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.io.IOException
import java.net.ConnectException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StatsRepository @Inject constructor() {

    val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.0.147:5754/stats/")
        .addConverterFactory(
            Json.asConverterFactory(
                "application/json; charset=UTF8".toMediaType()))
        .build()

    val statsApi = retrofit.create(StatsApi::class.java)

    fun getStats(): Flow<Resource<StatsDTO>> {
        return flow {
            emit(Resource.Loading(true))
            try {
                emit(
                    Resource.Success(
                        data = statsApi
                            .fetchStatsAll()
                            .body()
                    )
                )


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
        }
    }



//TODO: move to a common repo
    fun getOldestStreamDate(): Flow<Resource<LocalDate>> {
        return flow {
            emit(Resource.Loading(true))
            val oldestStreamDate = try{
                statsApi.fetchStartupData()
                    .body()
            } catch (e: IOException) {
                e.printStackTrace()
                emit(Resource.Error("Encountered IOException: " + e.message))
                ""
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error("Encountered HttpException: " + e.code()))
                ""
            }
            val isFetchSuccessful = !oldestStreamDate.isNullOrEmpty()
            if (isFetchSuccessful) {
                val dateFromTimestamp = oldestStreamDate!!.substringBefore('T')
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                emit(
                    Resource.Success(
                    data = LocalDate.parse(dateFromTimestamp, formatter)
                ))
            } else {
                emit(Resource.Error("Received list is empty"))
            }
            emit(Resource.Loading(false))
        }
    }


}

