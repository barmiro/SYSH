package com.github.barmiro.syshclient.util

import com.github.barmiro.syshclient.data.stats.StatsSeriesChunkDTO

fun averageStreamLength(item: StatsSeriesChunkDTO): Float {
    return if (item.stream_count == 0) 0f else 1f * item.minutes_streamed / item.stream_count
}

fun averageStreamLengthInterpolator(statsSeries: List<StatsSeriesChunkDTO>): List<Float> {
    val tempList = emptyList<Float>().toMutableList()
    var index = 0
    while(index < statsSeries.size) {
        if (statsSeries[index].stream_count > 0 || index == 0) {
            tempList.add(averageStreamLength(statsSeries[index]))
        } else {
            interpolateStats(statsSeries, index + 1)?.let { result ->
                val startItem = statsSeries[index - 1]
                val endItem = result.foundItem
                val startAverage = averageStreamLength(startItem)
                val endAverage = averageStreamLength(endItem)
                val step = (endAverage - startAverage) / (result.offset + 1)
                for (iteration in 1..result.offset) {
                    val value = startAverage + step * iteration
                    tempList.add(value)
                    index++
                }
            } ?: tempList.add(averageStreamLength(statsSeries[index - 1]))
        }
        index++
    }

    return tempList
}

fun interpolateStats(statsSeries: List<StatsSeriesChunkDTO>, index: Int, offset: Int = 1): StatsInterpolationResult? {
    return if (index < statsSeries.size) {
        if (statsSeries[index].stream_count == 0) {
            interpolateStats(statsSeries, index + 1, offset + 1) ?: StatsInterpolationResult(statsSeries[index], offset)
        } else {
            StatsInterpolationResult(statsSeries[index], offset)
        }
    } else {
        null
    }
}


data class StatsInterpolationResult(
    val foundItem: StatsSeriesChunkDTO,
    val offset: Int
)