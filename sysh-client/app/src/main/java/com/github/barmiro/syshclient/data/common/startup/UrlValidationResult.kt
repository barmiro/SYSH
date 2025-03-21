package com.github.barmiro.syshclient.data.common.startup

data class UrlValidationResult(
    val isValidUrl: Boolean = false,
    val hasScheme: Boolean = false,
    val hasPort: Boolean = false
)
