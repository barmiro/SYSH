package com.github.barmiro.syshclient.domain.top

data class TopAlbum(
    val id: String,
    val name: String,
    val thumbnailUrl: String,
    val primaryArtistName: String,
    val minutesPlayed: Int,
    val streamCount: Int
) {
}