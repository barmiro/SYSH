package com.github.barmiro.syshclient.presentation.top

import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.barmiro.syshclient.presentation.top.albums.TopAlbumsScreen
import com.github.barmiro.syshclient.presentation.top.albums.TopAlbumsViewModel
import com.github.barmiro.syshclient.presentation.top.artists.TopArtistsScreen
import com.github.barmiro.syshclient.presentation.top.artists.TopArtistsViewModel
import com.github.barmiro.syshclient.presentation.top.components.DateRangePickerModal
import com.github.barmiro.syshclient.presentation.top.components.TopScreenTopBar
import com.github.barmiro.syshclient.presentation.top.components.TopScreenTopIndicator
import com.github.barmiro.syshclient.presentation.top.components.TopScreenTopText
import com.github.barmiro.syshclient.presentation.top.tracks.TopTracksScreen
import com.github.barmiro.syshclient.presentation.top.tracks.TopTracksViewModel
import com.github.barmiro.syshclient.util.setToEndOfDay
import com.github.barmiro.syshclient.util.yearToDateRange
import com.github.barmiro.syshclient.util.yearToEnd
import com.github.barmiro.syshclient.util.yearToStart
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneOffset
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
    var dateRangeMode by remember { mutableStateOf("") }
    var isDateRangePickerVisible by remember { mutableStateOf(false) }

    var tabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Tracks", "Albums", "Artists")
    val tabPagerState = rememberPagerState(pageCount = { 3 })



    if (isDateRangePickerVisible) {
        DateRangePickerModal(
            onDateRangeSelected = {
                if (it.first != null) {
                    val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                    dateRange = when {
                        it.second == null -> Pair(
                            it.first,
//                            this mess is here for compatibility with Material 3's date range picker
//                            the server deals with converting the timezone
//                            I'll change it at some point because it will cause issues when moving between timezones
                            LocalDateTime.now()
                                .atOffset(ZoneOffset.UTC)
                                .toInstant()
                                .toEpochMilli())
                        else -> it
                    }

                    val start = formatter.format(dateRange!!.first)
                    val end = formatter.format(setToEndOfDay(dateRange!!.second!!))
                    viewModel.onEvent((TopScreenEvent.OnSearchParameterChange(null, start, end)))
                    dateRangeMode = "custom"
                }
            },
            onDismiss = {
                isDateRangePickerVisible = false
            }
        )
    }

    LaunchedEffect(tabIndex) {
        tabPagerState.animateScrollToPage(page = tabIndex,
            animationSpec = spring(stiffness = 500f)
        )
    }

    LaunchedEffect(tabPagerState.currentPage, tabPagerState.isScrollInProgress) {
        if (!tabPagerState.isScrollInProgress) {
            tabIndex = tabPagerState.currentPage
        }
    }

    Scaffold(
        topBar = {
            TopScreenTopBar(
                state = state,
                onDateRangeSelect = { dateRange = it },
                onDateRangeModeChange = { dateRangeMode = it },
                onDateRangePickerVisibilityChange = { isDateRangePickerVisible = it },
                onVMSearchParameterChange = { viewModel.onEvent(it) }
            )
//            TODO: this is a terrible hack
            Box(
                modifier = Modifier.fillMaxWidth().height(88.dp), contentAlignment = Alignment.BottomCenter
            ) {
                TopScreenTopText(state, dateRangeMode)
            }
        }
    ) { innerPadding ->
        Row(modifier = Modifier.fillMaxWidth().padding(top = innerPadding.calculateTopPadding(), bottom = 0.dp)) {
            Column(modifier = Modifier.fillMaxWidth()) {
                TabRow(selectedTabIndex = tabIndex,
                    indicator = { tabPositions ->
                        TopScreenTopIndicator(
                            tabPositions,
                            tabPagerState.getOffsetDistanceInPages(0))
                    }) {
                    tabs.forEachIndexed { index, title ->
                        CompositionLocalProvider(LocalRippleConfiguration provides null) {
                            Tab(text = { Text(
                                text = title,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.alpha(
                                    (1 - tabPagerState.getOffsetDistanceInPages(index).absoluteValue / 2).coerceAtLeast(0.5f)
                                )
                            ) },
                                selected = tabIndex == index,
                                onClick = { tabIndex = index }
                            )
                        }
                    }
                }

                HorizontalPager(
                    state = tabPagerState,
                    modifier = Modifier.fillMaxWidth().weight(1f)
                ) { page ->
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


        var bottomBarPageCount by remember { mutableIntStateOf(15)}
        var bottomBarPagerState = rememberPagerState(pageCount = { bottomBarPageCount }, initialPage = bottomBarPageCount - 1)
        var bottomBarTargetPage by remember { mutableIntStateOf(bottomBarPagerState.currentPage) }

        LaunchedEffect(bottomBarTargetPage) {
            bottomBarPagerState.animateScrollToPage(bottomBarTargetPage)
        }

        LaunchedEffect(bottomBarPagerState.currentPage) {
            if (dateRangeMode == "yearly") {
                val year = LocalDateTime.now().year - (bottomBarPageCount - bottomBarPagerState.currentPage - 1)
                dateRange = yearToDateRange(year)
                viewModel.onEvent(TopScreenEvent.OnSearchParameterChange(start = yearToStart(year) , end = yearToEnd(year)))
            }
        }

        if (dateRangeMode.isNotEmpty()) {
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
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.fillMaxWidth().weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                IconButton(
                                    onClick = {
                                        if (bottomBarPagerState.currentPage > 0) {
                                            bottomBarTargetPage = bottomBarPagerState.currentPage - 1
                                        }
                                    },
                                    enabled = when {
                                        bottomBarPagerState.currentPage == 0 -> false
                                        else -> true
                                    }
                                ) {
                                    Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                        tint = IconButtonDefaults.iconButtonColors().contentColor,
                                        contentDescription = "Left arrow"
                                    )
                                }
                            }
                            Column(modifier = Modifier.fillMaxWidth().weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                HorizontalPager(
                                    state = bottomBarPagerState,
                                    modifier = Modifier.fillMaxWidth()
                                ) { page ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly,
                                        verticalAlignment = Alignment.Bottom
                                    ) {
                                        Text((LocalDateTime.now().year - (bottomBarPageCount - page - 1)).toString())

                                    }
                                }
                            }
                            Column(modifier = Modifier.fillMaxWidth().weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                IconButton(
                                    onClick = {
                                        if (bottomBarPagerState.currentPage < bottomBarPageCount - 1) {
                                            bottomBarTargetPage = bottomBarPagerState.currentPage + 1
                                        }
                                    },
                                    enabled = when {
                                        bottomBarPagerState.currentPage == bottomBarPageCount - 1 -> false
                                        else -> true
                                    }
                                ) {
                                    Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                        tint = IconButtonDefaults.iconButtonColors().contentColor,
                                        contentDescription = "Right arrow"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


//                if (dateRange != null) {
//                    val formattedStart = SimpleDateFormat(
//                        "MMM dd, yyyy",
//                        Locale.getDefault()
//                    ).format(dateRange!!.first)
//
//                    val formattedEnd = SimpleDateFormat(
//                        "MMM dd, yyyy",
//                        Locale.getDefault()
//                    ).format(dateRange!!.second)
//
//                    rangeText = "$formattedStart - $formattedEnd"
//                }