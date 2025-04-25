package com.github.barmiro.syshclient.data.common.authentication

import com.github.barmiro.syshclient.data.common.ServerErrorInterceptor
import com.github.barmiro.syshclient.data.common.ServerUrlInterceptor
import com.github.barmiro.syshclient.data.common.handleNetworkException
import com.github.barmiro.syshclient.data.common.preferences.UserPreferencesRepository
import com.github.barmiro.syshclient.util.Resource
import com.github.barmiro.syshclient.util.Resource.Error
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import okhttp3.Credentials
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthenticationRepository @Inject constructor(
    private val userPrefRepo: UserPreferencesRepository
) {

    val client = OkHttpClient.Builder()
        .addInterceptor(ServerUrlInterceptor(userPrefRepo))
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS))
        .addInterceptor(ServerErrorInterceptor(userPrefRepo))
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl("http://localhost/")
//        TODO: remove
        .client(client)
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
            } catch (e: Exception) {
                val errorValues = handleNetworkException(e)
                emit(Error(errorValues.message, errorValues.code))
            }

            emit(Resource.Loading(false))
        }
    }

    fun register(username: String, password: String, timezone: String): Flow<Resource<String>> {
        return flow {
            emit(Resource.Loading(true))

            try {
                val response = authApi.register(
                    CreateUserDTO(username, password, timezone))

                if (response.isSuccessful) {
                    response.body()?.takeIf {
                        it.username == username
                    } ?.let {
                        println(it)
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
            } catch (e: Exception) {
                val errorValues = handleNetworkException(e)
                emit(Error(errorValues.message, errorValues.code))
            }
            emit(Resource.Loading(false))
        }
    }

    suspend fun callback(state: String, code: String): Int {
        try {
            val response = authApi.callback(state, code)
            return response.code()
        } catch (e: Exception) {
            e.printStackTrace()
            return 600
        }

    }

}