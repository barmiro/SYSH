package com.github.barmiro.syshclient.presentation.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.barmiro.syshclient.data.common.preferences.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _isLoggedIn = MutableStateFlow<Boolean>(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()


    val serverUrl: StateFlow<String?> = userPreferencesRepository.serverUrl
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    init {
        viewModelScope.launch {

//                !!! DELETE BEFORE PUBLISHING !!!
//            userPreferencesRepository.clearAllPreferences()
//          ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯
            userPreferencesRepository.isLoggedIn.collect {
                _isLoggedIn.value = it
            }
        }
    }

    fun saveServerUrl(serverUrl: String) {
        viewModelScope.launch {
            userPreferencesRepository.saveServerUrl(serverUrl)
        }
    }

    fun setLoggedIn(value: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setLoggedIn(value)
        }
    }

    fun saveToken(token: String) {
        viewModelScope.launch {
            userPreferencesRepository.saveToken(token)
        }
    }


}