package com.github.barmiro.syshclient.presentation.top.tracks

import com.github.barmiro.syshclient.domain.top.TopTrack

data class TopTracksState(
    val trackList: List<TopTrack> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val sort: String? = null,
    val start: String? = null,
    val end: String? = null
)
