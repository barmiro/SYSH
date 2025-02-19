package com.github.barmiro.syshclient.data.top.dto

import kotlinx.serialization.Serializable

@Serializable
data class ArtistDTO(
    val id: String,
    val name: String,
    val thumbnail_url: String,
    val total_ms_played: Int,
    val stream_count: Int
)
