package com.github.barmiro.syshclient.data.top

import com.github.barmiro.syshclient.data.top.dto.TrackDTO
import com.github.barmiro.syshclient.domain.top.TopTrack

fun TrackDTO.toTopTrack(): TopTrack {
    return TopTrack(
        id = spotify_track_id,
        name = name,
        albumName = album_name,
        thumbnailUrl = thumbnail_url,
        primaryArtistName = primary_artist_name,
        minutesPlayed = total_ms_played / 60000,
        streamCount = stream_count
    )
}