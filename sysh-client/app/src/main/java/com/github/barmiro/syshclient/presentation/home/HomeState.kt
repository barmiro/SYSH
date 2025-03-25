package com.github.barmiro.syshclient.presentation.home

import com.github.barmiro.syshclient.data.stats.StatsDTO

data class HomeState(
    val isLoading: Boolean = true,
    val stats: StatsDTO = StatsDTO()
//    TODO: add date range and stuff

)
