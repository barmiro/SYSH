package com.github.barmiro.syshclient.data.common.authentication

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthenticationApi {

    @POST("token")
    suspend fun getToken(
        @Header("Authorization") authHeader: String
    ) : Response<TokenResponse>

    @POST("register")
    suspend fun register(
        @Body user: CreateUserDTO
    ) : Response<RegisterResponse>

//    this should be a POST request,
//    GET to keep it consistent with how Spotify handles this
    @GET("callback")
    suspend fun callback(
        @Query("state") state: String,
        @Query("code") code: String
    ) : Response<Void>
}