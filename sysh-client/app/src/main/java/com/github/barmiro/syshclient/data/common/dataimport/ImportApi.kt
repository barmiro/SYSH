package com.github.barmiro.syshclient.data.common.dataimport

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ImportApi {

    @POST("addJson")
    suspend fun addJson(
        @Body jsonBody: RequestBody
    ) : Response<ResponseBody>

    @GET("recent")
    suspend fun recent() : Response<Unit>
}