package com.github.barmiro.syshclient.presentation.top.artists

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.barmiro.syshclient.data.top.TopRepository
import com.github.barmiro.syshclient.presentation.top.TopScreenEvent
import com.github.barmiro.syshclient.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TopArtistsViewModel @Inject constructor(
    private val topRepository: TopRepository
): ViewModel(), DefaultLifecycleObserver {

    var state by mutableStateOf(TopArtistsState())
    override fun onCreate(owner: LifecycleOwner) {
        if (state.artistList.isEmpty()) {
            getTopArtists()
        }
    }
    fun onEvent(event: TopScreenEvent) {
        when(event) {
            is TopScreenEvent.Refresh -> {
                getTopArtists()
            }
            is TopScreenEvent.OnSearchParameterChange -> {
                state = state.copy(
                    start = event.start,
                    end = event.end,
                    sort = event.sort
                )
                viewModelScope.launch {
                    getTopArtists()
                }
            }
        }
    }

    private fun getTopArtists(
        start: String? = state.start,
        end: String? = state.end,
        sort: String? = state.sort
    ) {
        viewModelScope.launch {
            topRepository.getTopArtists(start, end, sort)
                .collect { result ->
                    when(result) {
                        is Resource.Success -> {
                            result.data?.let { topArtists ->
                                state = state.copy(
                                    artistList = topArtists
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