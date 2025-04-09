package com.github.barmiro.syshclient.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils

fun tintFromColor(baseColor: Color, lightness: Float = 0.5f, alpha: Float = 0.1f): Color {
    val hsv = FloatArray(3)
    ColorUtils.colorToHSL(baseColor.toArgb(), hsv)
    hsv[2] = lightness.coerceIn(0f, 1f)

    val tintedArgb = ColorUtils.HSLToColor(hsv)
    return Color(tintedArgb).copy(alpha = alpha)
}