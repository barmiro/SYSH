package com.github.barmiro.syshclient.data.stats

import com.github.barmiro.syshclient.util.OffsetDateTimeSerializer
import com.github.barmiro.syshclient.util.ZonedDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime
import java.time.ZonedDateTime

@Serializable
data class StatsDTO(
    val username: String? = null,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val start_date: OffsetDateTime? = null,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val end_date: OffsetDateTime? = null,
    val minutes_streamed: Int = 0,
    val stream_count: Int = 0,
    val track_count: Int = 0,
    val album_count: Int = 0,
    val artist_count: Int = 0
)


@Serializable
data class StatsSeriesChunkDTO(
    val username: String? = null,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val start_date: OffsetDateTime? = null,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val end_date: OffsetDateTime? = null,
    val minutes_streamed: Int = 0,
    val stream_count: Int = 0
)

@Serializable
data class FirstStreamDateDTO(
    @Serializable(with = ZonedDateTimeSerializer::class)
    val firstStreamDate: ZonedDateTime
)
