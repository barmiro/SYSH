package com.github.barmiro.syshclient.presentation.stats

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.barmiro.syshclient.data.stats.HourlyStatsDTO
import com.github.barmiro.syshclient.data.stats.StatsRepository
import com.github.barmiro.syshclient.data.stats.StatsSeriesChunkDTO
import com.github.barmiro.syshclient.presentation.top.TopScreenEvent
import com.github.barmiro.syshclient.presentation.top.TopScreenState
import com.github.barmiro.syshclient.presentation.top.TopScreenStateManager
import com.github.barmiro.syshclient.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val statsRepository: StatsRepository,
    private val stateManager: TopScreenStateManager
): ViewModel(), DefaultLifecycleObserver {

    val state: StateFlow<TopScreenState> = stateManager.state

//    TODO: refactor
    private val _statsState = MutableStateFlow(StatsState())
    val statsState: StateFlow<StatsState> = _statsState

    private val _statsSeries = MutableStateFlow<List<StatsSeriesChunkDTO>>(emptyList())
    val statsSeries: StateFlow<List<StatsSeriesChunkDTO>> = _statsSeries

    private val _hourlyStats = MutableStateFlow<List<HourlyStatsDTO>>(emptyList())
    val hourlyStats: StateFlow<List<HourlyStatsDTO>> = _hourlyStats

    private val _errorMessage: MutableStateFlow<String?> = MutableStateFlow(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    fun onEvent(event: TopScreenEvent) {
        when(event) {
            is TopScreenEvent.Refresh -> {
                getStatsSeries()
                getStats()
                getHourlyStats()
            }
            is TopScreenEvent.OnSearchParameterChange -> {
                stateManager.updateState(
                    start = event.start,
                    end = event.end,
                    sort = event.sort,
                    dateRangeMode = event.dateRangeMode,
                    dateRangePageNumber = event.dateRangePage
                )
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
        start: LocalDateTime? = state.value.start,
        end: LocalDateTime? = state.value.end,
        mode: String? = state.value.dateRangeMode,
        year: Int? = state.value.start?.year
    ) {
        viewModelScope.launch {
            statsRepository.getStats(start, end, mode, year)
                .collect { result ->
                    when(result) {
                        is Resource.Success -> {
                            result.data?.let { statsResult ->
                                _statsState.update {
                                    it.copy(stats = statsResult)
                                }
                            }
                        }
                        is Resource.Error -> {
                            _errorMessage.value = result.message
                        }
                        is Resource.Loading -> {
                            _statsState.update {
                                it.copy(isLoading = result.isLoading)
                            }
                        }

                    }
                }
        }
    }

    fun getStatsSeries(
        start: LocalDateTime? = state.value.start,
        end: LocalDateTime? = state.value.end,
        step: String? = null
    ) {
        viewModelScope.launch {
            statsRepository.getStatsSeries(start, end, step)
                .collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let { statsResult ->
                                _statsSeries.value = statsResult
                            }
                        }

                        is Resource.Error -> {
                            _errorMessage.value = result.message
                        }

                        is Resource.Loading -> {
                        }
                    }
                }
        }
    }

    fun getHourlyStats(
        start: LocalDateTime? = state.value.start,
        end: LocalDateTime? = state.value.end
    ) {
        viewModelScope.launch {
            statsRepository.getHourlyStats(start, end)
                .collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let { statsResult ->
                                _hourlyStats.value = statsResult
                            }
                        }

                        is Resource.Error -> {
                            _errorMessage.value = result.message
                        }

                        is Resource.Loading -> {
                        }
                    }
                }
        }
    }

}