package com.github.barmiro.syshclient.presentation.top

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun TopTracksScreen(
    viewModel: TopTracksViewModel
) {
    val state = viewModel.state
    viewModel.observeLifecycle(LocalLifecycleOwner.current.lifecycle)
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                viewModel.onEvent(TopTracksEvent.OnSearchParameterChange("time"))
            }
        ) {
            Text(text = "Sort by time")
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(state.tracks.size) { i ->
                val track = state.tracks[i]
                TrackItem(
                    index = i + 1,
                    track = track,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            //TODO
                        }
                        .padding(16.dp)
                )
                if (i < state.tracks.size) {
                    HorizontalDivider(
                        modifier = Modifier.padding(
                            horizontal = 16.dp
                        ))
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