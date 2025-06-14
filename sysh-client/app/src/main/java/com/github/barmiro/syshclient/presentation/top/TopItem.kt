package com.github.barmiro.syshclient.presentation.top

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.MarqueeSpacing
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.barmiro.syshclient.R
import com.github.barmiro.syshclient.domain.top.TopItemData
import com.github.barmiro.syshclient.presentation.common.LoadImage
import com.github.barmiro.syshclient.presentation.common.LoadImageWithGradient
import com.github.barmiro.syshclient.presentation.top.components.TopItemSortParamDisplay
import com.github.barmiro.syshclient.presentation.top.components.TopListIndexText
import com.github.barmiro.syshclient.util.drawMarqueeTextFadedEdge

@Composable
fun TopItem(
    index: Int,
    itemData: TopItemData,
    sort: String?,
    modifier: Modifier = Modifier,
    onColorExtracted: (Color) -> Unit,
    startColor: Color,
    placeholderID: Int,
    isGradientEnabled: Boolean
) {
    var gradientModifier: Modifier = Modifier
    val dominantColor = remember { mutableStateOf(startColor) }

    if (isGradientEnabled) {
        val animatedColor = animateColorAsState(
            targetValue = dominantColor.value,
            animationSpec = tween(durationMillis = 400)
        )

        val imageBrush = ShaderBrush(
            ImageShader(
                image = ImageBitmap.imageResource(
                    id = R.drawable.noise_128x128),
                tileModeX = TileMode.Repeated,
                tileModeY = TileMode.Repeated
            )
        )

        gradientModifier = Modifier
            .background(
                brush = imageBrush,
                alpha = 0.01f
            )
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.secondaryContainer,
                        animatedColor.value
                    ),
                    startX = 80f
                )
            )
    }

    Surface(
        modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Box(
            modifier = gradientModifier
        ) {
            Row(
                modifier = modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    Modifier.weight(1f)
                        .padding(end = 8.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TopListIndexText(index)
                }
                Column(
                ) {
                    if (isGradientEnabled) {
                        LoadImageWithGradient(
                            itemName = itemData.name,
                            imageUrl = itemData.thumbnailUrl,
                            placeholderID = placeholderID,
                            onGradientColorChange = {
                                dominantColor.value = it
                                onColorExtracted(it)
                            },
                            modifier = Modifier.height(50.dp).width(50.dp)
                                .clip(RoundedCornerShape(2.dp))
                        )
                    } else {
                        LoadImage(
                            itemName = itemData.name,
                            imageUrl = itemData.thumbnailUrl,
                            placeholderID = placeholderID,
                            modifier = Modifier.height(50.dp).width(50.dp)
                                .clip(RoundedCornerShape(2.dp))
                        )
                    }
                }
                Column(
                    modifier = Modifier.weight(5f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
                            .drawWithContent {
                                drawContent()
                                drawMarqueeTextFadedEdge(isRightEdge = false)
                                drawMarqueeTextFadedEdge(isRightEdge = true)
                            }
                    ) {
                        Text(
                            text = when{
                                itemData.name.isEmpty() -> "[ Deleted by Spotify ]"
                                else -> itemData.name
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            lineHeight = 18.sp,
                            color = MaterialTheme.colorScheme.onBackground,
                            maxLines = 1,
                            modifier = Modifier
                                .basicMarquee(
                                    iterations = 3,
                                    velocity = 48.dp,
                                    repeatDelayMillis = 3000,
                                    initialDelayMillis = 1000,
                                    spacing = MarqueeSpacing(32.dp)
                                )
                                .padding(start = 8.dp)
                        )
                    }
                    itemData.primaryArtistName?.let {
                        Text(
                            text = itemData.primaryArtistName,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 14.sp,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            modifier = Modifier.alpha(0.5F)
                                .padding(start = 8.dp)
                        )
                    }
                    itemData.albumName?.let {
                        Text(
                            text = itemData.albumName,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 12.sp,
                            lineHeight = 14.sp,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            modifier = Modifier.alpha(0.5F)
                                .padding(start = 8.dp)
                        )
                    }
                }
                TopItemSortParamDisplay(
                    sort = sort,
                    minutesPlayed = itemData.minutesPlayed,
                    streamCount = itemData.streamCount
                )
            }
        }
    }
}