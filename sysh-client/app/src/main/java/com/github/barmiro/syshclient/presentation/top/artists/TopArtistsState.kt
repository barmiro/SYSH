package com.github.barmiro.syshclient.presentation.top.artists

import com.github.barmiro.syshclient.domain.top.TopArtist

data class TopArtistsState(
    val artistList: List<TopArtist> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val sort: String? = null,
    val start: String? = null,
    val end: String? = null
)
