package com.github.barmiro.syshclient.presentation.top

sealed class TopScreenEvent {
    object Refresh: TopScreenEvent()
    data class OnSearchParameterChange(
        val sort: String? = null,
        val start: String? = null,
        val end: String? = null,
    ): TopScreenEvent()
}