package com.github.barmiro.syshclient.presentation.top

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
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

@Composable
fun TopScreen(
    topTracksVM: TopTracksViewModel,
    topAlbumsVM: TopAlbumsViewModel,
    topArtistsVM: TopArtistsViewModel
) {

    val pagerState = rememberPagerState(pageCount = { 3 })
    //                                    not using lifecycleScope because the animations need a MonotonicFrameClock
    val coroutineScope = rememberCoroutineScope()

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
        Column(modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom) {

            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Bottom) {
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