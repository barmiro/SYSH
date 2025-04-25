package com.github.barmiro.syshclient.data.common.authentication

import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponse(
    val username: String,
    val role: String
)
