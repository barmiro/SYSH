package com.github.barmiro.syshclient.presentation.top

import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.barmiro.syshclient.R
import com.github.barmiro.syshclient.presentation.top.albums.TopAlbumsScreen
import com.github.barmiro.syshclient.presentation.top.albums.TopAlbumsViewModel
import com.github.barmiro.syshclient.presentation.top.artists.TopArtistsScreen
import com.github.barmiro.syshclient.presentation.top.artists.TopArtistsViewModel
import com.github.barmiro.syshclient.presentation.top.components.DateRangePickerModal
import com.github.barmiro.syshclient.presentation.top.components.TopScreenBottomBar
import com.github.barmiro.syshclient.presentation.top.components.TopScreenTopBar
import com.github.barmiro.syshclient.presentation.top.components.TopScreenTopIndicator
import com.github.barmiro.syshclient.presentation.top.components.TopScreenTopText
import com.github.barmiro.syshclient.presentation.top.tracks.TopTracksScreen
import com.github.barmiro.syshclient.presentation.top.tracks.TopTracksViewModel
import kotlin.math.absoluteValue


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopScreen(
    viewModel: TopScreenViewModel,
    topTracksVM: TopTracksViewModel,
    topAlbumsVM: TopAlbumsViewModel,
    topArtistsVM: TopArtistsViewModel,
    isGradientEnabled: Boolean
) {

    val state by viewModel.state.collectAsState()

    var isDateRangePickerVisible by remember { mutableStateOf(false) }

    var tabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Tracks", "Albums", "Artists")
    val tabPagerState = rememberPagerState(pageCount = { 3 })

    if (isDateRangePickerVisible) {
        DateRangePickerModal(
            onVMSearchParameterChange = { viewModel.onEvent(it) },
            onDateRangeModeChange = { viewModel.onEvent(it) },
            onDismiss = {
                isDateRangePickerVisible = false
            },
            oldestStreamDate = state.oldestStreamDate
        )
    }



//    workaround for the refresh indicator not disappearing
    var isRefreshing by remember { mutableStateOf(false) }
    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            isRefreshing = false
        }
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
                onDateRangeModeChange = { viewModel.onEvent(TopScreenEvent.OnDateRangeModeChange(it)) },
                onDateRangePickerVisibilityChange = { isDateRangePickerVisible = it },
                onVMSearchParameterChange = { viewModel.onEvent(it) },
                onDateRangePageChange = {viewModel.onEvent(it) },
                titleText = "Top",
                animatedText = { TopScreenTopText(state) },
                actions = {
                    if (state.sort == "time") {
                        IconButton(
                            onClick = {
                                viewModel.onEvent(TopScreenEvent.OnSearchParameterChange("count"))
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
                                viewModel.onEvent(TopScreenEvent.OnSearchParameterChange("time"))
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
        }
    ) { innerPadding ->

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                topAlbumsVM.onEvent(TopScreenEvent.Refresh)
                topTracksVM.onEvent(TopScreenEvent.Refresh)
                topArtistsVM.onEvent(TopScreenEvent.Refresh)
            },
            modifier = Modifier.padding(top = innerPadding.calculateTopPadding(), bottom = 0.dp)
        ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                TabRow(selectedTabIndex = tabIndex,
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground,
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
                                    (1 - tabPagerState.getOffsetDistanceInPages(index)
                                        .absoluteValue / 2)
                                        .coerceAtLeast(0.5f)
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
                            0 -> TopTracksScreen(topTracksVM, isGradientEnabled)
                            1 -> TopAlbumsScreen(topAlbumsVM, isGradientEnabled)
                            2 -> TopArtistsScreen(topArtistsVM, isGradientEnabled)
                            else -> Text("Something went wrong with the pager")
                        }
                    }
                }
            }
        }
        }
        TopScreenBottomBar(
            state = state,
            onVMSearchParameterChange = { viewModel.onEvent(it) },
            onDateRangePageChange = { viewModel.onEvent(it) }
        )
    }
}