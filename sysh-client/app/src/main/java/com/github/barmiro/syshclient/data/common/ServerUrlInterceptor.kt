package com.github.barmiro.syshclient.data.common

import com.github.barmiro.syshclient.data.common.preferences.UserPreferencesRepository
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Interceptor
import okhttp3.Response

class ServerUrlInterceptor(
    private val userPrefRepo: UserPreferencesRepository
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        val serverUrl = userPrefRepo.serverUrlFlow.value?.toHttpUrl()
        val requestUrl: HttpUrl = chain.request().url

        val newUrl: HttpUrl = requestUrl.newBuilder()
            .scheme(serverUrl?.scheme ?: requestUrl.scheme)
            .host(serverUrl?.host ?: requestUrl.host)
            .port(serverUrl?.port ?: requestUrl.port)
            .build()

        val request = chain.request().newBuilder()
            .url(newUrl)
            .build()

        return chain.proceed(request)
    }
}