package com.github.barmiro.syshclient.data.common.authentication

import com.github.barmiro.syshclient.util.Resource
import com.github.barmiro.syshclient.util.Resource.Error
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
import java.net.ConnectException
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

            try {
                val authHeader = Credentials.basic(username, password)
                val response = authApi.getToken(authHeader)
                if (response.isSuccessful) {
                    response.body()?.takeIf {
                        it.token.isNotEmpty()
                    } ?.let {
                        emit(
                            Resource.Success(
                                data = it.token
                            )
                        )
                    } ?: emit(
                        Error(
                            message = "Server error, please contact your system administrator",
                            code = 500
                        )
                    )
                } else if (response.code() == 401) {
                    emit(Error(message = "Incorrect username or password"))
                } else {
                    emit(Error(message = response.message(),
                        code = response.code()))
                }
            } catch (e: IOException) {
                e.printStackTrace()
                emit(Error("Couldn't connect to server: " + e.message, code = 600))
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Error("Encountered HttpException: " + e.code(), code = e.code()))
            } catch (e: ConnectException) {
                e.printStackTrace()
                emit(Error("ConnectException:\n" + e.message, code = 601))
            } catch (e: Exception) {
                e.printStackTrace()
                emit(Error("Error:\n" + e.message, code = 666))
            }

            emit(Resource.Loading(false))
        }
    }

    fun register(username: String, password: String): Flow<Resource<String>> {
        return flow {
            emit(Resource.Loading(true))

            try {
                val response = authApi.register(
                    CreateUserDTO(username, password))

                if (response.isSuccessful) {
                    response.body()?.takeIf {
                        it.username == username
                    } ?.let {
                        emit(
                            Resource.Success(
                                data = it.username
                            )
                        )
                    } ?: emit(
                        Error(
                            message = "User registration failed"
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
            } catch (e: IOException) {
                e.printStackTrace()
                emit(Error("Encountered IOException: " + e.message))
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Error("Encountered HttpException: " + e.code()))
            } catch (e: ConnectException) {
                e.printStackTrace()
                emit(Error("ConnectException:\n" + e.message, code = 600))
            } catch (e: Exception) {
                e.printStackTrace()
                emit(Error("Exception:\n" + e.message))
            }
            emit(Resource.Loading(false))
        }
    }

}