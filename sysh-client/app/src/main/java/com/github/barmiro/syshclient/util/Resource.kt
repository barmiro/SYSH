package com.github.barmiro.syshclient.util

sealed class Resource<T>(val data: T? = null, val message: String? = null, val code: Int? = null) {
    class Success<T>(data: T?): Resource <T>(data)
    class Error<T>(message: String, data: T? = null, code: Int? = null): Resource<T>(data, message, code)
    class Loading<T>(val isLoading: Boolean = true): Resource<T>(null)
}