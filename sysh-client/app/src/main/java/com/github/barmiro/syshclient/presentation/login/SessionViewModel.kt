package com.github.barmiro.syshclient.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.barmiro.syshclient.data.common.authentication.AuthenticationRepository
import com.github.barmiro.syshclient.data.common.preferences.UserPreferencesRepository
import com.github.barmiro.syshclient.data.common.startup.StartupDataRepository
import com.github.barmiro.syshclient.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
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

    private val timezone: String = ZoneId.systemDefault().id



    fun register(username: String, password: String) {
        viewModelScope.launch {
            authRepo.register(username, password, timezone).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _isRegistered.value = true
//                        this is the simplest workaround for resetting the value
                        delay(200)
                        _isRegistered.value = false
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

    fun logout() {
        viewModelScope.launch {
            userPrefRepo.logout()
        }
    }


    val isAuthorizedWithSpotify: StateFlow<Boolean> = userPrefRepo.isAuthorizedWithSpotify
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    val serverUrl: StateFlow<String?> = userPrefRepo.serverUrl
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val isLoggedIn: StateFlow<Boolean> = userPrefRepo.isLoggedIn
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
            userPrefRepo.saveServerUrl(serverUrl)
            userPrefRepo.serverUrl.first()
            onComplete()
        }
    }





}