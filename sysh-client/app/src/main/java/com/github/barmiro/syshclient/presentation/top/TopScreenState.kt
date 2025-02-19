package com.github.barmiro.syshclient.presentation.top

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

data class TopScreenState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val sort: String? = null,
    val start: String? = null,
    val end: String? = null
)

@Singleton
class TopScreenStateManager @Inject constructor() {
    private var _state = MutableStateFlow(TopScreenState())
    val state: StateFlow<TopScreenState> get() = _state

    fun updateState(
        isLoading: Boolean? = null,
        isRefreshing: Boolean? = null,
        sort: String? = null,
        start: String? = null,
        end: String? = null
    ) {
        _state.value = _state.value.copy(
            isLoading = isLoading ?: _state.value.isLoading,
            isRefreshing = isRefreshing ?: _state.value.isRefreshing,
            sort = sort ?: _state.value.sort,
            start = start ?: _state.value.start,
            end = end ?: _state.value.end
        )
    }
}