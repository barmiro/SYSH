package com.github.barmiro.syshclient.data.common.authentication

import kotlinx.serialization.Serializable

@Serializable
data class TokenResponse(
    val username: String,
    val token: String
)
