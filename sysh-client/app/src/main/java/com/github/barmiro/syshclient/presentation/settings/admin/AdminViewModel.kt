package com.github.barmiro.syshclient.presentation.settings.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.barmiro.syshclient.data.common.startup.UserDataDTO
import com.github.barmiro.syshclient.data.settings.admin.AdminRepository
import com.github.barmiro.syshclient.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val adminRepo: AdminRepository
) : ViewModel() {
    

    private val _usernameList = MutableStateFlow<List<UserDataDTO>?>(emptyList())
    val usernameList: StateFlow<List<UserDataDTO>?> = _usernameList.asStateFlow()

    private val _isCreateUserLoading = MutableStateFlow(false)
    val isCreateUserLoading: StateFlow<Boolean> = _isCreateUserLoading.asStateFlow()

    private val _userCreationString = MutableStateFlow<String?>(null)
    val userCreationString: StateFlow<String?> = _userCreationString.asStateFlow()

    private val _passwordResetMap = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val passwordResetMap: StateFlow<Map<String, Boolean>> = _passwordResetMap.asStateFlow()

    private val timezone: String = ZoneId.systemDefault().id

    fun getUsers() {
        viewModelScope.launch {
            adminRepo.getUsers().collect { result ->
                when(result) {
                    is Resource.Success -> _usernameList.value = result.data
                    else -> { } // TODO
                }
            }
        }
    }

    fun createUser(username: String, password: String, role: String) {
        viewModelScope.launch {
            adminRepo.createUser(username, password, timezone, role).collect { result ->
                val privilegesString = if (role == "ADMIN") " with admin privileges" else ""
                when (result) {
                    is Resource.Success -> {
                        _userCreationString.value = "User $username$privilegesString created successfully."
                    }

                    is Resource.Error -> {
                        _userCreationString.value = "Failed to create user $username$privilegesString."
                    }

                    is Resource.Loading -> {
                        _isCreateUserLoading.value = result.isLoading
                    }
                }
            }
        }
    }

    fun resetPassword(username: String, password: String) {
        viewModelScope.launch {
            adminRepo.resetPassword(username, password).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _passwordResetMap.value = _passwordResetMap.value.toMutableMap().apply {
                            result.data?.let { username ->
                                this[username] = true
                            }
                        }
                    }

                    is Resource.Error -> {
                        _passwordResetMap.value = _passwordResetMap.value.toMutableMap().apply {
                            result.data?.let { username ->
                                this[username] = false
                            }
                        }
                    }
                    is Resource.Loading -> {
                    }
                }
            }
        }
    }


    fun deleteUser(username: String) {
        viewModelScope.launch {
            adminRepo.deleteUser(username).collect { result ->
                when(result) {
                    is Resource.Success -> {
                        getUsers()
                    }
                    else -> {
                    }
                }
            }
        }
    }

}