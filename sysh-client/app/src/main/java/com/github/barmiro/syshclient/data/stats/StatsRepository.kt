package com.github.barmiro.syshclient.data.stats

import com.github.barmiro.syshclient.data.common.ServerErrorInterceptor
import com.github.barmiro.syshclient.data.common.ServerUrlInterceptor
import com.github.barmiro.syshclient.data.common.authentication.JwtInterceptor
import com.github.barmiro.syshclient.data.common.handleNetworkException
import com.github.barmiro.syshclient.data.common.preferences.UserPreferencesRepository
import com.github.barmiro.syshclient.util.Resource
import com.github.barmiro.syshclient.util.Resource.Error
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StatsRepository @Inject constructor(
    private val userPrefRepo: UserPreferencesRepository
) {
    val client = OkHttpClient.Builder()
        .addInterceptor(ServerUrlInterceptor(userPrefRepo))
        .addInterceptor(JwtInterceptor(userPrefRepo))
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS))
        .addInterceptor(ServerErrorInterceptor(userPrefRepo))
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl("http://localhost/stats/")
        .client(client)
        .addConverterFactory(
            Json.asConverterFactory(
                "application/json; charset=UTF8".toMediaType()))
        .build()

    val statsApi = retrofit.create(StatsApi::class.java)

    fun getStats(
        start: LocalDateTime? = null,
        end: LocalDateTime? = null,
        mode: String?,
        year: Int?
    ): Flow<Resource<StatsDTO>> {
        return flow {
            emit(Resource.Loading(true))

            try {
                val response = if (mode == "yearly") {
                    statsApi.fetchStatsYear(year ?: LocalDate.now().year)
                } else if (start == null || end == null) {
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
                        Error(response.message())
                    )
                }
            } catch (e: Exception) {
                val errorValues = handleNetworkException(e)
                emit(Error(errorValues.message, errorValues.code))
            }
            emit(Resource.Loading(false))

        }
    }

    fun getStatsSeries(
        start: LocalDateTime? = null,
        end: LocalDateTime? = null,
        step: String?
    ): Flow<Resource<List<StatsSeriesChunkDTO>>> {
        return flow {
            emit(Resource.Loading(true))

            try {
                val response = statsApi.fetchStatsSeries(
                    start = start,
                    end = end,
                    step = step
                )
                if (response.isSuccessful) {
                    emit(
                        Resource.Success(
                            data = response.body()
                        )
                    )
                } else {
                    emit(
                        Error(response.message())
                    )
                }
            } catch (e: Exception) {
                val errorValues = handleNetworkException(e)
                emit(Error(errorValues.message, errorValues.code))
            }
            emit(Resource.Loading(false))

        }
    }


    fun getHourlyStats(
        start: LocalDateTime? = null,
        end: LocalDateTime? = null
    ): Flow<Resource<List<HourlyStatsDTO>>> {
        return flow {
            emit(Resource.Loading(true))

            try {
                val response = statsApi.fetchHourlyStats(
                    start = start,
                    end = end
                )
                if (response.isSuccessful) {
                    emit(
                        Resource.Success(
                            data = response.body()
                        )
                    )
                } else {
                    emit(
                        Error(response.message())
                    )
                }
            } catch (e: Exception) {
                val errorValues = handleNetworkException(e)
                emit(Error(errorValues.message, errorValues.code))
            }
            emit(Resource.Loading(false))

        }
    }


//TODO: move to a common repo
    fun getOldestStreamDate(): Flow<Resource<LocalDate>> {
        return flow {
            emit(Resource.Loading(true))
            try {
                statsApi.fetchStartupData().body()?.let {
                    val oldestStreamDate: LocalDate = it.firstStreamDate.toLocalDate()
                    emit(Resource.Success(
                        data = oldestStreamDate
                        )
                    )
                }
            } catch (e: Exception) {
                val errorValues = handleNetworkException(e)
                emit(Error(errorValues.message, errorValues.code))
            }
            emit(Resource.Loading(false))
        }
    }
}
