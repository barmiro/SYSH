package com.github.barmiro.syshclient.presentation.home

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.barmiro.syshclient.data.common.preferences.UserPreferencesRepository
import com.github.barmiro.syshclient.data.stats.StatsRepository
import com.github.barmiro.syshclient.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val statsRepository: StatsRepository,
    private val userPrefRepo: UserPreferencesRepository
): ViewModel(), DefaultLifecycleObserver {

//    DTO for now
    private val _homeState = MutableStateFlow(HomeState())
    val homeState: StateFlow<HomeState> = _homeState


    val userDisplayName: StateFlow<String?> = userPrefRepo.userDisplayName
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val username: StateFlow<String?> = userPrefRepo.username
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val isDemoVersion: StateFlow<Boolean?> = userPrefRepo.isDemoVersion
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val showImportAlert: StateFlow<Boolean> = userPrefRepo.showImportAlert
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    val isUsernameDisplayed: StateFlow<Boolean?> = userPrefRepo.isUsernameDisplayed
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )


    fun getStats() {
        viewModelScope.launch {
            statsRepository.getHomeStats(
            )
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


}