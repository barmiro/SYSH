package com.github.barmiro.syshclient.presentation.top

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.barmiro.syshclient.data.top.TopRepository
import com.github.barmiro.syshclient.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TopTracksViewModel @Inject constructor(
    private val topRepository: TopRepository
): ViewModel() {

    var state by mutableStateOf(TopTracksState())

    fun onEvent(event: TopTracksEvent) {
        when(event) {
            is TopTracksEvent.Refresh -> {
                getTopTracks()
            }
            is TopTracksEvent.OnSearchParameterChange -> {
                state = state.copy(
                    start = event.start,
                    end = event.end,
                    sort = event.sort
                )
                viewModelScope.launch {
                    getTopTracks()
                }
            }
        }
    }

    private fun getTopTracks(
        start: String = state.start,
        end: String = state.end,
        sort: String = state.sort
    ) {
        viewModelScope.launch {
            topRepository.getTopTracks(start, end, sort)
                .collect { result ->
                    when(result) {
                        is Resource.Success -> {
                            result.data?.let { topTracks ->
                                state = state.copy(
                                    tracks = topTracks
                                )
                            }
                        }
                        is Resource.Error -> {

                        }
                        is Resource.Loading -> {
                            state = state.copy(isLoading = result.isLoading)
                        }
                    }
                }
        }
    }
}