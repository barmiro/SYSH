package com.github.barmiro.syshclient.domain.top

data class TopItemData(
    val id: String,
    val name: String,
    val thumbnailUrl: String,
    val minutesPlayed: Int,
    val streamCount: Int,
    val albumName: String? = null,
    val primaryArtistName: String? = null
) {
}
