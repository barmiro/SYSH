package com.github.barmiro.syshclient.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF994C9B),
    onPrimary = Color(0xFFFAFAFA),
    primaryContainer = Color(0xFF202020),
    onPrimaryContainer = Color(0xFFD2E4FF),
    secondary = Color(0xFFBCC7DC),
    onSecondary = Color(0xFF263141),
    secondaryContainer = Color(0xFF181818),
    onSecondaryContainer = Color(0xFFE2E2E2),
    background = Color(0xFF101010),
    onBackground = Color(0xFFE2E2E2),
    surface = Color(0xFF121316),
    onSurface = Color(0xFFE2E2E5),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFF92D5F),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD2E4FF),
    onPrimaryContainer = Color(0xFF001D36),
    secondary = Color(0xFF525F70),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEFEDF2),
    onSecondaryContainer = Color(0xFF181818),
    background = Color(0xFFFCFCFC),
    onBackground = Color(0xFF1A1C1E),
    surface = Color(0xFFFAFCFF),
    onSurface = Color(0xFF1A1C1E),
    error = Color(0xFFBA1A1A),
    onError = Color.White,

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun SyshClientTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
//        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
//            val context = LocalContext.current
//            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
//        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}