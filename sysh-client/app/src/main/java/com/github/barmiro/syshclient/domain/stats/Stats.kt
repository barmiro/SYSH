package com.github.barmiro.syshclient.domain.stats

data class Stats(
    val start_date: String? = null,
    val end_date: String? = null,
    val minutes_streamed: Int,
    val stream_count: Int,
    val track_count: Int,
    val album_count: Int,
    val artist_count: Int
)
