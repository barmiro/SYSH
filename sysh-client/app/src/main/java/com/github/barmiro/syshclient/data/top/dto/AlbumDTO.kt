package com.github.barmiro.syshclient.data.top.dto

import kotlinx.serialization.Serializable

@Serializable
data class AlbumDTO(
    val id: String,
    val name: String,
    val sort_param: Int
)
