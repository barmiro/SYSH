package com.github.barmiro.syshclient.data.settings.dataimport

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ImportApi {

    @POST("addJson")
    suspend fun addJson(
        @Body jsonBody: RequestBody
    ) : Response<ResponseBody>

    @Multipart
    @POST("uploadZip")
    suspend fun uploadZip(
        @Part zip: MultipartBody.Part
    ) : Response<ResponseBody>

    @GET("recent")
    suspend fun recent() : Response<Unit>
}