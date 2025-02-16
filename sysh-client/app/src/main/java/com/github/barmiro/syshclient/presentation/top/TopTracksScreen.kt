package com.github.barmiro.syshclient.presentation.top

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TopTracksScreen(
    viewModel: TopTracksViewModel
) {
    val state = viewModel.state
    viewModel.observeLifecycle(LocalLifecycleOwner.current.lifecycle)

    var dateRange by remember { mutableStateOf<Pair<Long?, Long?>?>(null) }



    var isDateRangePickerVisible by remember { mutableStateOf(false) }

    if (isDateRangePickerVisible) {
        DateRangePickerModal(
            onDateRangeSelected = {
                dateRange = it
                val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                val start = formatter.format(it.first)
                val end = formatter.format(it.second)
                viewModel.onEvent((TopTracksEvent.OnSearchParameterChange(state.sort, start, end)))
            },
            onDismiss = {
                isDateRangePickerVisible = false
            }
        )
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (state.sort == "time") {
            Button(
                onClick = {
                    viewModel.onEvent(TopTracksEvent.OnSearchParameterChange(null, state.start, state.end))
                }
            ) {
                Text(text = "Sort by count")
            }
        } else {
            Button(
                onClick = {
                    viewModel.onEvent(TopTracksEvent.OnSearchParameterChange("time", state.start, state.end))
                }
            ) {
                Text(text = "Sort by time")
            }
        }
        Button(
            onClick = {
                isDateRangePickerVisible = true
            }
        ) {
            Text(text = "Select date range")
        }
        if(dateRange != null) {
            val start = Date(dateRange!!.first!!)
            val end = Date(dateRange!!.second!!)

            val formattedStart = SimpleDateFormat(
                "MMM dd, yyyy",
                Locale.getDefault())
                .format(start)

            val formattedEnd = SimpleDateFormat(
                "MMM dd, yyyy",
                Locale.getDefault())
                .format(end)

            Text("Selected range: $formattedStart - $formattedEnd")
        } else {
            Text("No date range selected")
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(state.tracks.size) { i ->
                val track = state.tracks[i]
                TrackItem(
                    index = i + 1,
                    track = track,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            //TODO
                        }
                        .padding(16.dp)
                )
                if (i < state.tracks.size) {
                    HorizontalDivider(
                        modifier = Modifier.padding(
                            horizontal = 16.dp
                        ))
                }
            }
        }
    }
}

@Composable
fun <T: LifecycleObserver> T.observeLifecycle(lifecycle: Lifecycle) {
    DisposableEffect(lifecycle) {
        lifecycle.addObserver(this@observeLifecycle)
        onDispose {
            lifecycle.removeObserver(this@observeLifecycle)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerModal(
    onDateRangeSelected: (Pair<Long?, Long?>) -> Unit,
    onDismiss: () -> Unit
) {
    val dateRangePickerState = rememberDateRangePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onDateRangeSelected(
                        Pair(
                            dateRangePickerState.selectedStartDateMillis,
                            dateRangePickerState.selectedEndDateMillis
                        )
                    )
                    onDismiss()
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    ) {
        DateRangePicker(
            state = dateRangePickerState,
            title = {
                Text(
                    text = "Select date range"
                )
            },
            showModeToggle = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .padding(16.dp)
        )
    }
}