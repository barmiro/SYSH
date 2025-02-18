package com.github.barmiro.syshclient.presentation.top.albums

import com.github.barmiro.syshclient.domain.top.TopAlbum

data class TopAlbumsState(
    val albumList: List<TopAlbum> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val sort: String? = null,
    val start: String? = null,
    val end: String? = null
)
