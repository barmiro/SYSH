package com.github.barmiro.syshclient.data.settings.admin

import kotlinx.serialization.Serializable

@Serializable
data class DeleteUserDTO(
    val username: String
)
