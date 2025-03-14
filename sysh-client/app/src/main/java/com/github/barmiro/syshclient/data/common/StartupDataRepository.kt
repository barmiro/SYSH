package com.github.barmiro.syshclient.data.common

import com.github.barmiro.syshclient.data.common.authentication.JwtInterceptor
import com.github.barmiro.syshclient.data.common.preferences.UserPreferencesRepository
import com.github.barmiro.syshclient.util.Resource
import com.github.barmiro.syshclient.util.Resource.Error
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import java.io.IOException
import java.net.ConnectException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StartupDataRepository @Inject constructor(
    private val userPrefRepo: UserPreferencesRepository
) {

    val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS))
        .addInterceptor(JwtInterceptor(userPrefRepo))
        .build()


    val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.0.147:5754/")
        .client(client)
        .build()

    val startupApi = retrofit.create(StartupDataApi::class.java)


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
            } catch (e: IOException) {
                e.printStackTrace()
                emit(Resource.Error("Encountered IOException: " + e.message))
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error("Encountered HttpException: " + e.code()))
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
            } catch (e: IOException) {
                e.printStackTrace()
                emit(Resource.Error("Encountered IOException: " + e.message))
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error("Encountered HttpException: " + e.code()))
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