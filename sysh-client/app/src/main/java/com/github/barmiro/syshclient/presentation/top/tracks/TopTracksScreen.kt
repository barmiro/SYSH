package com.github.barmiro.syshclient.presentation.top.tracks

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.github.barmiro.syshclient.presentation.top.TopScreenEvent
import com.github.barmiro.syshclient.util.tintFromColor

@Composable
fun TopTracksScreen(
    viewModel: TopTracksViewModel
) {
    val state by viewModel.state.collectAsState()
    viewModel.observeLifecycle(LocalLifecycleOwner.current.lifecycle)

    val tracks = viewModel.tracks.collectAsLazyPagingItems()
    val dominantColors = remember { mutableStateMapOf<Int, Color>() }


    LaunchedEffect(state.sort, state.start, state.end) {
        val newValues = listOf(state.sort, state.start, state.end)
        if (newValues != viewModel.previousValues) {
            viewModel.onEvent(TopScreenEvent.Refresh)
            viewModel.previousValues = newValues
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (val loadState = tracks.loadState.refresh) {
            is LoadState.Loading -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(48.dp))
                }
            }
            is LoadState.Error -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = loadState.error.message ?: "Encountered unknown error",
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center)
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(tracks.itemCount) { i ->
                        tracks[i]?.let { track ->
                            TrackItem(
                                index = i + 1,
                                track = track,
                                sort = state.sort,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        //TODO
                                    }
                                    .padding(vertical = 12.dp, horizontal = 10.dp),
                                onColorExtracted = { color: Color ->
                                    if (dominantColors[i] != color) {
                                        dominantColors[i] = color
                                    }
                                },
                                startColor = dominantColors[i] ?: tintFromColor(Color.Gray)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun <T: LifecycleObserver> T.observeLifecycle(lifecycle: Lifecycle) {
    DisposableEffect(lifecycle) {
        lifecycle.addObserver(this@observeLifecycle)
        onDispose {
            lifecycle.removeObserver(this@observeLifecycle)
        }
    }
}