package com.github.barmiro.syshclient.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.barmiro.syshclient.data.common.authentication.AuthenticationRepository
import com.github.barmiro.syshclient.data.common.preferences.UserPreferencesRepository
import com.github.barmiro.syshclient.data.common.startup.StartupDataRepository
import com.github.barmiro.syshclient.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepo: AuthenticationRepository,
    private val userPrefRepo: UserPreferencesRepository,
    private val startupDataRepository: StartupDataRepository
) : ViewModel() {

    private val _registerState = MutableStateFlow<String?>(null)
    val registerState: StateFlow<String?> = _registerState.asStateFlow()

    private val _isRegistered = MutableStateFlow(false)
    val isRegistered: StateFlow<Boolean> = _isRegistered.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _responseCode: MutableStateFlow<Int?> = MutableStateFlow(0)
    val responseCode: StateFlow<Int?> = _responseCode

    private val _errorMessage: MutableStateFlow<String?> = MutableStateFlow(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _spotifyAuthUrl: MutableStateFlow<String?> = MutableStateFlow(null)
    val spotifyAuthUrl: StateFlow<String?> = _spotifyAuthUrl

    private val _isAuthorizedWithSpotify = MutableStateFlow(false)
    val isAuthorizedWithSpotify: StateFlow<Boolean> = _isAuthorizedWithSpotify.asStateFlow()




    fun register(username: String, password: String) {
        viewModelScope.launch {
            authRepo.register(username, password).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _isRegistered.value = true
                    }

                    is Resource.Error -> {
                        _responseCode.value = result.code
                        _registerState.value = result.message
                        _errorMessage.value = result.message
                    }

                    is Resource.Loading -> {
                        _isLoading.value = result.isLoading
                    }
                }
            }
        }
    }


    fun getToken(username: String, password: String) {
        _responseCode.value = 0
        viewModelScope.launch {
            userPrefRepo.setLoggedIn(false)
            authRepo.getToken(username, password).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        result.data?.let {
                            userPrefRepo.saveToken(it)
                            userPrefRepo.setLoggedIn(true)
                        }
                    }

                    is Resource.Error -> {
                        _responseCode.value = result.code
                        _errorMessage.value = result.message
                    }

                    is Resource.Loading -> {
                        _isLoading.value = result.isLoading
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
                            result.data?.let {
                                userPrefRepo.setAuthorizedWithSpotify(true)
                                userPrefRepo.saveUserDisplayName(it)
                            }
                            _isAuthorizedWithSpotify.value = userPrefRepo.isAuthorizedWithSpotify.first()
                            _responseCode.value = 200
                        }

                        is Resource.Error -> {
                            _responseCode.value = result.code
                        }
                        is Resource.Loading -> {
                            _isLoading.value = result.isLoading
                        }
                    }
                }
        }
    }


    fun spotifyAuthorization() {
        viewModelScope.launch {
            startupDataRepository
                .getSpotifyAuthUrl()
                .collect { result ->
                when(result) {
                    is Resource.Success -> {
                        result.data?.let {
                            _spotifyAuthUrl.value = result.data
                        }
                    }
                    is Resource.Error -> {
                        _responseCode.value = result.code
                    }
                    is Resource.Loading -> {
                        _isLoading.value = result.isLoading
                    }
                }
            }
        }
    }
}