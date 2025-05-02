package com.github.barmiro.syshclient.presentation.startup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.barmiro.syshclient.data.common.preferences.UserPreferencesRepository
import com.github.barmiro.syshclient.data.common.startup.StartupDataRepository
import com.github.barmiro.syshclient.data.common.startup.UrlValidationResult
import com.github.barmiro.syshclient.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import javax.inject.Inject

@HiltViewModel
class StartupViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val startupDataRepo: StartupDataRepository
) : ViewModel() {

    private val _urlInput = MutableStateFlow("")
    val urlInput: StateFlow<String> = _urlInput.asStateFlow()

    private val _serverResponded = MutableStateFlow<Boolean?>(null)
    val serverResponded: StateFlow<Boolean?> = _serverResponded.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val urlValidation: StateFlow<UrlValidationResult?> = _urlInput
        .mapLatest { validateServerUrl(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )


    fun onUrlChanged(newUrl: String) {
        _urlInput.value = newUrl
    }

    private fun validateServerUrl(input: String?): UrlValidationResult? {
        val url = input?.trim()
        if (url.isNullOrEmpty()) {
            return null
        }

        val parsedUrl: HttpUrl = (if (url.contains("://")) url else "http://$url")
            .toHttpUrlOrNull() ?: return UrlValidationResult()

        val isValidUrl: Boolean = parsedUrl.host.isNotEmpty()

        val hasScheme: Boolean = Regex("https?://.*").matches(url)
        val hasPort: Boolean = Regex(".*:\\d{1,5}\\D*").matches(url)

        return UrlValidationResult(
            isValidUrl, hasScheme, hasPort
        )
    }

    fun getServerInfo() {
        _serverResponded.value = null
        viewModelScope.launch {
            startupDataRepo.getServerInfo().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _serverResponded.value = true
                    }
//                    TODO: complete
                    is Resource.Error -> {
                        _serverResponded.value = result.code?.let {
                            it == 401
                        } ?: false
                    }
                    else -> {
                    }
                }
            }
        }
    }
}