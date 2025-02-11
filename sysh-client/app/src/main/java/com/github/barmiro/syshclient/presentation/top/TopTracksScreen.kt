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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TopTracksScreen(
    viewModel: TopTracksViewModel
) {

    val state = viewModel.state
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                viewModel.onEvent(TopTracksEvent.Refresh)
            }
        ) {
            Text(text = "Load")
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