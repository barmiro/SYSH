package com.github.barmiro.syshclient.presentation.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.barmiro.syshclient.R
import com.github.barmiro.syshclient.presentation.startup.UrlInfoItem
import com.github.barmiro.syshclient.presentation.top.components.HomeScreenTopBar
import com.github.barmiro.syshclient.presentation.top.components.HomeScreenTopText
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

@SuppressLint("DefaultLocale")
@Composable
fun HomeScreen(
    viewModel: HomeViewModel
) {

    val state by viewModel.homeState.collectAsState()
    val showImportAlert by viewModel.showImportAlert.collectAsState()
    val isDemoVersion by viewModel.isDemoVersion.collectAsState()

    Scaffold(
        topBar = {
            HomeScreenTopBar(
                titleText = "Home",
                animatedText = { HomeScreenTopText(viewModel.userDisplayName.collectAsState()) }
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
            val fractionPassedWrappedLower = daysPassed / (totalDays - 45)
            val fractionPassedWrappedUpper = daysPassed / (totalDays - 30)

            require(fractionPassedYear > 0.0
                    && fractionPassedWrappedLower > 0.0
                    && fractionPassedWrappedUpper > 0.0) { }

            val projectedMinutesYear: Int = (state.stats.minutes_streamed / fractionPassedYear).toInt()
            val projectedMinutesWrappedLower: Int = ((
                    state.stats.minutes_streamed / fractionPassedWrappedLower
                    ).toInt() / 100) * 100

            val projectedMinutesWrappedUpper: Int = ((
                    state.stats.minutes_streamed / fractionPassedWrappedUpper
                    ).toInt() / 100) * 100

            val numberFormat = NumberFormat.getInstance(Locale.US)
            val dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd")

            if (state.isLoading) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(48.dp))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    if (showImportAlert) {
                        item() {
                            UrlInfoItem(
                                icon = {
                                    Icon(imageVector = Icons.Default.Info,
                                        contentDescription = "Info")
                                },
                                text = "For the best experience, please import streaming data in the Settings tab"
                            )
                        }
                    }
                    if (isDemoVersion == true) {
                        item() {
                            UrlInfoItem(
                                icon = {
                                    Icon(imageVector = Icons.Default.Info,
                                        contentDescription = "Info")
                                },
                                text = "You're now connected to a demo server, serving generated sample data. Feel free to look around and explore all features of SYSH. When you're ready to set up your own account and start using SYSH, you can exit the demo mode in the Settings tab"
                            )
                        }
                    }
                    item() {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = LocalDate.now().format(dateFormatter),
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Column (
                                modifier = Modifier.weight(1f),
                            ) {
                                val streamsString = numberFormat.format(state.stats.day_streams)
                                val averageStreams = state.stats.stream_count / daysPassed
//                            this obviously shouldn't happen, but just in case
                                val streamsPercent: Int? = if (averageStreams == 0.0) null else {
                                    (state.stats.day_streams / averageStreams * 100).toInt()
                                }
                                HomeDayItem(
                                    itemText = "Streams",
                                    itemValue = streamsString,
                                    itemPercent = streamsPercent
                                )
                            }
                            Column (
                                modifier = Modifier.weight(1f),
                            ) {
                                val minutesString = numberFormat.format(state.stats.day_minutes)
                                val averageMinutes = state.stats.minutes_streamed / daysPassed
//                            this obviously shouldn't happen, but just in case
                                val minutesPercent: Int? = if (averageMinutes == 0.0) null else {
                                    (state.stats.day_minutes / averageMinutes * 100).toInt()
                                }
                                HomeDayItem(
                                    itemText = "Minutes",
                                    itemValue = minutesString,
                                    itemPercent = minutesPercent
                                )
                            }


                        }
                    }


                    item() {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.Center
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
                        }
                    }
                    item() {
                        Row() {
                            HomeTopItem(itemLabel = "Top Track",
                                itemName = state.stats.top_track?.name ?: "No tracks found",
                                imageUrl = state.stats.top_track?.image_url,
                                streamCount = state.stats.top_track?.stream_count,
                                minutesStreamed = state.stats.top_track?.total_ms_played?.div(60000),
                                placeholderID = R.drawable.music_note_24dp,
                                albumName = state.stats.top_track?.album_name,
                                artistName = state.stats.top_track?.primary_artist_name
                            )
                        }
                        Row() {
                            HomeTopItem(itemLabel = "Top Album",
                                itemName = state.stats.top_album?.name ?: "No albums found",
                                imageUrl = state.stats.top_album?.image_url,
                                streamCount = state.stats.top_album?.stream_count,
                                minutesStreamed = state.stats.top_album?.total_ms_played?.div(60000),
                                placeholderID = R.drawable.album_24dp,
                                artistName = state.stats.top_album?.primary_artist_name
                            )
                        }
                        Row() {
                            HomeTopItem(itemLabel = "Top Artist",
                                itemName = state.stats.top_artist?.name ?: "No artists found",
                                imageUrl = state.stats.top_artist?.image_url,
                                streamCount = state.stats.top_artist?.stream_count,
                                minutesStreamed = state.stats.top_artist?.total_ms_played?.div(60000),
                                placeholderID = R.drawable.artist_24dp
                            )
                        }
                    }
                    item() {
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
                                HomeItem(itemText = "Minutes on Spotify Wrapped",
                                    itemValue = (numberFormat.format(projectedMinutesWrappedLower)
                                            + " - "
                                            + numberFormat.format(projectedMinutesWrappedUpper)))
                            }
                        }
                    }
                    item() {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

            }
        }
    }
}