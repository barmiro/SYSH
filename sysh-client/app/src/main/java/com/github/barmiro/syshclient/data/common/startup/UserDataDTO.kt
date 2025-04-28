package com.github.barmiro.syshclient.data.common.startup

import kotlinx.serialization.Serializable

@Serializable
data class UserDataDTO(
    val username: String,
    val display_name: String,
    val timezone: String,
    val has_imported_data: Boolean,
    val role: String // create an enum?
)
