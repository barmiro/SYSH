package com.github.barmiro.syshclient.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.barmiro.syshclient.data.common.AuthenticationRepository
import com.github.barmiro.syshclient.data.common.UserPreferencesRepository
import com.github.barmiro.syshclient.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepo: AuthenticationRepository,
    private val userPrefRepo: UserPreferencesRepository
) : ViewModel() {

    private val _registerState = MutableStateFlow<Resource<String>?>(null)
    val registerState: StateFlow<Resource<String>?> = _registerState.asStateFlow()

    fun register(username: String, password: String) {
        viewModelScope.launch {
            authRepo.register(username, password).collect { result ->
                _registerState.value = result
            }
        }
    }
}