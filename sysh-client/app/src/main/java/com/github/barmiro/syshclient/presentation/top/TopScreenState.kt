package com.github.barmiro.syshclient.presentation.top

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

data class TopScreenState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val oldestStreamDate: LocalDate? = LocalDate.of(2006, 1, 1),
    val sort: String? = null,
    val start: String? = null,
    val end: String? = null,
    val dateRangeMode: String? = null
)

@Singleton
class TopScreenStateManager @Inject constructor() {
    private var _state = MutableStateFlow(TopScreenState())
    val state: StateFlow<TopScreenState> get() = _state

    fun updateState(
        isLoading: Boolean? = null,
        isRefreshing: Boolean? = null,
        oldestStreamDate: LocalDate? = null,
        sort: String? = null,
        start: String? = null,
        end: String? = null,
        dateRangeMode: String? = null
    ) {
        _state.value = _state.value.copy(
            isLoading = isLoading ?: _state.value.isLoading,
            isRefreshing = isRefreshing ?: _state.value.isRefreshing,
            oldestStreamDate = oldestStreamDate ?: _state.value.oldestStreamDate,
            sort = handleNullOrEmptyString(sort, _state.value.sort),
            start = handleNullOrEmptyString(start, _state.value.start),
            end = handleNullOrEmptyString(end, _state.value.end),
            dateRangeMode = handleNullOrEmptyString(dateRangeMode, _state.value.dateRangeMode)
        )
    }
}


private fun handleNullOrEmptyString(newValue: String?, currentValue: String?): String? {
    return when {
        newValue == null -> currentValue
        newValue.isEmpty() -> null
        else -> newValue
    }
}