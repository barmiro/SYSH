package com.github.barmiro.syshclient.data.common.authentication

import com.github.barmiro.syshclient.data.common.preferences.UserPreferencesRepository
import okhttp3.Interceptor
import okhttp3.Response

class JwtInterceptor(
    private val userPrefRepo: UserPreferencesRepository
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = userPrefRepo.tokenFlow.value
        val request = chain.request().newBuilder()
        token?.let { request.addHeader("Authorization", "Bearer $it")}

        return chain.proceed(request.build())
    }
}
