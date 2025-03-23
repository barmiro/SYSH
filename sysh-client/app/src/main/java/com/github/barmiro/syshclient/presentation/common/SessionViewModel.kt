package com.github.barmiro.syshclient.presentation.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.barmiro.syshclient.data.common.preferences.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

//    private val _isLoggedIn = MutableStateFlow<Boolean>(false)
//    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    val serverUrl: StateFlow<String?> = userPreferencesRepository.serverUrl
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val isLoggedIn: StateFlow<Boolean> = userPreferencesRepository.isLoggedIn
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

//    init {
//        viewModelScope.launch {
////                !!! DELETE BEFORE PUBLISHING !!!
//            userPreferencesRepository.clearAllPreferences()
////          ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯
//        }
//    }

    fun saveServerUrl(serverUrl: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            userPreferencesRepository.saveServerUrl(serverUrl)
            userPreferencesRepository.serverUrl.first()
            onComplete()
        }
    }
}