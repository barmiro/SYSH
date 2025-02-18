package com.github.barmiro.syshclient.data.top.dto

import kotlinx.serialization.Serializable

@Serializable
data class AlbumDTO(
    val id: String,
    val name: String,
    val thumbnail_url: String,
    val primary_artist_name: String,
    val total_ms_played: Int,
    val stream_count: Int
)
