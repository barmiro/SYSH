package com.github.barmiro.syshclient.data.common

import com.github.barmiro.syshclient.data.common.preferences.UserPreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.Response

class ServerErrorInterceptor(
    private val userPrefRepo: UserPreferencesRepository
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        if (response.code == 401) {
            CoroutineScope(Dispatchers.IO).launch {
                userPrefRepo.logout()
            }
        } else if (response.code == 403) {
            CoroutineScope(Dispatchers.IO).launch {
                userPrefRepo.setAuthorizedWithSpotify(false)
            }
        }

        return response
    }
}