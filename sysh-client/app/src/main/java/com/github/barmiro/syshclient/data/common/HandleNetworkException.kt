package com.github.barmiro.syshclient.data.common

import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

fun handleNetworkException(e: Exception): ErrorValues {
    e.printStackTrace()
    return when (e) {
        is ConnectException -> ErrorValues(
            message = "Cannot connect to the server. Check your internet connection or server URL.",
            code = 601
        )
        is UnknownHostException -> ErrorValues(
            message = "Server address could not be found. Check your URL.",
            code = 602
        )
        is SocketTimeoutException -> ErrorValues(
            message = "Connection timed out. The server took too long to respond.",
            code = 603
        )
        is IOException -> ErrorValues(
            message = "Network error: ${e.message}. Please check your connection.",
            code = 604
        )
        else -> ErrorValues(
            message = "Unexpected error: ${e.message}",
            code = 600
        )
    }
}


// this is a workaround - Resource requires a type parameter
data class ErrorValues(
    val message: String,
    val code: Int
)