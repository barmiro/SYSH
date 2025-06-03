package com.github.barmiro.syshclient.data.settings.admin

import com.github.barmiro.syshclient.data.common.ServerErrorInterceptor
import com.github.barmiro.syshclient.data.common.ServerUrlInterceptor
import com.github.barmiro.syshclient.data.common.authentication.AdminCreateUserDTO
import com.github.barmiro.syshclient.data.common.authentication.CreateUserDTO
import com.github.barmiro.syshclient.data.common.authentication.JwtInterceptor
import com.github.barmiro.syshclient.data.common.handleNetworkException
import com.github.barmiro.syshclient.data.common.preferences.UserPreferencesRepository
import com.github.barmiro.syshclient.data.common.startup.UserDataDTO
import com.github.barmiro.syshclient.util.Resource
import com.github.barmiro.syshclient.util.Resource.Error
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdminRepository @Inject constructor(
    private val userPrefRepo: UserPreferencesRepository
){

    val client = OkHttpClient.Builder()
        .addInterceptor(ServerUrlInterceptor(userPrefRepo))
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

    val adminApi = retrofit.create(AdminApi::class.java)

    fun getUsers(): Flow<Resource<List<UserDataDTO>>> {
        return flow {
            emit(Resource.Loading(true))
            try {
                val response = adminApi.users()

                if (response.isSuccessful) {
                    emit(Resource.Success(response.body()))
                } else {
                    emit(Resource.Error(response.message()))
                }
            } catch (e: Exception) {
                val errorValues = handleNetworkException(e)
                emit(Error(errorValues.message, errorValues.code))
            }
        }
    }

    fun createUser(username: String,
                   password: String,
                   timezone: String,
                   role: String): Flow<Resource<String>> {
        return flow {
            emit(Resource.Loading(true))

            try {
                val response = adminApi.createUser(
                    AdminCreateUserDTO(username, password, timezone, role)
                )

                if (response.isSuccessful) {
                    response.body()?.takeIf {
                        it.username == username
                    }?.let {
                        emit(
                            Resource.Success(
                                data = it.username
                            )
                        )
                    } ?: emit(
                        Error(
                            message = "User creation failed"
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

    fun resetPassword(username: String,
                   password: String): Flow<Resource<String>> {
        return flow {
            emit(Resource.Loading(true))

            try {
                val response = adminApi.resetPassword(
                    CreateUserDTO(username, password, "UTC")
                )

                if (response.isSuccessful) {
                    response.body()?.takeIf {
                        it.username == username
                    }?.let {
                        emit(
                            Resource.Success(
                                data = it.username
                            )
                        )
                    } ?: emit(
                        Error(
                            message = "User creation failed"
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

    fun deleteUser(username: String): Flow<Resource<Int>> {
        return flow {
            emit(Resource.Loading(true))
            try {
                val response = adminApi.deleteUser(DeleteUserDTO(username))

                if (response.isSuccessful) {
                    emit(Resource.Success(response.body()))
                } else {
                    emit(Resource.Error(response.message()))
                }
            } catch (e: Exception) {
                val errorValues = handleNetworkException(e)
                emit(Error(errorValues.message, errorValues.code))
            }
        }
    }
}