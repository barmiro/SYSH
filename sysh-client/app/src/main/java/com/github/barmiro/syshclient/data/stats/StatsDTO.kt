package com.github.barmiro.syshclient.data.stats

import kotlinx.serialization.Serializable

@Serializable
data class StatsDTO(
    val username: String? = null,
    val start_date: String? = null,
    val end_date: String? = null,
    val minutes_streamed: Int = 0,
    val stream_count: Int = 0,
    val track_count: Int = 0,
    val album_count: Int = 0,
    val artist_count: Int = 0
)


@Serializable
data class StatsSeriesChunkDTO(
    val username: String? = null,
    val start_date: String? = null,
    val end_date: String? = null,
    val minutes_streamed: Int = 0,
    val stream_count: Int = 0
)