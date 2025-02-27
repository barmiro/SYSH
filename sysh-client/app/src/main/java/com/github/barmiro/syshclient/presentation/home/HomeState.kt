package com.github.barmiro.syshclient.presentation.home

import com.github.barmiro.syshclient.data.stats.StatsDTO

data class HomeState(
    val isLoading: Boolean = false,
    val stats: StatsDTO = StatsDTO(),
    val userDisplayName: String = "unknown username"
//    TODO: add date range and stuff

)
