package com.github.barmiro.syshclient.presentation.stats

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.barmiro.syshclient.presentation.top.TopScreenEvent
import com.github.barmiro.syshclient.presentation.top.components.StatsScreenTopText
import com.github.barmiro.syshclient.presentation.top.components.TopScreenBottomBar
import com.github.barmiro.syshclient.presentation.top.components.TopScreenTopBar
import com.github.barmiro.syshclient.util.localDateFromTimestampString
import java.text.NumberFormat
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Locale

@SuppressLint("DefaultLocale")
@Composable
fun StatsScreen(
    viewModel: StatsViewModel
) {

    val homeState by viewModel.homeState.collectAsState()
    val state by viewModel.state.collectAsState()

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
                StatsScreenTopText(state)
            }
        }
    ) { innerPadding ->
        Row(modifier = Modifier.fillMaxWidth().padding(top = innerPadding.calculateTopPadding(), bottom = 0.dp, start = 8.dp, end = 8.dp)) {
            if (homeState.isLoading) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Loading...",
                        color = MaterialTheme.colorScheme.onBackground )
                }
            }  else if (!viewModel.errorMessage.collectAsState().value.isNullOrEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = viewModel.errorMessage.collectAsState().value!!,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center)
                }
            } else {
                val numberFormat = NumberFormat.getInstance(Locale.US)

                val startDate: LocalDate = localDateFromTimestampString(state.start)
                    ?: state.oldestStreamDate

                val endDate: LocalDate = localDateFromTimestampString(state.end)
                        ?.takeIf { it.isBefore(LocalDate.now()) }
                    ?: LocalDate.now()

                val totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1

                val minutesPerDay = homeState.stats.minutes_streamed / totalDays
                val hoursStreamed = homeState.stats.minutes_streamed / 60f
                val hoursPerDay = (hoursStreamed / totalDays).toInt()
                val minutesMod = String.format("%02d", minutesPerDay % 60)
                val daysStreamed = homeState.stats.minutes_streamed / 1440f
                val percentageOfTime = (daysStreamed / totalDays) * 100

                Column (
                    modifier = Modifier.weight(1f),
                ) {
                    StatsItem(
                        dateRangeMode = state.dateRangeMode,
                        itemValue = numberFormat.format(homeState.stats.stream_count),
                        itemText = "Streams",
                        perDayValue = (homeState.stats.stream_count / totalDays).toString()  + " a day"
                    )
                    StatsItem(
                        dateRangeMode = state.dateRangeMode,
                        itemValue = numberFormat.format(hoursStreamed.toInt()),
                        itemText =  "Hours",
                        perDayValue = "$hoursPerDay:$minutesMod a day"
                    )

                    StatsItem(
                        dateRangeMode = state.dateRangeMode,
                        itemValue = numberFormat.format(homeState.stats.track_count),
                        itemText = "Tracks"
                    )

                    StatsItem(
                        dateRangeMode = state.dateRangeMode,
                        itemValue = numberFormat.format(homeState.stats.artist_count),
                        itemText = "Artists"
                    )
                }
                Column (
                    modifier = Modifier.weight(1f),
                ) {
                    StatsItem(
                        dateRangeMode = state.dateRangeMode,
                        itemValue = numberFormat.format(homeState.stats.minutes_streamed),
                        itemText = "Minutes",
                        perDayValue = (minutesPerDay).toString() + " a day"
                    )
                    StatsItem(
                        dateRangeMode = state.dateRangeMode,
                        itemValue = String.format(
                            "%.1f",
                            daysStreamed
                        ),
                        itemText = "Days",
                        perDayValue = String.format("%.1f", percentageOfTime) + "% of total time"
                    )

                    StatsItem(
                        dateRangeMode = state.dateRangeMode,
                        itemValue = numberFormat.format(homeState.stats.album_count),
                        itemText = "Albums"
                    )
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



//if (state.dateRangeMode == "yearly") {
//    val today = LocalDate.now()
//    val startOfYear = today.withDayOfYear(1)
//    val endOfYear = today.withDayOfYear(today.lengthOfYear())
//    val daysPassed = ChronoUnit.DAYS.between(startOfYear, today).toDouble() + 1
//    val totalDays = ChronoUnit.DAYS.between(startOfYear, endOfYear).toDouble()
//
//    val fractionPassed = daysPassed / totalDays
//    require(fractionPassed != 0.0) { }
//    val projectedMinutes: Int = (homeState.stats.minutes_streamed / fractionPassed).toInt()
//
//    StatsItem(itemText = "On track to reach $projectedMinutes minutes this year"
//
//    )
//}