package com.github.barmiro.syshclient.data.common.startup

import com.github.barmiro.syshclient.data.common.ServerErrorInterceptor
import com.github.barmiro.syshclient.data.common.ServerUrlInterceptor
import com.github.barmiro.syshclient.data.common.authentication.JwtInterceptor
import com.github.barmiro.syshclient.data.common.handleNetworkException
import com.github.barmiro.syshclient.data.common.preferences.ServerInfo
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
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StartupDataRepository @Inject constructor(
    private val userPrefRepo: UserPreferencesRepository
) {
    val client = OkHttpClient.Builder()
        .addInterceptor(ServerUrlInterceptor(userPrefRepo))
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS))
        .addInterceptor(JwtInterceptor(userPrefRepo))
        .addInterceptor(ServerErrorInterceptor(userPrefRepo))
        .build()


    val retrofit = Retrofit.Builder()
        .baseUrl("http://localhost/")
        .client(client)
        .addConverterFactory(
            Json.asConverterFactory(
                "application/json; charset=UTF8".toMediaType()))
        .build()

    val startupApi = retrofit.create(StartupDataApi::class.java)

    fun getServerInfo(): Flow<Resource<ServerInfo>> {
        return flow {
            emit(Resource.Loading(true))

            try {
                val response = startupApi.getServerInfo()
                if (response.isSuccessful) {
                    response.body()?.let {
                        emit(
                            Resource.Success(
                                data = it
                            )
                        )
                    } ?: emit(
                        Error(
                            message = "Server didn't return any data",
                            code = response.code()
                        )
                    )
                } else {
                    emit(
                        Error(
                            message = response.message(),
                            code = response.code()
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

    fun getUserDisplayName(): Flow<Resource<String>> {
        return flow {
            emit(Resource.Loading(true))

            try {
                val response = startupApi.getUserData()
                if (response.isSuccessful) {
                    response.body()?.let {
                        emit(
                            Resource.Success(
                                data = it.string()
                            )
                        )
                    } ?: emit(
                        Error(
                            message = "No username found",
                            code = response.code()
                        )
                    )
                } else {
                    emit(Error(
                        message = response.message(),
                        code = response.code()
                    ))
                }
            } catch (e: Exception) {
                val errorValues = handleNetworkException(e)
                emit(Error(errorValues.message, errorValues.code))
            }
            emit(Resource.Loading(false))
        }
    }

    fun getSpotifyAuthUrl(): Flow<Resource<String>> {
        return flow{
            emit(Resource.Loading(true))
            try {
                val response = startupApi.spotifyAuthorize()
                if (response.isSuccessful) {
                    response.body()?.let {
                        emit(
                            Resource.Success(
                                data = it.string()
                            )
                        )
                    } ?: emit(
                        Error(
                            message = "Couldn't generate Spotify authorization URL",
                            code = response.code()
                        )
                    )
                } else {
                    emit(Error(
                        message = response.message(),
                        code = response.code()
                    ))
                }
            } catch (e: Exception) {
                val errorValues = handleNetworkException(e)
                emit(Error(errorValues.message, errorValues.code))
            }

            emit(Resource.Loading(false))
        }
    }
}