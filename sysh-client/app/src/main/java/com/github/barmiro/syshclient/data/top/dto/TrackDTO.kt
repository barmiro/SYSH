package com.github.barmiro.syshclient.data.top.dto

import kotlinx.serialization.Serializable

@Serializable
data class TrackDTO(
    val spotify_track_id: String,
    val name: String,
    val album_name: String,
    val thumbnail_url: String,
    val artist_names: String,
    val minutes_played: Int,
    val stream_count: Int
)
