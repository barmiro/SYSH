package com.github.barmiro.syshclient.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.barmiro.syshclient.data.common.AuthenticationRepository
import com.github.barmiro.syshclient.data.common.UserPreferencesRepository
import com.github.barmiro.syshclient.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepo: AuthenticationRepository,
    private val userPrefRepo: UserPreferencesRepository
) : ViewModel() {


    fun getToken(username: String, password: String) {
//        var token: String
        viewModelScope.launch {
            authRepo.getToken(username, password).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        result.data?.let {
                            userPrefRepo.saveToken(it)
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

    fun setLoggedIn(value: Boolean) {
        viewModelScope.launch {
            userPrefRepo.setLoggedIn(value)
        }
    }
}