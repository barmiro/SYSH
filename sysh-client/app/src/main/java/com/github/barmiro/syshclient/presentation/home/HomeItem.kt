package com.github.barmiro.syshclient.presentation.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import coil3.asDrawable
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.allowHardware
import com.github.barmiro.syshclient.R
import com.github.barmiro.syshclient.util.tintFromColor
import java.text.NumberFormat
import java.time.LocalDateTime
import java.util.Locale

@Composable
fun HomeItem(
    itemText: String,
    itemValue: String,
    modifier: Modifier = Modifier,
    perDayValue: String? = null
) {
    Surface(
        modifier = Modifier.padding(5.dp),
        shape = RoundedCornerShape(16.dp),
        color = NavigationBarDefaults.containerColor
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Row {
                Text(
                    text = itemText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    textAlign = TextAlign.Center
                )
            }
            Row {

                Text(
                    text = itemValue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    textAlign = TextAlign.Center
                )
            }


            if (perDayValue != null) {
                Row {
                    Text(
                        text = perDayValue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        textAlign = TextAlign.Center
                    )

                }
            }

        }
    }
}


@Composable
fun HomeDayItem(
    itemText: String,
    itemValue: String,
    itemPercent: Int?
) {
    val year: Int = LocalDateTime.now().year
    Surface(
        modifier = Modifier.padding(5.dp),
        shape = RoundedCornerShape(16.dp),
        color = NavigationBarDefaults.containerColor
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Row {
                Text(
                    text = itemText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    textAlign = TextAlign.Center
                )
            }
            Row {

                Text(
                    text = itemValue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    textAlign = TextAlign.Center
                )
            }
            Row {
                Text(
                    text = "$itemPercent% of $year average",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.alpha(0.5f)
                )
            }
        }
    }
}

@Composable
fun HomeTopItem(
    itemLabel: String,
    itemName: String,
    imageUrl: String?,
    streamCount: Int?,
    minutesStreamed: Int?
) {

    val dominantColor = remember { mutableStateOf(Color.Transparent) }

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

    Surface(
        modifier = Modifier.padding(5.dp),
        shape = RoundedCornerShape(6.dp),
        color = NavigationBarDefaults.containerColor
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = imageBrush,
                    alpha = 0.01f
                )
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            NavigationBarDefaults.containerColor,
                            animatedColor.value
                        ),
                        startX = 80f
                    )
                )
        ) {
            Row() {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .height(116.dp)
                        .padding(vertical = 8.dp, horizontal = 8.dp),
//                horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly,
                ) {
                    Row() {
                        Text(
                            text = itemLabel,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                    }
                    Row(modifier = Modifier.weight(1f, fill = true), verticalAlignment = Alignment.Top) {
                        Text(
                            text = itemName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            lineHeight = 20.sp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 2
                        )
                    }

                    val format = NumberFormat.getInstance(Locale.US)
                    val streamCountString = streamCount?.let {
                        format.format(it) + " streams"
                    } ?: ""
                    val minutesStreamedString = minutesStreamed?.let {
                        format.format(it) + " minutes"
                    } ?: ""
                    Row(verticalAlignment = Alignment.Bottom) {

                        Text(
                            text = "$streamCountString • $minutesStreamedString",
                            fontSize = 12.sp,
                            lineHeight = 12.sp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            modifier = Modifier.alpha(0.5f)
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .padding(vertical = 8.dp, horizontal = 8.dp),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Center,
                ) {
                    val context = LocalContext.current
                    val request = ImageRequest.Builder(context)
                        .data(imageUrl)
                        .allowHardware(false)
                        .listener(
                            onSuccess = { _, result ->
                                val bitmap = result.image.asDrawable(context.resources).toBitmap()
                                Palette.Builder(bitmap).generate { palette ->
                                    palette?.let { pal ->
                                        dominantColor.value = tintFromColor(
                                            Color(
                                                pal.getVibrantColor(
                                                    pal.getDominantColor(
                                                        tintFromColor(Color.Gray).toArgb()
                                                    )
                                                )
                                            )
                                        )
                                    }
                                }
                            }
                        )
                        .build()

                    AsyncImage(
                        model = request,
                        contentDescription = "thumbnail for $itemName",
                        modifier = Modifier.height(100.dp).width(100.dp)
                            .clip(RoundedCornerShape(5.dp))
                    )
                }
            }

        }
    }
}