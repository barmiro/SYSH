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
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val adminRepo: AdminRepository
) : ViewModel() {

    init {
        getUsers()
    }

    private val _usernameList = MutableStateFlow<List<UserDataDTO>?>(emptyList())
    val usernameList: StateFlow<List<UserDataDTO>?> = _usernameList.asStateFlow()

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

}