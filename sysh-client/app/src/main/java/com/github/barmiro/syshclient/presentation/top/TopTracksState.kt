package com.github.barmiro.syshclient.presentation.top

import com.github.barmiro.syshclient.data.top.dto.TrackDTO

data class TopTracksState(
    val tracks: List<TrackDTO> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val sort: String? = null,
    val start: String? = null,
    val end: String? = null
)
