package com.github.barmiro.syshclient.presentation.top.albums

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
class TopAlbumsViewModel @Inject constructor(
    private val topRepository: TopRepository
): ViewModel(), DefaultLifecycleObserver {

    override fun onCreate(owner: LifecycleOwner) {
        getTopAlbums()
    }
    var state by mutableStateOf(TopAlbumsState())
    fun onEvent(event: TopScreenEvent) {
        when(event) {
            is TopScreenEvent.Refresh -> {
                getTopAlbums()
            }
            is TopScreenEvent.OnSearchParameterChange -> {
                state = state.copy(
                    start = event.start,
                    end = event.end,
                    sort = event.sort
                )
                viewModelScope.launch {
                    getTopAlbums()
                }
            }
        }
    }

    private fun getTopAlbums(
        start: String? = state.start,
        end: String? = state.end,
        sort: String? = state.sort
    ) {
        viewModelScope.launch {
            topRepository.getTopAlbums(start, end, sort)
                .collect { result ->
                    when(result) {
                        is Resource.Success -> {
                            result.data?.let { topAlbums ->
                                state = state.copy(
                                    albumList = topAlbums
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