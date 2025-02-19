package com.github.barmiro.syshclient.presentation.top

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.github.barmiro.syshclient.presentation.top.albums.TopAlbumsScreen
import com.github.barmiro.syshclient.presentation.top.albums.TopAlbumsViewModel
import com.github.barmiro.syshclient.presentation.top.artists.TopArtistsScreen
import com.github.barmiro.syshclient.presentation.top.artists.TopArtistsViewModel
import com.github.barmiro.syshclient.presentation.top.tracks.TopTracksScreen
import com.github.barmiro.syshclient.presentation.top.tracks.TopTracksViewModel
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

    var tabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Tracks", "Albums", "Artists")
    val pagerState = rememberPagerState(pageCount = { 3 })

    LaunchedEffect(tabIndex) {
        pagerState.animateScrollToPage(page = tabIndex,
            animationSpec = spring(stiffness = 300f)
        )
    }

    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress) {
            tabIndex = pagerState.currentPage
        }
    }

    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 48.dp)) {
        Column(modifier = Modifier.fillMaxWidth()) {
            TabRow(selectedTabIndex = tabIndex,
                indicator = { tabPositions ->
                    TopIndicator(
                        tabPositions,
                        pagerState.getOffsetDistanceInPages(0))
                }) {
                tabs.forEachIndexed { index, title ->
                    Tab(text = { Text(title) },
                        selected = tabIndex == index,
                        onClick = { tabIndex = index }
                    )
                }
            }

            HorizontalPager(state = pagerState, modifier = Modifier.fillMaxWidth().weight(1f)) { page ->
                Box(Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    when (page) {
                        0 -> TopTracksScreen(topTracksVM)
                        1 -> TopAlbumsScreen(topAlbumsVM)
                        2 -> TopArtistsScreen(topArtistsVM)
                        else -> Text("Something went wrong with the pager")
                    }
                }
            }
        }

    }

    Box (modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
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

                    Button(
                        onClick = {
                            viewModel.onEvent(TopScreenEvent.OnSearchParameterChange(start = "", end = ""))
                            dateRange = null
                        }
                    ) {
                        Text(text = "$formattedStart - $formattedEnd")
                    }
                } else {
                    Button(
                        onClick = {
                            isDateRangePickerVisible = true
                        }
                    ) {
                        Text(text = "Select date range")
                    }
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


@Composable
fun TopIndicator(tabPositions: List<TabPosition>, animationOffset: Float) {
    val transition = updateTransition(0 - animationOffset)
    val leftIndicatorEdge by transition.animateDp(
        transitionSpec = {
            if (initialState < targetState) {
                spring(dampingRatio = 1f, stiffness = 60f)
            } else {
                spring(dampingRatio = 1f, stiffness = 1000f)
            }
        }
    ) {
        tabPositions[0].right * it
    }

    val rightIndicatorEdge by transition.animateDp(
        transitionSpec = {
            if (initialState < targetState) {
                spring(dampingRatio = 1f, stiffness = 1000f)
            } else {
                spring(dampingRatio = 1f, stiffness = 60f)
            }
        }
    ) {
        tabPositions[0].right * (it + 1)
    }

    Box(
        modifier = Modifier
            .wrapContentSize(align = Alignment.BottomStart)
            .offset(x = leftIndicatorEdge)
            .width(rightIndicatorEdge - leftIndicatorEdge)
            .padding(bottom = 0.dp)
            .fillMaxWidth()
            .height(2.dp)
            .background(MaterialTheme.colorScheme.secondary, RectangleShape)
    )
}