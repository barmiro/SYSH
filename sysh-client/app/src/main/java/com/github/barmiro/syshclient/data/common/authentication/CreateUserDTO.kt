package com.github.barmiro.syshclient.data.common.authentication

import kotlinx.serialization.Serializable

@Serializable
data class CreateUserDTO(
    val username: String,
    val password: String,
    val timezone: String
)

@Serializable
data class AdminCreateUserDTO(
    val username: String,
    val password: String,
    val timezone: String,
    val role: String
)
