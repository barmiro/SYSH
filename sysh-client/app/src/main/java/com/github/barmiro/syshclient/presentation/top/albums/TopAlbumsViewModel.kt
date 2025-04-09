package com.github.barmiro.syshclient.presentation.top.albums

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.github.barmiro.syshclient.data.top.TopRepository
import com.github.barmiro.syshclient.domain.top.TopAlbum
import com.github.barmiro.syshclient.presentation.top.TopScreenEvent
import com.github.barmiro.syshclient.presentation.top.TopScreenState
import com.github.barmiro.syshclient.presentation.top.TopScreenStateManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class TopAlbumsViewModel @Inject constructor(
    private val topRepository: TopRepository,
    private val stateManager: TopScreenStateManager
): ViewModel(), DefaultLifecycleObserver {

    val state: StateFlow<TopScreenState> = stateManager.state

    private val _albums = MutableStateFlow<PagingData<TopAlbum>>(PagingData.empty())
    val albums: StateFlow<PagingData<TopAlbum>> = _albums

    var previousValues = listOf("", LocalDateTime.MIN, LocalDateTime.MAX)

    fun onEvent(event: TopScreenEvent) {
        when(event) {
            is TopScreenEvent.Refresh -> {
                getTopAlbums()
            }
            else -> {}
        }
    }

    fun getTopAlbums(
        start: LocalDateTime? = state.value.start,
        end: LocalDateTime? = state.value.end,
        sort: String? = state.value.sort
    ) {
        viewModelScope.launch {
            topRepository.getTopAlbums(
                start,
                end,
                sort
            )
                .cachedIn(viewModelScope)
                .collect { pagedData ->
                _albums.value = pagedData
            }
        }
    }
}