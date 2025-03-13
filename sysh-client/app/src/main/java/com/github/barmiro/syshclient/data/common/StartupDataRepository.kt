package com.github.barmiro.syshclient.data.common

import com.github.barmiro.syshclient.data.common.authentication.JwtInterceptor
import com.github.barmiro.syshclient.data.common.preferences.UserPreferencesRepository
import com.github.barmiro.syshclient.util.Resource
import com.github.barmiro.syshclient.util.Resource.Error
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
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

            emit(Resource.Loading(false))
        }
    }

    fun getSpotifyAuthUrl(): Flow<Resource<String>> {
        return flow{
            emit(Resource.Loading(true))

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

            emit(Resource.Loading(false))
        }
    }

}