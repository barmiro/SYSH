package com.github.barmiro.syshclient.presentation.stats

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.barmiro.syshclient.data.stats.StatsRepository
import com.github.barmiro.syshclient.presentation.home.HomeState
import com.github.barmiro.syshclient.presentation.top.TopScreenEvent
import com.github.barmiro.syshclient.presentation.top.TopScreenState
import com.github.barmiro.syshclient.presentation.top.TopScreenStateManager
import com.github.barmiro.syshclient.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val statsRepository: StatsRepository,
    private val stateManager: TopScreenStateManager
): ViewModel(), DefaultLifecycleObserver {

    val state: StateFlow<TopScreenState> = stateManager.state

    private var isDataLoaded = false

    private val _homeState = MutableStateFlow(HomeState())
    val homeState: StateFlow<HomeState> = _homeState

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _errorMessage: MutableStateFlow<String?> = MutableStateFlow(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    override fun onCreate(owner: LifecycleOwner) {
        getStats()
    }


    fun onEvent(event: TopScreenEvent) {
        when(event) {
            is TopScreenEvent.Refresh -> {
                getStats()
            }
            is TopScreenEvent.OnSearchParameterChange -> {
                stateManager.updateState(
                    start = event.start,
                    end = event.end,
                    sort = event.sort
                )
                getStats()
            }
            is TopScreenEvent.OnDateRangeModeChange -> {
                stateManager.updateState(
                    dateRangeMode = event.dateRangeMode
                )
            }
            is TopScreenEvent.OnDateRangePageChange -> {
                stateManager.updateState(
                    dateRangePageNumber = event.dateRangePage
                )
            }
        }
    }

    fun getStats(
        start: String? = state.value.start,
        end: String? = state.value.end
    ) {
        viewModelScope.launch {
            statsRepository.getStats(start, end)
                .collect { result ->
                    when(result) {
                        is Resource.Success -> {
                            result.data?.let { statsResult ->
                                _homeState.update {
                                    it.copy(stats = statsResult)
                                }
                            }
                        }
                        is Resource.Error -> {
                            println(result)
                            _errorMessage.value = result.message
                        }
                        is Resource.Loading -> {
                            _homeState.update {
                                it.copy(isLoading = result.isLoading)
                            }
                        }

                    }
                }
        }
    }


}