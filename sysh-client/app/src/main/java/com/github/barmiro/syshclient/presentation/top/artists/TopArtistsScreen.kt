package com.github.barmiro.syshclient.presentation.top.artists

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.github.barmiro.syshclient.presentation.top.TopScreenEvent

@Composable
fun TopArtistsScreen(
    viewModel: TopArtistsViewModel
) {
    val state by viewModel.state.collectAsState()
    viewModel.observeLifecycle(LocalLifecycleOwner.current.lifecycle)

    val artists = viewModel.artists.collectAsLazyPagingItems()

    var previousValues by remember { mutableStateOf(listOf(state.sort, state.start, state.end)) }
    LaunchedEffect(state.sort, state.start, state.end) {
        if (listOf(state.sort, state.start, state.end) != previousValues) {
            viewModel.onEvent(TopScreenEvent.Refresh)
        }
        previousValues = listOf(state.sort, state.start, state.end)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (val loadState = artists.loadState.refresh) {
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
                    items(artists.itemCount) { i ->
                        artists[i]?.let { artist ->
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