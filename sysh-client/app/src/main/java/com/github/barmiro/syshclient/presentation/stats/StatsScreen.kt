package com.github.barmiro.syshclient.presentation.stats

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.barmiro.syshclient.presentation.top.TopScreenEvent
import com.github.barmiro.syshclient.presentation.top.components.DateRangePickerModal
import com.github.barmiro.syshclient.presentation.top.components.StatsScreenTopText
import com.github.barmiro.syshclient.presentation.top.components.TopScreenBottomBar
import com.github.barmiro.syshclient.presentation.top.components.TopScreenTopBar
import com.patrykandpatrick.vico.compose.common.vicoTheme
import java.text.NumberFormat
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("DefaultLocale")
@Composable
fun StatsScreen(
    viewModel: StatsViewModel
) {

    val statsState by viewModel.statsState.collectAsState()
    val state by viewModel.state.collectAsState()
    val statsSeries by viewModel.statsSeries.collectAsState()
    val hourlyStats by viewModel.hourlyStats.collectAsState()

    var isDateRangePickerVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.getStatsSeries()
        viewModel.getStats()
        viewModel.getHourlyStats()
    }

    if (isDateRangePickerVisible) {
        DateRangePickerModal(
            onVMSearchParameterChange = { viewModel.onEvent(it) },
            onDateRangeModeChange = { viewModel.onEvent(it) },
            onDismiss = {
                isDateRangePickerVisible = false
            }
        )
    }

    //    workaround for the refresh indicator not disappearing
    var isRefreshing by remember { mutableStateOf(false) }
    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            isRefreshing = false
        }
    }

    Scaffold(
        topBar = {
            TopScreenTopBar(
                state = state,
                onDateRangeModeChange = { viewModel.onEvent(TopScreenEvent.OnDateRangeModeChange(it)) },
                onDateRangePickerVisibilityChange = { isDateRangePickerVisible = it },
                onVMSearchParameterChange = { viewModel.onEvent(it) },
                onDateRangePageChange = {viewModel.onEvent(it) },
                titleText = "Stats",
                animatedText = { StatsScreenTopText(state) },
                actions = {
                    IconButton(
                        onClick = {
                        }
                    ) {
                    }
                }
            )
        }
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                viewModel.onEvent(TopScreenEvent.Refresh)
                        },
            modifier = Modifier.padding(top = innerPadding.calculateTopPadding(), bottom = 0.dp, start = 8.dp, end = 8.dp)
        ) {


        Row(modifier = Modifier.fillMaxWidth()) {
//            if (homeState.isLoading) {
//                Column(
//                    modifier = Modifier.fillMaxWidth(),
//                    verticalArrangement = Arrangement.Center,
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    CircularProgressIndicator(modifier = Modifier.size(48.dp))
//                }
//            }  else if (!viewModel.errorMessage.collectAsState().value.isNullOrEmpty()) {
//                Column(
//                    modifier = Modifier.fillMaxSize(),
//                    verticalArrangement = Arrangement.Center,
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Text(text = viewModel.errorMessage.collectAsState().value!!,
//                        color = MaterialTheme.colorScheme.onBackground,
//                        textAlign = TextAlign.Center)
//                }
//            } else {
                val numberFormat = NumberFormat.getInstance(Locale.US)

                val startDate: LocalDate = state.start?.toLocalDate()
                    ?: state.oldestStreamDate

                val endDate: LocalDate = state.end?.toLocalDate()
                        ?.takeIf { it.isBefore(LocalDate.now()) }
                    ?: LocalDate.now()

                val totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1

                val minutesPerDay = statsState.stats.minutes_streamed / totalDays
                val hoursStreamed = statsState.stats.minutes_streamed / 60f
                val hoursPerDay = (hoursStreamed / totalDays).toInt()
                val minutesMod = String.format("%02d", minutesPerDay % 60)
                val daysStreamed = statsState.stats.minutes_streamed / 1440f
                val percentageOfTime = (daysStreamed / totalDays) * 100

                val bottomPadding = if (state.dateRangeMode.isNullOrEmpty()) 0.dp else 48.dp

                LazyColumn(
                    modifier = Modifier.fillMaxWidth().padding(bottom = bottomPadding), verticalArrangement = Arrangement.Top
                ) {
                    item() {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 8.dp), horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "General",
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
                        ) {
                            Column (
                                modifier = Modifier.weight(1f),
                            ) {
                                GeneralStatsItem(
                                    dateRangeMode = state.dateRangeMode,
                                    itemValue = numberFormat.format(statsState.stats.stream_count),
                                    itemText = "Streams",
                                    perDayValue = (statsState.stats.stream_count / totalDays).toString() + " a day",
                                    color = vicoTheme.lineCartesianLayerColors[1]
                                )
                                GeneralStatsItem(
                                    dateRangeMode = state.dateRangeMode,
                                    itemValue = numberFormat.format(hoursStreamed.toInt()),
                                    itemText = "Hours",
                                    perDayValue = "$hoursPerDay:$minutesMod a day",
                                    color = Color(153, 76, 155)
                                )
                            }

                            Column (
                                modifier = Modifier.weight(1f),
                            ) {
                                GeneralStatsItem(
                                    dateRangeMode = state.dateRangeMode,
                                    itemValue = numberFormat.format(statsState.stats.minutes_streamed),
                                    itemText = "Minutes",
                                    perDayValue = (minutesPerDay).toString() + " a day",
                                    color = vicoTheme.lineCartesianLayerColors[0]

                                )
                                GeneralStatsItem(
                                    dateRangeMode = state.dateRangeMode,
                                    itemValue = String.format(
                                        "%.1f",
                                        daysStreamed
                                    ),
                                    itemText = "Days",
                                    perDayValue = String.format(
                                        "%.1f",
                                        percentageOfTime
                                    ) + "% of total time",
                                    color = Color(249, 45, 95)
                                )
                            }
                        }

                    }

                    item() {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 8.dp), horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Collection",
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
                        ) {
                            Column (
                                modifier = Modifier.weight(1f),
                            ) {

                                CollectionStatsItem(
                                    dateRangeMode = state.dateRangeMode,
                                    itemValue = numberFormat.format(statsState.stats.track_count),
                                    itemText = "Tracks"
                                )


                            }

                            Column (
                                modifier = Modifier.weight(1f),
                            ) {
                                CollectionStatsItem(
                                    dateRangeMode = state.dateRangeMode,
                                    itemValue = numberFormat.format(statsState.stats.album_count),
                                    itemText = "Albums"
                                )
                            }

                            Column (
                                modifier = Modifier.weight(1f),
                            ) {
                                CollectionStatsItem(
                                    dateRangeMode = state.dateRangeMode,
                                    itemValue = numberFormat.format(statsState.stats.artist_count),
                                    itemText = "Artists"
                                )
                            }
                        }

                    }

                    item() {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 8.dp), horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Graphs",
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp
                            )
                        }
                        if (statsSeries.isEmpty() || (statsSeries.size == 1 && statsSeries[0].minutes_streamed == 0)) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 8.dp), horizontalArrangement = Arrangement.Center
                            ) {
                                Text("no data to show")
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth()
                                    .padding(top = 16.dp, bottom = 8.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                StreamingSumChart(statsSeries)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth()
                                    .padding(top = 16.dp, bottom = 8.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                StreamingValuesChart(statsSeries)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth()
                                    .padding(top = 16.dp, bottom = 8.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                HourlyStatsChart(hourlyStats)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth()
                                    .padding(top = 16.dp, bottom = 8.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                AverageStreamLengthChart(statsSeries)
                            }
                        }
                    }
                }
            }
        }
        TopScreenBottomBar(
            state = state,
            onVMSearchParameterChange = { viewModel.onEvent(it)},
            onDateRangePageChange = { viewModel.onEvent(it) }
        )
    }
}