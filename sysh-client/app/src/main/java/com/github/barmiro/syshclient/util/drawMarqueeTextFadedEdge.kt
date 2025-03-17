package com.github.barmiro.syshclient.util

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.unit.dp

fun ContentDrawScope.drawMarqueeTextFadedEdge(isRightEdge: Boolean) {

    val baseWidth = 8.dp.toPx()
    var xOffset = 0f
    var width = baseWidth
    var startX = 0f
    var endX = baseWidth

    if (isRightEdge) {
        xOffset = size.width - 2 * baseWidth
        width = 2 * baseWidth
        startX = size.width
        endX = size.width - 2 * baseWidth
    }

    drawRect(
        topLeft = Offset(xOffset, 0f),
        size = Size(width, size.height),
        brush = Brush.horizontalGradient(
            colors = listOf(Color.Transparent, Color.Black),
            startX = startX,
            endX = endX
        ),
        blendMode = BlendMode.DstIn
    )
}