package com.github.barmiro.syshclient.data.settings

import com.github.barmiro.syshclient.data.common.authentication.PasswordChangeRequest
import com.github.barmiro.syshclient.data.common.authentication.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface SettingsApi {

    @POST("changePassword")
    suspend fun changePassword(
        @Body request: PasswordChangeRequest
    ) : Response<RegisterResponse>

}