package com.github.barmiro.syshclient.data.common.preferences

import kotlinx.serialization.Serializable

@Serializable
data class ServerInfo(
    val users_exist: Boolean,
    val is_restricted_mode: Boolean
)
