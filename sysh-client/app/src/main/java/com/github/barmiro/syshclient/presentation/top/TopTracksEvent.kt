package com.github.barmiro.syshclient.presentation.top

sealed class TopTracksEvent {
    object Refresh: TopTracksEvent()
    data class OnSearchParameterChange(
        val sort: String? = null,
        val start: String? = null,
        val end: String? = null
    ): TopTracksEvent()
}