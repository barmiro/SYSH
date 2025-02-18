package com.github.barmiro.syshclient.data.top.dto

import kotlinx.serialization.Serializable

@Serializable
data class TrackDTO(
    val spotify_track_id: String,
    val name: String,
    val album_name: String,
    val thumbnail_url: String,
    val primary_artist_name: String,
    val total_ms_played: Int,
    val stream_count: Int
)
