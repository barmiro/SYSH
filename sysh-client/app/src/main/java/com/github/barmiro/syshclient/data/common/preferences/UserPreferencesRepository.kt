package com.github.barmiro.syshclient.data.common.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    private val _tokenFlow = MutableStateFlow<String?>(null)
    val tokenFlow: StateFlow<String?> = _tokenFlow.asStateFlow()

    init {
        // Load the token initially
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.data.map { it[PreferencesKeys.TOKEN] }.collect {
                _tokenFlow.value = it
            }
        }
    }


    suspend fun saveUsername(username: String) {
        dataStore.edit {
            it[PreferencesKeys.USERNAME] = username
        }
    }

    suspend fun saveToken(token: String) {
        dataStore.edit {
            it[PreferencesKeys.TOKEN] = token
        }
    }

    suspend fun setLoggedIn(isLoggedIn: Boolean) {
        dataStore.edit {
            it[PreferencesKeys.IS_LOGGED_IN] = isLoggedIn
        }
    }

    suspend fun setFreshInstall(isFreshInstall: Boolean) {
        dataStore.edit {
            it[PreferencesKeys.IS_FRESH_INSTALL] = isFreshInstall
        }
    }

    suspend fun setAuthorizedWithSpotify(isAuthorizedWithSpotify: Boolean) {
        dataStore.edit {
            it[PreferencesKeys.IS_AUTHORIZED_WITH_SPOTIFY] = isAuthorizedWithSpotify
        }
    }

    suspend fun saveUserDisplayName(displayName: String) {
        dataStore.edit {
            it[PreferencesKeys.USER_DISPLAY_NAME] = displayName
        }
    }

//    suspend fun setAuthenticatedWithSpo


    val username: Flow<String?> = dataStore.data.map {
        it[PreferencesKeys.USERNAME]
    }

    val token: Flow<String?> = dataStore.data.map {
        it[PreferencesKeys.TOKEN]
    }

    val isLoggedIn: Flow<Boolean> = dataStore.data.map {
        it[PreferencesKeys.IS_LOGGED_IN] ?: false
    }

    val isFreshInstall: Flow<Boolean> = dataStore.data.map {
        it[PreferencesKeys.IS_FRESH_INSTALL] ?: true
    }

    val isAuthorizedWithSpotify: Flow<Boolean> = dataStore.data.map {
        it[PreferencesKeys.IS_AUTHORIZED_WITH_SPOTIFY] ?: false
    }

    val userDisplayName: Flow<String?> = dataStore.data.map {
        it[PreferencesKeys.USER_DISPLAY_NAME]
    }

    suspend fun clearAllPreferences() {
        dataStore.edit { it.clear() }
    }
}