package com.github.barmiro.syshclient.data.settings.admin


import com.github.barmiro.syshclient.data.common.authentication.AdminCreateUserDTO
import com.github.barmiro.syshclient.data.common.authentication.CreateUserDTO
import com.github.barmiro.syshclient.data.common.authentication.RegisterResponse
import com.github.barmiro.syshclient.data.common.startup.UserDataDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AdminApi {

    @GET("admin/users")
    suspend fun users() : Response<List<UserDataDTO>>

    @POST("admin/users/create")
    suspend fun createUser(
        @Body user: AdminCreateUserDTO
    ) : Response<RegisterResponse>

    @POST("admin/users/resetPassword")
    suspend fun resetPassword(
        @Body user: CreateUserDTO
    ) : Response<RegisterResponse>

    @POST("admin/users/delete")
    suspend fun deleteUser(
        @Body deleteUserBody: DeleteUserDTO
    ) : Response<Int>
}