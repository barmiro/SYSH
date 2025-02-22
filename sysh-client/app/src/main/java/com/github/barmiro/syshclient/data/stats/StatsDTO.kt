package com.github.barmiro.syshclient.data.stats

import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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

@Serializable
data class StartupDTO(
    val ts: String
) {
    fun toLocalDate(): LocalDate {
        val dateFromTimestamp = ts.substringBefore('T')
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return LocalDate.parse(dateFromTimestamp, formatter)
    }
}