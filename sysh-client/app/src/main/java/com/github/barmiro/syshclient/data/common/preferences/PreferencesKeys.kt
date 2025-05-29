package com.github.barmiro.syshclient.data.common.preferences

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesKeys {
    val USERNAME = stringPreferencesKey("username")
    val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    val USER_ROLE = stringPreferencesKey("user_role")
    val SERVER_URL = stringPreferencesKey("server_url")
    val IS_USERNAME_DISPLAYED = booleanPreferencesKey("is_username_displayed")
    val IS_DEMO_VERSION = booleanPreferencesKey("is_demo_version")
    val TOKEN = stringPreferencesKey("jwt_token")
    val APP_THEME = stringPreferencesKey("app_theme")
    val IS_GRADIENT_ENABLED = booleanPreferencesKey("is_gradient_enabled")
    val USER_DISPLAY_NAME = stringPreferencesKey("user_display_name")
    val USER_TIMEZONE = stringPreferencesKey("user_timezone")
    val USER_IMAGE_URL = stringPreferencesKey("user_image_url")
    val IS_AUTHORIZED_WITH_SPOTIFY = booleanPreferencesKey("is_authorized_with_spotify")
    val SHOW_IMPORT_ALERT = booleanPreferencesKey("show_import_alert")
}