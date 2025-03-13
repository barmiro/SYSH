package com.github.barmiro.syshclient.data.common.preferences

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesKeys {
    val USERNAME = stringPreferencesKey("username")
    val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    val IS_FRESH_INSTALL = booleanPreferencesKey("is_fresh_install")
    val TOKEN = stringPreferencesKey("jwt_token")
    val USER_DISPLAY_NAME = stringPreferencesKey("user_display_name")
    val IS_AUTHORIZED_WITH_SPOTIFY = booleanPreferencesKey("is_authorized_with_spotify")
}