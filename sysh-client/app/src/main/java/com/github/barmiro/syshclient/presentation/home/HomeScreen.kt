package com.github.barmiro.syshclient.presentation.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    viewModel: HomeViewModel
) {

    val state by viewModel.homeState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
    ) {
        HomeItem(itemText = "Welcome, " + state.userDisplayName)
        HomeItem(itemText = "${state.stats.stream_count} streams")
        HomeItem(itemText = "${state.stats.minutes_streamed} minutes")
        HomeItem(itemText = "${state.stats.minutes_streamed / 60} hours")
        HomeItem(itemText = "${String.format("%.1f", state.stats.minutes_streamed / 1440f)} days")
        HomeItem(itemText = "${state.stats.track_count} tracks")
        HomeItem(itemText = "${state.stats.album_count} albums")
        HomeItem(itemText = "${state.stats.artist_count} artists")
    }
}