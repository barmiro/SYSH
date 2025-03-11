package com.github.barmiro.syshclient.presentation.stats

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StatsScreen(
    viewModel: StatsViewModel
) {

    val state by viewModel.homeState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
    ) {
        StatsItem(itemText = "${state.stats.stream_count} streams")
        StatsItem(itemText = "${state.stats.minutes_streamed} minutes")
        StatsItem(itemText = "${state.stats.minutes_streamed / 60} hours")
        StatsItem(itemText = "${String.format("%.1f", state.stats.minutes_streamed / 1440f)} days")
        StatsItem(itemText = "${state.stats.track_count} tracks")
        StatsItem(itemText = "${state.stats.album_count} albums")
        StatsItem(itemText = "${state.stats.artist_count} artists")
    }
}