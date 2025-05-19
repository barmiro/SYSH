package com.github.barmiro.syshclient.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.barmiro.syshclient.data.common.preferences.UserPreferencesRepository
import com.github.barmiro.syshclient.data.settings.SettingsRepository
import com.github.barmiro.syshclient.data.settings.dataimport.ImportRepository
import com.github.barmiro.syshclient.util.AppTheme
import com.github.barmiro.syshclient.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepo: SettingsRepository,
    private val userPrefRepo: UserPreferencesRepository,
    private val importRepo: ImportRepository
) : ViewModel() {

    private val _isPasswordChanged = MutableStateFlow<Boolean?>(null)
    val isPasswordChanged: StateFlow<Boolean?> = _isPasswordChanged

    private val _isTimezoneChanged = MutableStateFlow<Boolean?>(null)
    val isTimezoneChanged: StateFlow<Boolean?> = _isTimezoneChanged

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    val isUsernameDisplayed: StateFlow<Boolean?> = userPrefRepo.isUsernameDisplayed
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val appTheme: StateFlow<AppTheme> = userPrefRepo.appTheme
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppTheme.SYSTEM_DEFAULT
        )

    fun setIsUsernameDisplayed(newValue: Boolean) {
        viewModelScope.launch {
            userPrefRepo.setIsUsernameDisplayed(newValue)
        }
    }

    fun changeAppTheme(newAppTheme: AppTheme) {
        viewModelScope.launch {
            userPrefRepo.saveTheme(newAppTheme)
        }
    }

    fun onChangePasswordReset() {
        _isPasswordChanged.value = null
    }


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

    fun updateTimezone(timezone: String) {
        viewModelScope.launch {
            settingsRepo.updateTimezone(timezone).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        importRepo.recent().collect { }
                        _isTimezoneChanged.value = true
                    }

                    is Resource.Error -> {
                        _isTimezoneChanged.value = false
                    }

                    is Resource.Loading -> {
                        _isLoading.value = result.isLoading
                    }
                }
            }
        }
    }

}