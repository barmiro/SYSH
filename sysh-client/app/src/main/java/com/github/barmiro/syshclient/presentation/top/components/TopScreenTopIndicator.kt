package com.github.barmiro.syshclient.presentation.top.components

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TabPosition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@Composable
fun TopScreenTopIndicator(tabPositions: List<TabPosition>, animationOffset: Float) {
    val transition = updateTransition(0 - animationOffset)
    val leftIndicatorEdge by transition.animateDp(
        transitionSpec = {
            if (initialState < targetState) {
                spring(dampingRatio = 1f, stiffness = 200f)
            } else {
                spring(dampingRatio = 1f, stiffness = 2000f)
            }
        }
    ) {
        tabPositions[0].right * (it + 0.125f)
    }

    val rightIndicatorEdge by transition.animateDp(
        transitionSpec = {
            if (initialState < targetState) {
                spring(dampingRatio = 1f, stiffness = 2000f)
            } else {
                spring(dampingRatio = 1f, stiffness = 200f)
            }
        }
    ) {
        tabPositions[0].right * (it + 0.875f)
    }


    Box(
        modifier = Modifier
            .wrapContentSize(align = Alignment.BottomStart)
            .offset(x = leftIndicatorEdge)
            .width(rightIndicatorEdge - leftIndicatorEdge)
            .padding(8.dp)
            .fillMaxWidth()
            .height(64.dp)
            .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(32.dp))
            .zIndex(-1f)
    )
}