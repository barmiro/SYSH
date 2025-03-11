package com.github.barmiro.syshclient.data.common.authentication

import kotlinx.serialization.Serializable

@Serializable
data class TokenResponse(
    val token: String
)
