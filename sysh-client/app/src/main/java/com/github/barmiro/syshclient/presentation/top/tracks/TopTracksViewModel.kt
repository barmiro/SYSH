package com.github.barmiro.syshclient.presentation.top.tracks

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.github.barmiro.syshclient.data.top.TopRepository
import com.github.barmiro.syshclient.domain.top.TopTrack
import com.github.barmiro.syshclient.presentation.top.TopScreenEvent
import com.github.barmiro.syshclient.presentation.top.TopScreenState
import com.github.barmiro.syshclient.presentation.top.TopScreenStateManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TopTracksViewModel @Inject constructor(
    private val topRepository: TopRepository,
    private val stateManager: TopScreenStateManager
): ViewModel(), DefaultLifecycleObserver {

    val state: StateFlow<TopScreenState> = stateManager.state

    private val _tracks = MutableStateFlow<PagingData<TopTrack>>(PagingData.empty())
    val tracks: StateFlow<PagingData<TopTrack>> = _tracks

    private var isDataLoaded = false

    var previousValues by mutableStateOf(listOf(state.value.sort, state.value.start, state.value.end))

    override fun onCreate(owner: LifecycleOwner) {
        if (!isDataLoaded
            || listOf(state.value.sort, state.value.start, state.value.end) != previousValues) {
            getTopTracks()
            previousValues = listOf(state.value.sort, state.value.start, state.value.end)
        }
    }

    fun onEvent(event: TopScreenEvent) {
        when(event) {
            is TopScreenEvent.Refresh -> {
                getTopTracks()
            } else -> {}
        }
    }

    fun getTopTracks(
        start: String? = state.value.start,
        end: String? = state.value.end,
        sort: String? = state.value.sort
    ) {
        viewModelScope.launch {
            topRepository.getTopTracks(
                start,
                end,
                sort
            )
                .cachedIn(viewModelScope)
                .collect { pagedData ->
                    _tracks.value = pagedData
                    isDataLoaded = true
                }
        }
    }
}