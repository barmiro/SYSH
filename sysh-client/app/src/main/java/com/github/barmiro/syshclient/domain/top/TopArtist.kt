package com.github.barmiro.syshclient.domain.top

data class TopArtist(
    val id: String,
    val name: String,
    val thumbnailUrl: String,
    val minutesPlayed: Int,
    val streamCount: Int
) {
}