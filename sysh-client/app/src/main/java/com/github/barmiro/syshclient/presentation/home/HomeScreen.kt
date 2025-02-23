package com.github.barmiro.syshclient.presentation.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(
    viewModel: HomeViewModel
) {

    val state by viewModel.homeState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
    ) {
        Row(
        ) {
            Text(text = "Welcome to SYSH",
                fontSize = 32.sp,
                color = MaterialTheme.colorScheme.onBackground)
        }
        Row(
        ) {
            Text(text = "${state.stats.stream_count} streams",
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onBackground)
        }
        Row(
        ) {
            Text(text = "${state.stats.minutes_streamed} minutes",
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onBackground)
        }
        Row(
        ) {
            Text(text = "${state.stats.minutes_streamed / 60} hours",
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onBackground)
        }
        Row(
        ) {
            Text(text = "${String.format("%.1f", state.stats.minutes_streamed / 1440f)} days",
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onBackground)
        }
        Row(
        ) {
            Text(text = "${state.stats.track_count} tracks",
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onBackground)
        }
        Row(
        ) {
            Text(text = "${state.stats.album_count} albums",
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onBackground)
        }
        Row(
        ) {
            Text(text = "${state.stats.artist_count} artists",
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onBackground)
        }

    }
}