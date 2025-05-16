package com.github.barmiro.syshclient.data.common.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.github.barmiro.syshclient.util.AppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

//    these are for use by interceptors, I might have to rethink this approach
    private val _tokenFlow = MutableStateFlow<String?>(null)
    val tokenFlow: StateFlow<String?> = _tokenFlow.asStateFlow()

    private val _serverUrlFlow = MutableStateFlow<String?>(null)
    val serverUrlFlow: StateFlow<String?> = _serverUrlFlow.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.data.map {
                it[PreferencesKeys.TOKEN] to it[PreferencesKeys.SERVER_URL]
            }.collect { (token, serverUrl) ->
                _tokenFlow.value = token
                _serverUrlFlow.value = serverUrl
            }
        }
    }

    suspend fun saveServerUrl(serverUrl: String) {
        dataStore.edit {
            it[PreferencesKeys.SERVER_URL] = serverUrl
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

    suspend fun saveUserRole(userRole: String) {
        dataStore.edit {
            it[PreferencesKeys.USER_ROLE] = userRole
        }
    }

    suspend fun saveTheme(theme: AppTheme) {
        dataStore.edit {
            it[PreferencesKeys.APP_THEME] = theme.name
        }
    }

    suspend fun setImportAlert(showImportAlert: Boolean) {
        dataStore.edit {
            it[PreferencesKeys.SHOW_IMPORT_ALERT] = showImportAlert
        }
    }

    suspend fun setDemoVersion(isDemoVersion: Boolean) {
        dataStore.edit {
            it[PreferencesKeys.IS_DEMO_VERSION] = isDemoVersion
        }
    }

    suspend fun setIsUsernameDisplayed(isUsernameDisplayed: Boolean) {
        dataStore.edit {
            it[PreferencesKeys.IS_USERNAME_DISPLAYED] = isUsernameDisplayed
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
    suspend fun saveImageUrl(imageUrl: String?) {
        imageUrl?.let { url ->
            dataStore.edit {
                it[PreferencesKeys.USER_IMAGE_URL] = url
            }
        }
    }

    val serverUrl: Flow<String?> = dataStore.data.map {
        it[PreferencesKeys.SERVER_URL]
    }

    val username: Flow<String?> = dataStore.data.map {
        it[PreferencesKeys.USERNAME]
    }

    val token: Flow<String?> = dataStore.data.map {
        it[PreferencesKeys.TOKEN]
    }

    val isLoggedIn: Flow<Boolean> = dataStore.data.map {
        it[PreferencesKeys.IS_LOGGED_IN] ?: false
    }

    val showImportAlert: Flow<Boolean> = dataStore.data.map {
        it[PreferencesKeys.SHOW_IMPORT_ALERT] ?: false
    }

    val isUsernameDisplayed: Flow<Boolean> = dataStore.data.map {
        it[PreferencesKeys.IS_USERNAME_DISPLAYED] ?: false
    }

    val isDemoVersion: Flow<Boolean?> = dataStore.data.map {
        it[PreferencesKeys.IS_DEMO_VERSION]
    }

    val appTheme: Flow<AppTheme> = dataStore.data.map {
            AppTheme.entries.firstOrNull { theme ->
                theme.name == it[PreferencesKeys.APP_THEME]
            } ?: AppTheme.DARK
        }

    val isAuthorizedWithSpotify: Flow<Boolean> = dataStore.data.map {
        it[PreferencesKeys.IS_AUTHORIZED_WITH_SPOTIFY] ?: false
    }

    val userDisplayName: Flow<String?> = dataStore.data.map {
        it[PreferencesKeys.USER_DISPLAY_NAME]
    }

    val userRole: Flow<String?> = dataStore.data.map {
        it[PreferencesKeys.USER_ROLE]
    }

    val userImageUrl: Flow<String?> = dataStore.data.map {
        it[PreferencesKeys.USER_IMAGE_URL]
    }

    suspend fun clearAllPreferences() {
        dataStore.edit { it.clear() }
    }

    suspend fun logout() {
        val currentServerUrl: String? = serverUrl.first()
        dataStore.edit { data ->
            data.clear()
            currentServerUrl?.let {
                data[PreferencesKeys.SERVER_URL] = it
            }
        }
    }
}