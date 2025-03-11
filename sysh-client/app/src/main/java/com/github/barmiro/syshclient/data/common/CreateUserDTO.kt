package com.github.barmiro.syshclient.data.common

import kotlinx.serialization.Serializable

@Serializable
data class CreateUserDTO(
    val username: String,
    val password: String
)
