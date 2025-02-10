package com.github.barmiro.syshclient.data.top.dto

import kotlinx.serialization.Serializable

@Serializable
data class TrackDTO(
    val spotify_track_id: String,
    val name: String,
    val sort_param: Int
)
