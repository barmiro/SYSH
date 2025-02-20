package com.github.barmiro.syshclient.presentation.top.albums

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.barmiro.syshclient.data.top.TopRepository
import com.github.barmiro.syshclient.domain.top.TopAlbum
import com.github.barmiro.syshclient.presentation.top.TopScreenEvent
import com.github.barmiro.syshclient.presentation.top.TopScreenState
import com.github.barmiro.syshclient.presentation.top.TopScreenStateManager
import com.github.barmiro.syshclient.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TopAlbumsViewModel @Inject constructor(
    private val topRepository: TopRepository,
    private val stateManager: TopScreenStateManager
): ViewModel(), DefaultLifecycleObserver {

    val state: StateFlow<TopScreenState> = stateManager.state
    var albumList by mutableStateOf<List<TopAlbum>>(emptyList())
    private var isDataLoaded = false

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> get() = _isLoading
    override fun onCreate(owner: LifecycleOwner) {
        if (albumList.isEmpty() && !isDataLoaded) {
            getTopAlbums()
        }
    }
    fun onEvent(event: TopScreenEvent) {
        when(event) {
            is TopScreenEvent.Refresh -> {
                getTopAlbums()
            }
            is TopScreenEvent.OnSearchParameterChange -> {
                stateManager.updateState(
                    start = event.start,
                    end = event.end,
                    sort = event.sort
                )

                    getTopAlbums()

            }
        }
    }

    private fun getTopAlbums(
        start: String? = state.value.start,
        end: String? = state.value.end,
        sort: String? = state.value.sort
    ) {
        viewModelScope.launch {
            topRepository.getTopAlbums(start, end, sort)
                .collect { result ->
                    when(result) {
                        is Resource.Success -> {
                            result.data?.let { topAlbums ->
                                albumList = topAlbums
                                isDataLoaded = true
                            }
                        }
                        is Resource.Error -> {

                        }
                        is Resource.Loading -> {
                            _isLoading.value = result.isLoading

                        }

                    }
                }
        }
    }
}