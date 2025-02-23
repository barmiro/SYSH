package com.github.barmiro.syshclient.presentation.top.tracks

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.barmiro.syshclient.data.top.TopRepository
import com.github.barmiro.syshclient.domain.top.TopTrack
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
class TopTracksViewModel @Inject constructor(
    private val topRepository: TopRepository,
    private val stateManager: TopScreenStateManager
): ViewModel(), DefaultLifecycleObserver {

    val state: StateFlow<TopScreenState> = stateManager.state
    var trackList by mutableStateOf<List<TopTrack>>(emptyList())
    private var isDataLoaded = false

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _errorMessage: MutableStateFlow<String?> = MutableStateFlow(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    var previousValues by mutableStateOf(listOf(state.value.sort, state.value.start, state.value.end))

    override fun onCreate(owner: LifecycleOwner) {
        if ((trackList.isEmpty() && !isDataLoaded)
            || listOf(state.value.sort, state.value.start, state.value.end) != previousValues) {
            getTopTracks()
            previousValues = listOf(state.value.sort, state.value.start, state.value.end)
        }
    }

    fun onEvent(event: TopScreenEvent) {
        when(event) {
            is TopScreenEvent.Refresh -> {
                getTopTracks()
            }
            is TopScreenEvent.OnSearchParameterChange -> {
                stateManager.updateState(
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

    fun getTopTracks(
        start: String? = state.value.start,
        end: String? = state.value.end,
        sort: String? = state.value.sort
    ) {
        viewModelScope.launch {
            topRepository.getTopTracks(start, end, sort)
                .collect { result ->
                    when(result) {
                        is Resource.Success -> {
                            result.data?.let { topTracks ->
                                _errorMessage.value = null
                                trackList = topTracks
                                isDataLoaded = true
                            }
                        }
                        is Resource.Error -> {
                            _errorMessage.value = result.message
                        }
                        is Resource.Loading -> {
                            _isLoading.value = result.isLoading
                        }
                    }
                }
        }
    }
}