package com.github.barmiro.syshclient.presentation.home

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.barmiro.syshclient.data.common.StartupDataRepository
import com.github.barmiro.syshclient.data.stats.StatsRepository
import com.github.barmiro.syshclient.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val statsRepository: StatsRepository,
    private val startupDataRepository: StartupDataRepository
): ViewModel(), DefaultLifecycleObserver {

//    DTO for now
    private val _homeState = MutableStateFlow(HomeState())
    val homeState: StateFlow<HomeState> = _homeState

    fun getStats() {
        viewModelScope.launch {
            statsRepository.getStats("", "")
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

    fun getUserData() {
        viewModelScope.launch {
            startupDataRepository.getUserDisplayName()
                .collect { result ->
                    when(result) {
                        is Resource.Success -> {
                            result.data?.let { userDataResult ->
                                _homeState.update {
                                    it.copy(userDisplayName = userDataResult)
                                }
                            }
                        }
                        is Resource.Error -> {

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