package com.github.barmiro.syshclient.data.stats

import com.github.barmiro.syshclient.data.top.dto.AlbumDTO
import com.github.barmiro.syshclient.data.top.dto.ArtistDTO
import com.github.barmiro.syshclient.data.top.dto.TrackDTO
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
data class HomeStatsDTO(
    val day_minutes: Int = 0,
    val day_streams: Int = 0,
    val minutes_streamed: Int = 0,
    val stream_count: Int = 0,
    val top_artist: ArtistDTO? = null,
    val top_album: AlbumDTO? = null,
    val top_track: TrackDTO? = null
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

@Serializable
data class HourlyStatsDTO(
    val hour: Int,
    val minutes_streamed: Int = 0,
)
