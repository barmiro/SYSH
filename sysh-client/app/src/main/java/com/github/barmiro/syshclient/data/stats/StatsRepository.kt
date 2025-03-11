package com.github.barmiro.syshclient.data.stats

import com.github.barmiro.syshclient.data.common.authentication.JwtInterceptor
import com.github.barmiro.syshclient.data.common.preferences.UserPreferencesRepository
import com.github.barmiro.syshclient.util.Resource
import com.github.barmiro.syshclient.util.Resource.Error
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StatsRepository @Inject constructor(
    private val userPrefRepo: UserPreferencesRepository
) {

    val client = OkHttpClient.Builder()
        .addInterceptor(JwtInterceptor(userPrefRepo))
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.0.147:5754/stats/")
        .client(client)
        .addConverterFactory(
            Json.asConverterFactory(
                "application/json; charset=UTF8".toMediaType()))
        .build()

    val statsApi = retrofit.create(StatsApi::class.java)

    fun getStats(
        start: String?,
        end: String?
    ): Flow<Resource<StatsDTO>> {
        return flow {
            emit(Resource.Loading(true))

            val response = if (start.isNullOrEmpty() || end.isNullOrEmpty()) {
                    statsApi.fetchStatsAll()
            } else {
                statsApi.fetchStatsRange(start, end)
            }
            if (response.isSuccessful) {
                emit(
                    Resource.Success(
                        data = response.body()
                    )
                )
            } else {
                emit(
                    Resource.Error(response.message())
                )
            }
            emit(Resource.Loading(false))

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
                emit(Error("Encountered IOException: " + e.message))
                ""
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Error("Encountered HttpException: " + e.code()))
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
                emit(Error("Received list is empty"))
            }
            emit(Resource.Loading(false))
        }
    }


}


//                var endOrLocalDateTimeNow = end
//                if (end.isNullOrEmpty()) {
//                    val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
//                    endOrLocalDateTimeNow = formatter.format(LocalDateTime.now())
//                }