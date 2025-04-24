package com.github.barmiro.syshclient.util

import com.github.barmiro.syshclient.data.stats.StatsSeriesChunkDTO

fun averageStreamLengthInterpolator(statsSeries: List<StatsSeriesChunkDTO>): List<StatsSeriesChunkDTO> {
    val tempList = emptyList<StatsSeriesChunkDTO>().toMutableList()
    statsSeries.forEachIndexed { index, item ->
        if (!tempList.any {
                it.start_date == item.start_date && it.end_date == item.end_date
            }) {
            if (item.stream_count == 0) {
                if (index == 0) {
                    tempList.add(item)
                } else {
                    interpolateStats(statsSeries, index + 1)?.let { result ->
                        val startItem = statsSeries[index - 1]
                        val endItem = statsSeries[index + result.offset]
                        val streamsStep = (endItem.stream_count - startItem.stream_count) / result.offset
                        val minutesStep = (endItem.minutes_streamed - startItem.minutes_streamed) / result.offset
                        for (iteration in 1..result.offset) {
                            val streams = startItem.stream_count + streamsStep * iteration
                            val minutes = startItem.minutes_streamed + minutesStep * iteration
                            tempList.add(statsSeries[index - 1 + iteration].copy(
                                stream_count = streams,
                                minutes_streamed = minutes))
                        }
                    } ?: tempList.add(
                        item.copy(
                            minutes_streamed = statsSeries[index - 1].minutes_streamed,
                            stream_count = statsSeries[index - 1].stream_count
                        )
                    )
                }
            } else {
                tempList.add(item)
            }
        }
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