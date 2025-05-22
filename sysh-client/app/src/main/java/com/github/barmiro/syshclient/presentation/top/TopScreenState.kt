package com.github.barmiro.syshclient.presentation.top

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

data class TopScreenState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val oldestStreamDate: LocalDate = LocalDate.of(2006, 1, 1),
    val sort: String? = null,
    val start: LocalDateTime? = null,
    val end: LocalDateTime? = null,
    val dateRangeMode: String? = null,
    val dateRangePageNumber: Int? = null
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
        start: LocalDateTime? = null,
        end: LocalDateTime? = null,
        dateRangeMode: String? = null,
        dateRangePageNumber: Int? = null
    ) {
        _state.value = _state.value.copy(
            isLoading = isLoading ?: _state.value.isLoading,
            isRefreshing = isRefreshing ?: _state.value.isRefreshing,
            oldestStreamDate = oldestStreamDate ?: _state.value.oldestStreamDate,
            sort = handleNullOrEmptyString(sort, _state.value.sort),
            start = handleNullOrMinLocalDateTime(start, _state.value.start),
            end = handleNullOrMinLocalDateTime(end, _state.value.end),
            dateRangeMode = handleNullOrEmptyString(dateRangeMode, _state.value.dateRangeMode),
            dateRangePageNumber = handleNullOrNegativeInt(dateRangePageNumber, _state.value.dateRangePageNumber)
        )
    }

    fun wipeState() {
        _state.value = TopScreenState()
    }
}

//These are for explicitly resetting values to null. More elegant way?

private fun handleNullOrMinLocalDateTime(newValue: LocalDateTime?, currentValue: LocalDateTime?): LocalDateTime? {
    return when {
        newValue == null -> currentValue
        newValue.isEqual(LocalDateTime.MIN) -> null
        else -> newValue
    }
}

private fun handleNullOrEmptyString(newValue: String?, currentValue: String?): String? {
    return when {
        newValue == null -> currentValue
        newValue.isEmpty() -> null
        else -> newValue
    }
}

private fun handleNullOrNegativeInt(newValue: Int?, currentValue: Int?): Int? {
    return when {
        newValue == null -> currentValue
        newValue < 0 -> null
        else -> newValue
    }
}