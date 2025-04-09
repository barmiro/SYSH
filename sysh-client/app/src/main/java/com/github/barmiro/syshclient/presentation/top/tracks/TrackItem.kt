package com.github.barmiro.syshclient.presentation.top.tracks

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
import androidx.compose.material3.NavigationBarDefaults
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import coil3.asDrawable
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.allowHardware
import com.github.barmiro.syshclient.domain.top.TopTrack
import com.github.barmiro.syshclient.presentation.top.components.TopItemSortParamDisplay
import com.github.barmiro.syshclient.presentation.top.components.TopListIndexText
import com.github.barmiro.syshclient.util.drawMarqueeTextFadedEdge
import com.github.barmiro.syshclient.util.tintFromColor

@Composable
fun TrackItem(
    index: Int,
    track: TopTrack,
    sort: String?,
    modifier: Modifier = Modifier,
    onColorExtracted: (Color) -> Unit,
    startColor: Color
) {
    val dominantColor = remember { mutableStateOf(startColor) }

    val animatedColor = animateColorAsState(
        targetValue = dominantColor.value,
        animationSpec = tween(durationMillis = 400)
    )

    Surface(
        modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp),
        shape = RoundedCornerShape(8.dp),
        color = NavigationBarDefaults.containerColor
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            animatedColor.value
                        )
                    )
                )
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
                    val context = LocalContext.current
                    val request = ImageRequest.Builder(context)
                        .data(track.thumbnailUrl)
                        .allowHardware(false)
                        .listener(
                            onSuccess = { _, result ->
                                val bitmap = result.image.asDrawable(context.resources).toBitmap()
                                Palette.Builder(bitmap).generate { palette ->
                                    palette?.let { pal ->
                                        dominantColor.value = tintFromColor(
                                            Color(
                                                pal.getDominantColor(
                                                    Color.Transparent.toArgb()
                                                )
                                            )
                                        )
                                        onColorExtracted(dominantColor.value)
                                    }
                                }
                            }
                        )
                        .build()
                    AsyncImage(
                        model = request,
                        contentDescription = "thumbnail for track " + track.name,
                        modifier = Modifier.height(50.dp).width(50.dp)
                            .clip(RoundedCornerShape(2.dp))
                    )
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
                                track.name.isEmpty() -> "[ Deleted track ]"
                                else -> track.name
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
                    Text(
                        text = track.primaryArtistName,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 14.sp,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        modifier = Modifier.alpha(0.5F)
                            .padding(start = 8.dp)
                    )
                    Text(
                        text = track.albumName,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 12.sp,
                        lineHeight = 14.sp,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        modifier = Modifier.alpha(0.5F)
                            .padding(start = 8.dp)
                    )
                }
                TopItemSortParamDisplay(
                    sort = sort,
                    minutesPlayed = track.minutesPlayed,
                    streamCount = track.streamCount
                )
            }
        }
    }
}