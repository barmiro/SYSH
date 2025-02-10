package com.github.barmiro.syshclient.presentation.top

sealed class TopTracksEvent {
    object Refresh: TopTracksEvent()
    data class OnSearchParameterChange(
        val sort: String,
        val start: String,
        val end: String
    ): TopTracksEvent()
}