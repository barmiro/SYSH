package com.github.barmiro.syshclient.presentation.top

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.barmiro.syshclient.presentation.top.albums.TopAlbumsScreen
import com.github.barmiro.syshclient.presentation.top.albums.TopAlbumsViewModel
import com.github.barmiro.syshclient.presentation.top.artists.TopArtistsScreen
import com.github.barmiro.syshclient.presentation.top.artists.TopArtistsViewModel
import com.github.barmiro.syshclient.presentation.top.tracks.TopTracksScreen
import com.github.barmiro.syshclient.presentation.top.tracks.TopTracksViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TopScreen(
    viewModel: TopScreenViewModel,
    topTracksVM: TopTracksViewModel,
    topAlbumsVM: TopAlbumsViewModel,
    topArtistsVM: TopArtistsViewModel
) {

    val state by viewModel.state.collectAsState()

    val pagerState = rememberPagerState(pageCount = { 3 })
    //                                    not using lifecycleScope because the animations need a MonotonicFrameClock
    val coroutineScope = rememberCoroutineScope()

    var dateRange by remember { mutableStateOf<Pair<Long?, Long?>?>(null) }

    var isDateRangePickerVisible by remember { mutableStateOf(false) }

    if (isDateRangePickerVisible) {
        DateRangePickerModal(
            onDateRangeSelected = {
                dateRange = it
                val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                val start = formatter.format(it.first)
                val end = formatter.format(it.second)

                viewModel.onEvent((TopScreenEvent.OnSearchParameterChange(null, start, end)))
            },
            onDismiss = {
                isDateRangePickerVisible = false
            }
        )
    }

    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 40.dp)) {
        HorizontalPager(state = pagerState) { page ->
            when (page) {
                0 -> TopTracksScreen(topTracksVM)
                1 -> TopAlbumsScreen(topAlbumsVM)
                2 -> TopArtistsScreen(topArtistsVM)
                else -> Text("Something went wrong with the pager")
            }
        }
    }
    Box (modifier = Modifier.fillMaxSize()) {
        Column {
            if (state.sort == "time") {
                Button(
                    onClick = {
                        viewModel.onEvent(
                            TopScreenEvent.OnSearchParameterChange(
                                "count",
                                state.start,
                                state.end
                            )
                        )
                    }
                ) {
                    Text(text = "Sort by count")
                }
            } else {
                Button(
                    onClick = {
                        viewModel.onEvent(
                            TopScreenEvent.OnSearchParameterChange(
                                "time",
                                state.start,
                                state.end
                            )
                        )
                    }
                ) {
                    Text(text = "Sort by time")
                }
            }
            Column {
                Button(
                    onClick = {
                        isDateRangePickerVisible = true
                    }
                ) {
                    Text(text = "Select date range")
                }
                if (dateRange != null) {
                    val start = Date(dateRange!!.first!!)
                    val end = Date(dateRange!!.second!!)

                    val formattedStart = SimpleDateFormat(
                        "MMM dd, yyyy",
                        Locale.getDefault()
                    )
                        .format(start)

                    val formattedEnd = SimpleDateFormat(
                        "MMM dd, yyyy",
                        Locale.getDefault()
                    )
                        .format(end)

                    Text("Selected range: $formattedStart - $formattedEnd")
                } else {
                    Text("No date range selected")
                }
            }
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Bottom
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.Bottom
                ) {
                    NavigationBarItem(
                        selected = pagerState.currentPage == 0,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(0)
                            }
                        },
                        icon = { Text("Tracks") },
                        modifier = Modifier.offset(y = 20.dp)
                    )
                    NavigationBarItem(
                        selected = pagerState.currentPage == 1,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(1)
                            }
                        },
                        icon = { Text("Albums") },
                        modifier = Modifier.offset(y = 20.dp)
                    )
                    NavigationBarItem(
                        selected = pagerState.currentPage == 2,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(2)
                            }
                        },
                        icon = { Text("Artists") },
                        modifier = Modifier.offset(y = 20.dp)
                    )
//                                                SingleChoiceSegmentedButtonRow {
//                                                    topNavItems.forEachIndexed { index, label ->
//                                                        SegmentedButton(
//                                                            shape = SegmentedButtonDefaults.itemShape(index = index, count = topNavItems.size),
//                                                            onClick = {
//                                                                coroutineScope.launch {
//                                                                    pagerState.animateScrollToPage(index)
//                                                                }
//                                                            },
//                                                            selected = index == pagerState.currentPage,
//                                                            modifier = Modifier.height(30.dp)
//                                                        ) {
//                                                            Text(text = label,
//                                                                fontSize = 14.sp,
//                                                                lineHeight = 14.sp)
//                                                        }
//                                                    }
//                                                }
                }
            }
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