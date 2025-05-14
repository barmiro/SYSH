package com.github.barmiro.syshclient.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.barmiro.syshclient.data.common.preferences.UserPreferencesRepository
import com.github.barmiro.syshclient.data.settings.SettingsRepository
import com.github.barmiro.syshclient.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepo: SettingsRepository,
    private val userPrefRepo: UserPreferencesRepository
) : ViewModel() {

    private val _isPasswordChanged = MutableStateFlow<Boolean?>(null)
    val isPasswordChanged: StateFlow<Boolean?> = _isPasswordChanged

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun changePassword(oldPassword: String, newPassword: String) {
        viewModelScope.launch {
            settingsRepo.changePassword(oldPassword, newPassword).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _isPasswordChanged.value = true
                    }

                    is Resource.Error -> {
                        _isPasswordChanged.value = false
                    }

                    is Resource.Loading -> {
                        _isLoading.value = result.isLoading
                    }
                }
            }
        }
    }

}