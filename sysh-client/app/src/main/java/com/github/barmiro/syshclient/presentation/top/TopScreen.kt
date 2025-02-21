package com.github.barmiro.syshclient.presentation.top

import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.github.barmiro.syshclient.R
import com.github.barmiro.syshclient.presentation.top.albums.TopAlbumsScreen
import com.github.barmiro.syshclient.presentation.top.albums.TopAlbumsViewModel
import com.github.barmiro.syshclient.presentation.top.artists.TopArtistsScreen
import com.github.barmiro.syshclient.presentation.top.artists.TopArtistsViewModel
import com.github.barmiro.syshclient.presentation.top.tracks.TopTracksScreen
import com.github.barmiro.syshclient.presentation.top.tracks.TopTracksViewModel
import com.github.barmiro.syshclient.util.setToEndOfDay
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.absoluteValue


@OptIn(ExperimentalMaterial3Api::class)
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
                if (it.first != null) {
                    val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                    dateRange = when {
                        it.second == null -> Pair(it.first, System.currentTimeMillis())
                        else -> it
                    }

                    val start = formatter.format(dateRange!!.first)
                    val end = formatter.format(setToEndOfDay(Date(dateRange!!.second!!)))
                    viewModel.onEvent((TopScreenEvent.OnSearchParameterChange(null, start, end)))
                }
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
            animationSpec = spring(stiffness = 500f)
        )
    }

    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress) {
            tabIndex = pagerState.currentPage
        }
    }
    var expanded by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                ),
                title = {
                    Text(text = "Top",
                        fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(Icons.Default.DateRange, contentDescription = "More options")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("All time") },
                            onClick = {
                                viewModel.onEvent(TopScreenEvent.OnSearchParameterChange(start = "", end = ""))
                                dateRange = null
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Yearly") },
                            onClick = {
                                val year: Int = LocalDate.now().year
                                val startDate = Calendar.getInstance().set(year, 0, 1)
                                val endDate:String =""
                                viewModel.onEvent(TopScreenEvent.OnSearchParameterChange(start = "${LocalDate.now().year}-01-01T00:00:00" , end = "${LocalDate.now().year}-12-31T23:59:59"))

                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Custom range") },
                            onClick = {
                                isDateRangePickerVisible = true
                                expanded = false
                            }
                        )
                    }

//                    if (dateRange != null) {
//                        IconButton(
//                            onClick = {
//                                viewModel.onEvent(TopScreenEvent.OnSearchParameterChange(start = "", end = ""))
//                                dateRange = null
//                            }
//                        ) {
//                            Icon(
//                                imageVector = Icons.Filled.DateRange,
//                                contentDescription = "Localized description"
//                            )
//                        }
//                    } else {
//                        IconButton(
//                            onClick = {
//                                isDateRangePickerVisible = true
//                            }
//                        ) {
//                            Icon(
//                                imageVector = Icons.Outlined.DateRange,
//                                contentDescription = "Localized description"
//                            )
//                        }
//                    }
                },
                actions = {
                    if (state.sort == "time") {
                        IconButton(
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
                            Icon(
                                painter = painterResource(id = R.drawable.sort_24px),
                                tint = IconButtonDefaults.iconButtonColors().contentColor,
                                contentDescription = "Sort icon"
                            )
                        }
                    } else {
                        IconButton(
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
                            Icon(
                                painter = painterResource(id = R.drawable.sort_24px),
                                tint = IconButtonDefaults.iconButtonColors().contentColor,
                                contentDescription = "Sort icon"
                            )
                        }
                    }
                }
            )
//            TODO: this is a terrible hack
            Box(
                modifier = Modifier.fillMaxWidth().height(88.dp), contentAlignment = Alignment.BottomCenter
            ) {
                var rangeText = "All time"
                if (dateRange != null) {
                    val start = Date(dateRange!!.first!!)
                    val end = Date(dateRange!!.second!!)

                    val formattedStart = SimpleDateFormat(
                        "MMM dd, yyyy",
                        Locale.getDefault()
                    ).format(start)

                    val formattedEnd = SimpleDateFormat(
                        "MMM dd, yyyy",
                        Locale.getDefault()
                    ).format(end)

                    rangeText = "$start - $end"
                }
                var sortText = "by stream count"
                if (state.sort == "time") {
                    sortText = "by listening time"
                }
                Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Bottom, horizontalAlignment = Alignment.CenterHorizontally) {
                    TopText(rangeText, sortText)
                }
            }
        }
    ) { innerPadding ->
        Row(modifier = Modifier.fillMaxWidth().padding(top = innerPadding.calculateTopPadding(), bottom = 0.dp)) {
            Column(modifier = Modifier.fillMaxWidth()) {
                TabRow(selectedTabIndex = tabIndex,
                    indicator = { tabPositions ->
                        TopIndicator(
                            tabPositions,
                            pagerState.getOffsetDistanceInPages(0))
                    }) {
                    tabs.forEachIndexed { index, title ->
                        CompositionLocalProvider(LocalRippleConfiguration provides null) {
                            Tab(text = { Text(
                                text = title,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.alpha(
                                    (1 - pagerState.getOffsetDistanceInPages(index).absoluteValue / 2).coerceAtLeast(0.5f)
                                )
                            ) },
                                selected = tabIndex == index,
                                onClick = { tabIndex = index }
                            )

                        }
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
                Surface(modifier = Modifier.fillMaxWidth().height(48.dp).background(MaterialTheme.colorScheme.background)) {
                    HorizontalDivider(thickness = 2.dp, modifier = Modifier.alpha(0.5f))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom
                    ) {

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
                spring(dampingRatio = 1f, stiffness = 200f)
            } else {
                spring(dampingRatio = 1f, stiffness = 2000f)
            }
        }
    ) {
        tabPositions[0].right * (it + 0.125f)
    }

    val rightIndicatorEdge by transition.animateDp(
        transitionSpec = {
            if (initialState < targetState) {
                spring(dampingRatio = 1f, stiffness = 2000f)
            } else {
                spring(dampingRatio = 1f, stiffness = 200f)
            }
        }
    ) {
        tabPositions[0].right * (it + 0.875f)
    }


    Box(
        modifier = Modifier
            .wrapContentSize(align = Alignment.BottomStart)
            .offset(x = leftIndicatorEdge)
            .width(rightIndicatorEdge - leftIndicatorEdge)
            .padding(8.dp)
            .fillMaxWidth()
            .height(64.dp)
            .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(32.dp))
            .zIndex(-1f)
    )
}

@Composable
fun TopText(rangeText: String, sortText: String) {
    var oldRangeText by remember { mutableStateOf("") }
    var oldSortText by remember { mutableStateOf("") }
    var targetRangeText by remember { mutableStateOf("") }
    var targetSortText by remember { mutableStateOf("") }
    val delayBase = 150L

    LaunchedEffect(rangeText) {
        targetRangeText = oldRangeText
        for(i in 1..targetRangeText.length) {
            targetRangeText = targetRangeText.dropLast(1)
            delay(delayBase / oldRangeText.length)
        }
        delay(delayBase)
        for(i in 1..rangeText.length) {
            targetRangeText += rangeText[i - 1]
            delay(delayBase / rangeText.length)
        }
        oldRangeText = rangeText
    }

    LaunchedEffect(sortText) {
        targetSortText = oldSortText
        for(i in 1..targetSortText.length) {
            targetSortText = targetSortText.dropLast(1)
            delay(delayBase / oldSortText.length)
        }
        delay(delayBase)
        for(i in 1..sortText.length) {
            targetSortText += sortText[i - 1]
            delay(delayBase / sortText.length)
        }
        oldSortText = sortText
    }

    Row (Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Text(text = targetRangeText, fontSize = 14.sp, lineHeight = 14.sp, maxLines = 1, modifier = Modifier.padding(0.dp)
            .animateContentSize(spring(1f, 3000f)))
        Text(text = " • ", fontSize = 14.sp, lineHeight = 14.sp, maxLines = 1, modifier = Modifier.padding(0.dp))
        Text(text = targetSortText, fontSize = 14.sp, lineHeight = 14.sp, maxLines = 1, modifier = Modifier.alpha(0.5f).padding(0.dp)
            .animateContentSize(spring(1f, 3000f)))

    }
}