package com.github.barmiro.syshclient.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.barmiro.syshclient.data.common.authentication.AuthenticationRepository
import com.github.barmiro.syshclient.data.common.preferences.UserPreferencesRepository
import com.github.barmiro.syshclient.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepo: AuthenticationRepository,
    private val userPrefRepo: UserPreferencesRepository
) : ViewModel() {

    private val _registerState = MutableStateFlow<String?>(null)
    val registerState: StateFlow<String?> = _registerState.asStateFlow()

    private val _isRegistered = MutableStateFlow<Boolean>(false)
    val isRegistered: StateFlow<Boolean> = _isRegistered.asStateFlow()

    fun register(username: String, password: String) {
        viewModelScope.launch {
            authRepo.register(username, password).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _isRegistered.value = true
                    }

                    is Resource.Error -> {
                        _registerState.value = result.message
                    }

                    is Resource.Loading -> {
                    }
                }
            }
        }
    }


    fun getToken(username: String, password: String) {
        viewModelScope.launch {
            authRepo.getToken(username, password).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        result.data?.let {
                            userPrefRepo.saveToken(it)
                            userPrefRepo.setLoggedIn(true)
                        }
                    }

                    is Resource.Error -> {
                    }

                    is Resource.Loading -> {
                    }
                }
            }
        }
    }

}