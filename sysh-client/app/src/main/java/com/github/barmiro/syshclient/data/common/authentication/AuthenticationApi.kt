package com.github.barmiro.syshclient.data.common.authentication

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthenticationApi {

    @POST("token")
    suspend fun getToken(
        @Header("Authorization") authHeader: String
    ) : Response<TokenDTO>

    @POST("register")
    suspend fun register(
        @Body user: CreateUserDTO
    ) : Response<String>
}