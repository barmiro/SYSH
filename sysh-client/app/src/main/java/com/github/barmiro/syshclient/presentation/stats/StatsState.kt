package com.github.barmiro.syshclient.presentation.stats

import com.github.barmiro.syshclient.data.stats.StatsDTO

// this is separate for future changes
data class StatsState(
    val isLoading: Boolean = true,
    val stats: StatsDTO = StatsDTO()
)