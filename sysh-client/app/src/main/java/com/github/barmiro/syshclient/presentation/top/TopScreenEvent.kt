package com.github.barmiro.syshclient.presentation.top

sealed class TopScreenEvent {
    object Refresh: TopScreenEvent()
    data class OnSearchParameterChange(
        val sort: String? = null,
        val start: String? = null,
        val end: String? = null
    ): TopScreenEvent()

    data class OnDateRangeModeChange(
        val dateRangeMode: String? = null
    ): TopScreenEvent()

    data class OnDateRangePageChange(
        val dateRangePage: Int? = null
    ): TopScreenEvent()
}