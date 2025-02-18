package com.github.barmiro.syshclient.presentation.top

import com.github.barmiro.syshclient.domain.top.TopTrack

data class TopTracksState(
    val tracks: List<TopTrack> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val sort: String? = null,
    val start: String? = null,
    val end: String? = null
)
