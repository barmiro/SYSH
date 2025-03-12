package com.github.barmiro.syshclient.presentation.stats

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.barmiro.syshclient.presentation.top.TopScreenEvent
import com.github.barmiro.syshclient.presentation.top.components.TopScreenBottomBar
import com.github.barmiro.syshclient.presentation.top.components.TopScreenTopBar
import com.github.barmiro.syshclient.presentation.top.components.TopScreenTopText

@SuppressLint("DefaultLocale")
@Composable
fun StatsScreen(
    viewModel: StatsViewModel
) {

    val homeState by viewModel.homeState.collectAsState()
    val state by viewModel.state.collectAsState()
//
//    var dateRange by remember { mutableStateOf<Pair<Long?, Long?>?>(null) }
//    var dateRangeMode by remember { mutableStateOf("") }
    var isDateRangePickerVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopScreenTopBar(
                state = state,
                onDateRangeModeChange = { viewModel.onEvent(TopScreenEvent.OnDateRangeModeChange(it)) },
                onDateRangePickerVisibilityChange = { isDateRangePickerVisible = it },
                onVMSearchParameterChange = { viewModel.onEvent(it) },
                onDateRangePageChange = {viewModel.onEvent(it) },
                titleText = "Stats",
                actions = { }
            )
//            TODO: this is a terrible hack
            Box(
                modifier = Modifier.fillMaxWidth().height(88.dp), contentAlignment = Alignment.BottomCenter
            ) {
                TopScreenTopText(state)
            }
        }
    ) { innerPadding ->
        Row(modifier = Modifier.fillMaxWidth().padding(top = innerPadding.calculateTopPadding(), bottom = 0.dp)) {
            Column(
                modifier = Modifier.fillMaxSize().padding(32.dp),
            ) {
                StatsItem(itemText = "${homeState.stats.stream_count} streams")
                StatsItem(itemText = "${homeState.stats.minutes_streamed} minutes")
                StatsItem(itemText = "${homeState.stats.minutes_streamed / 60} hours")
                StatsItem(
                    itemText = "${
                        String.format(
                            "%.1f",
                            homeState.stats.minutes_streamed / 1440f
                        )
                    } days"
                )
                StatsItem(itemText = "${homeState.stats.track_count} tracks")
                StatsItem(itemText = "${homeState.stats.album_count} albums")
                StatsItem(itemText = "${homeState.stats.artist_count} artists")
            }
        }
        TopScreenBottomBar(
            state = state,
            onVMSearchParameterChange = { viewModel.onEvent(it)},
            onDateRangePageChange = { viewModel.onEvent(it) }
        )
    }
}