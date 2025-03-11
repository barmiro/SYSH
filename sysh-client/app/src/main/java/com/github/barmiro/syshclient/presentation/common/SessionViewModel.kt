package com.github.barmiro.syshclient.presentation.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.barmiro.syshclient.data.common.preferences.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _isLoggedIn = MutableStateFlow<Boolean>(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _isAuthorizedWithSpotify = MutableStateFlow<Boolean>(false)
    val isAuthorizedWithSpotify: StateFlow<Boolean> = _isAuthorizedWithSpotify.asStateFlow()

    init {
        viewModelScope.launch {

//                !!! DELETE BEFORE PUBLISHING !!!
            userPreferencesRepository.setLoggedIn(false)
//          ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯

            userPreferencesRepository.isLoggedIn.collect {
                _isLoggedIn.value = it
            }
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