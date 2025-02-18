package com.github.barmiro.syshclient.domain.top

data class TopTrack(
    val id: String,
    val name: String,
    val albumName: String,
    val thumbnailUrl: String,
    val primaryArtistName: String,
    val minutesPlayed: Int,
    val streamCount: Int
) {
}