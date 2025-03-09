package com.github.barmiro.syshclient.data.common

import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthenticationApi {

    @POST("token")
    suspend fun getToken(
        @Header("Authorization") authHeader: String
    ) : ResponseBody

    @POST("register")
    suspend fun register(
        @Body user: CreateUserDTO
    ) : ResponseBody
}