package com.github.barmiro.syshclient.presentation.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.barmiro.syshclient.presentation.top.components.HomeScreenTopBar
import com.github.barmiro.syshclient.presentation.top.components.HomeScreenTopText
import java.text.NumberFormat
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Locale

@SuppressLint("DefaultLocale")
@Composable
fun HomeScreen(
    viewModel: HomeViewModel
) {

    val state by viewModel.homeState.collectAsState()

    Scaffold(
        topBar = {
            HomeScreenTopBar(
                state = state,
                titleText = "Home",
                animatedText = { HomeScreenTopText(viewModel.userDisplayName.collectAsState()) },
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
        Row(
            modifier = Modifier.fillMaxWidth().padding(
                top = innerPadding.calculateTopPadding(),
                bottom = 0.dp,
                start = 8.dp,
                end = 8.dp
            )
        ) {
            val today = LocalDate.now()
            val startOfYear = today.withDayOfYear(1)
            val endOfYear = today.withDayOfYear(today.lengthOfYear())
            val daysPassed = ChronoUnit.DAYS.between(startOfYear, today).toDouble() + 1
            val totalDays = ChronoUnit.DAYS.between(startOfYear, endOfYear).toDouble()

            val fractionPassedYear = daysPassed / totalDays
            require(fractionPassedYear != 0.0) { }
            val projectedMinutesYear: Int = (state.stats.minutes_streamed / fractionPassedYear).toInt()

            val fractionPassedWrapped = daysPassed / (totalDays - 45)
            require(fractionPassedWrapped != 0.0) { }
            val projectedMinutesWrapped: Int = (state.stats.minutes_streamed / fractionPassedWrapped).toInt()

            val numberFormat = NumberFormat.getInstance(Locale.US)

            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 8.dp), horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "${today.year} at a glance",
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
                        HomeItem(itemText = "Streams",
                            itemValue = numberFormat.format(state.stats.stream_count))
                    }
                    Column (
                        modifier = Modifier.weight(1f),
                    ) {
                        HomeItem(itemText = "Minutes",
                            itemValue = numberFormat.format(state.stats.minutes_streamed))
                    }
                    Column (
                        modifier = Modifier.weight(1f),
                    ) {
                        HomeItem(itemText = "Hours",
                            itemValue = numberFormat.format(state.stats.minutes_streamed / 60))
                    }




//                    HomeItem(
//                        itemText = "${
//                            String.format(
//                                "%.1f",
//                                state.stats.minutes_streamed / 1440f
//                            )
//                        } days"
//                    )
//                    HomeItem(itemText = "${state.stats.track_count} tracks")
//                    HomeItem(itemText = "${state.stats.album_count} albums")
//                    HomeItem(itemText = "${state.stats.artist_count} artists")

                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 8.dp), horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Projections",
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
                        HomeItem(itemText = "Minutes by end of year",
                            itemValue = numberFormat.format(projectedMinutesYear))
                        HomeItem(itemText = "Minutes by Spotify Wrapped",
                            itemValue = numberFormat.format(projectedMinutesWrapped))
                    }
                }



            }
//            Column(
//                modifier = Modifier.fillMaxSize().padding(16.dp),
//            ) {
//                HomeItem(itemText = "Welcome, " + viewModel.userDisplayName.collectAsState().value)
//                HomeItem(itemText = "${state.stats.stream_count} streams")
//                HomeItem(itemText = "${state.stats.minutes_streamed} minutes")
//                HomeItem(itemText = "${state.stats.minutes_streamed / 60} hours")
//                HomeItem(
//                    itemText = "${
//                        String.format(
//                            "%.1f",
//                            state.stats.minutes_streamed / 1440f
//                        )
//                    } days"
//                )
//                HomeItem(itemText = "${state.stats.track_count} tracks")
//                HomeItem(itemText = "${state.stats.album_count} albums")
//                HomeItem(itemText = "${state.stats.artist_count} artists")
//            }
        }
    }
}