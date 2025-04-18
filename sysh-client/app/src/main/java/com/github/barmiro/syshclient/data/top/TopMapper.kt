package com.github.barmiro.syshclient.data.top

import com.github.barmiro.syshclient.data.top.dto.AlbumDTO
import com.github.barmiro.syshclient.data.top.dto.ArtistDTO
import com.github.barmiro.syshclient.data.top.dto.TrackDTO
import com.github.barmiro.syshclient.domain.top.TopAlbum
import com.github.barmiro.syshclient.domain.top.TopArtist
import com.github.barmiro.syshclient.domain.top.TopItemData
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

fun AlbumDTO.toTopAlbum(): TopAlbum {
    return TopAlbum(
        id = id,
        name = name,
        thumbnailUrl = thumbnail_url,
        primaryArtistName = primary_artist_name,
        minutesPlayed = total_ms_played / 60000,
        streamCount = stream_count
    )
}

fun ArtistDTO.toTopArtist(): TopArtist {
    return TopArtist(
        id = id,
        name = name,
        thumbnailUrl = thumbnail_url,
        minutesPlayed = total_ms_played / 60000,
        streamCount = stream_count
    )
}

fun TrackDTO.toTopItemData(): TopItemData {
    return TopItemData(
        id = spotify_track_id,
        name = name,
        albumName = album_name,
        thumbnailUrl = thumbnail_url,
        primaryArtistName = primary_artist_name,
        minutesPlayed = total_ms_played / 60000,
        streamCount = stream_count
    )
}

fun AlbumDTO.toTopItemData(): TopItemData {
    return TopItemData(
        id = id,
        name = name,
        thumbnailUrl = thumbnail_url,
        primaryArtistName = primary_artist_name,
        minutesPlayed = total_ms_played / 60000,
        streamCount = stream_count
    )
}

fun ArtistDTO.toTopItemData(): TopItemData {
    return TopItemData(
        id = id,
        name = name,
        thumbnailUrl = thumbnail_url,
        minutesPlayed = total_ms_played / 60000,
        streamCount = stream_count
    )
}