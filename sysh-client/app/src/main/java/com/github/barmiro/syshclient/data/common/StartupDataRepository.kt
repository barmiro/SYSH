package com.github.barmiro.syshclient.data.common

import com.github.barmiro.syshclient.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import retrofit2.Retrofit
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StartupDataRepository @Inject constructor() {
    val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.0.147:5754/")
        .build()

    val startupApi = retrofit.create(StartupDataApi::class.java)


    fun getUserDisplayName(): Flow<Resource<String>> {
        return flow {
            emit(Resource.Loading(true))
            val userDisplayName = try{
                startupApi.getUserData().string()
            } catch (e: IOException) {
                e.printStackTrace()
                emit(Resource.Error("Encountered IOException: " + e.message))
                ""
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error("Encountered HttpException: " + e.code()))
                ""
            }
            val isFetchSuccessful = !userDisplayName.isNullOrEmpty()
            if (isFetchSuccessful) {
                emit(
                    Resource.Success(
                        data = userDisplayName
                    ))
            } else {
                emit(Resource.Error("No username found"))
            }
            emit(Resource.Loading(false))
        }
    }

}