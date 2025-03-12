package com.github.barmiro.syshclient.presentation.top

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.barmiro.syshclient.data.stats.StatsRepository
import com.github.barmiro.syshclient.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TopScreenViewModel @Inject constructor(
    private val statsRepository: StatsRepository,
    private val stateManager: TopScreenStateManager
): ViewModel(), DefaultLifecycleObserver {

    val state: StateFlow<TopScreenState> = stateManager.state


    fun sortParam(): String? {
        return state.value.sort
    }

    fun getOldestStreamDate() {
        viewModelScope.launch {
            statsRepository.getOldestStreamDate()
                .collect { result ->
                    when(result) {
                        is Resource.Success -> {
                            result.data.let { oldestStreamDate ->
                                stateManager.updateState(
                                    oldestStreamDate = oldestStreamDate
                                )
                            }
                        }
                        is Resource.Error -> {

                        }
                        is Resource.Loading -> {
//                            not adding anything here, i think it will mess with the loading state too much
                        }
                    }
            }
        }
    }

    fun onEvent(event: TopScreenEvent) {
        when(event) {
            is TopScreenEvent.Refresh -> {
//                this won't happen
            }
            is TopScreenEvent.OnSearchParameterChange -> {
                stateManager.updateState(
                    start = event.start,
                    end = event.end,
                    sort = event.sort
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

}