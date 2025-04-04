package com.github.barmiro.syshclient.presentation.top.albums

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import java.time.LocalDateTime


@Composable
fun TopAlbumsScreen(
    viewModel: TopAlbumsViewModel
) {
    val state by viewModel.state.collectAsState()
    viewModel.observeLifecycle(LocalLifecycleOwner.current.lifecycle)

    val albums = viewModel.albums.collectAsLazyPagingItems()


    var previousValues by remember { mutableStateOf(listOf("", LocalDateTime.MIN, LocalDateTime.MAX)) }
    LaunchedEffect(state.sort, state.start, state.end) {
        val newValues = listOf(state.sort, state.start, state.end)
        if (newValues != previousValues) {
            viewModel.onEvent(TopScreenEvent.Refresh)
            previousValues = newValues
        }
    }


    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
            when (val loadState = albums.loadState.refresh) {
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
                        Text(
                            text = loadState.error.message ?: "Encountered unknown error",
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(albums.itemCount) { i ->
                            albums[i]?.let { album ->
                                AlbumItem(
                                    index = i + 1,
                                    album = album,
                                    sort = state.sort,
                                    modifier = Modifier
                                        .fillMaxWidth()
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
