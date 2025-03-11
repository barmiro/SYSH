package com.github.barmiro.syshclient.data.common.authentication

import com.github.barmiro.syshclient.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import okhttp3.Credentials
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthenticationRepository @Inject constructor() {

    val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.0.147:5754/")
//        TODO: remove
        .client(OkHttpClient.Builder().addInterceptor(
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS))
            .build()
        )
        .addConverterFactory(
            Json.asConverterFactory(
                "application/json; charset=UTF8".toMediaType()))
        .build()

    val authApi = retrofit.create(AuthenticationApi::class.java)


    fun getToken(username: String, password: String): Flow<Resource<String>> {
        return flow {
            emit(Resource.Loading(true))
            val token = try{
                val authHeader = Credentials.basic(username, password)
                authApi.getToken(authHeader).body()?.token
            } catch (e: IOException) {
                e.printStackTrace()
                emit(Resource.Error("Encountered IOException: " + e.message))
                ""
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error("Encountered HttpException: " + e.code()))
                ""
            }
            val isFetchSuccessful = !token.isNullOrEmpty()
            if (isFetchSuccessful) {
                emit(
                    Resource.Success(
                        data = token
                    ))
            } else {
                emit(Resource.Error("Couldn't fetch token"))
            }
            emit(Resource.Loading(false))
        }
    }

    fun register(username: String, password: String): Flow<Resource<String>> {
        return flow {
            emit(Resource.Loading(true))
            val response = authApi.register(
                CreateUserDTO(username, password))
            if (response.isSuccessful) {
                val responseUsername = response.body()?.username

                if (responseUsername == username) {
                    emit(
                        Resource.Success(
                            data = responseUsername
                        ))
                } else {
                    emit(Resource.Error("User registration failed"))
                }

            } else {
                if (response.code() == 409) {
                    emit(Resource.Error("Username taken"))
                } else {
                    emit(Resource.Error("Server error: " + response.code()))
                }
            }
            emit(Resource.Loading(false))
        }
    }
}