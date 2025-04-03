package com.github.barmiro.syshclient.presentation.top

import java.time.LocalDateTime

sealed class TopScreenEvent {
    object Refresh: TopScreenEvent()
    data class OnSearchParameterChange(
        val sort: String? = null,
        val start: LocalDateTime? = null,
        val end: LocalDateTime? = null
    ): TopScreenEvent()

    data class OnDateRangeModeChange(
        val dateRangeMode: String? = null
    ): TopScreenEvent()

    data class OnDateRangePageChange(
        val dateRangePage: Int? = null
    ): TopScreenEvent()
}