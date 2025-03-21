package com.github.barmiro.syshclient.data.common.startup

import com.github.barmiro.syshclient.data.common.preferences.ServerInfo
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET

interface StartupDataApi {

    @GET("info")
    suspend fun getServerInfo(): Response<ServerInfo>

    @GET("userData")
    suspend fun getUserData(): Response<ResponseBody>

    @GET("authorize")
    suspend fun spotifyAuthorize(): Response<ResponseBody>
}