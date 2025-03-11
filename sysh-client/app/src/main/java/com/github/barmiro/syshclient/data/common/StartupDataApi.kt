package com.github.barmiro.syshclient.data.common

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET

interface StartupDataApi {

    @GET("userData")
    suspend fun getUserData(): Response<ResponseBody>
}