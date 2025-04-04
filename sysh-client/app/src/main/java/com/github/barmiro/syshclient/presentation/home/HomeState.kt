package com.github.barmiro.syshclient.presentation.home

import com.github.barmiro.syshclient.data.stats.HomeStatsDTO

data class HomeState(
    val isLoading: Boolean = true,
    val stats: HomeStatsDTO = HomeStatsDTO()
//    TODO: add date range and stuff

)
