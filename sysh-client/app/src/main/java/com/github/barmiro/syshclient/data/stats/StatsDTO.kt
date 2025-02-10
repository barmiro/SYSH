package com.github.barmiro.syshclient.data.stats

import kotlinx.serialization.Serializable

@Serializable
data class StatsDTO(
    val start_date: String,
    val end_date: String,
    val minutes_streamed: Int,
    val stream_count: Int,
    val track_count: Int,
    val album_count: Int,
    val artist_count: Int
)
