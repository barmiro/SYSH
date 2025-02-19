package com.github.barmiro.syshclient.presentation.top.artists

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.github.barmiro.syshclient.presentation.top.TopScreenEvent

@Composable
fun TopArtistsScreen(
    viewModel: TopArtistsViewModel
) {
    val state by viewModel.state.collectAsState()
    viewModel.observeLifecycle(LocalLifecycleOwner.current.lifecycle)

    var previousValues by remember { mutableStateOf(listOf(state.sort, state.start, state.end)) }
    LaunchedEffect(state.sort, state.start, state.end) {
        if (listOf(state.sort, state.start, state.end) != previousValues) {
            viewModel.onEvent(TopScreenEvent.Refresh)
        }
        previousValues = listOf(state.sort, state.start, state.end)
    }

    if (state.isLoading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Loading...",
                color = MaterialTheme.colorScheme.onBackground )
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(viewModel.artistList.size) { i ->
                    val artist = viewModel.artistList[i]
                    ArtistItem(
                        index = i + 1,
                        artist = artist,
                        sort = state.sort,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                //TODO
                            }
                            .padding(12.dp)
                    )
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