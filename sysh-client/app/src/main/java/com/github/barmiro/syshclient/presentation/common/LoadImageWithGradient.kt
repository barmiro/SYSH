package com.github.barmiro.syshclient.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import coil3.asDrawable
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.allowHardware
import com.github.barmiro.syshclient.util.tintFromColor

@Composable
fun LoadImageWithGradient(
    itemName: String,
    imageUrl: String?,
    placeholderID: Int,
    onGradientColorChange: (Color) -> Unit,
    modifier: Modifier
) {
    var isImageEmpty by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val request = imageUrl?.let { url ->
        ImageRequest.Builder(context)
            .data(url)
            .allowHardware(false)
            .listener(
                onSuccess = { _, result ->
                    isImageEmpty = false
                    val bitmap = result.image.asDrawable(context.resources).toBitmap()
                    Palette.Builder(bitmap).generate { palette ->
                        palette?.let { pal ->
                            onGradientColorChange(
                                tintFromColor(
                                    Color(
                                        pal.getVibrantColor(
                                            pal.getDominantColor(
                                                tintFromColor(Color.Gray).toArgb()
                                            )
                                        )
                                    )
                                )
                            )
                        }
                    }
                },
                onError = { _, _ ->
                    isImageEmpty = true
                    onGradientColorChange(tintFromColor(Color.Gray))
                }
            )
            .build()
    }
    Box(
        modifier = modifier
            .background(Color.Gray.copy(alpha = 0.1f)),
        contentAlignment = Alignment.Center) {
        if(!isImageEmpty) {
            AsyncImage(
                model = request,
                contentScale = ContentScale.Crop,
                contentDescription = "Thumbnail for item $itemName",
                modifier = modifier,
            )
        } else {
            Icon(
                painter = painterResource(id = placeholderID),
                tint = IconButtonDefaults.iconButtonColors().contentColor,
                contentDescription = "Placeholder for item $itemName",
                modifier = Modifier.size(28.dp).alpha(0.8f)
            )
        }
    }
}

@Composable
fun LoadImage(
    itemName: String,
    imageUrl: String?,
    placeholderID: Int,
    modifier: Modifier
) {
    var isImageEmpty by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val request = imageUrl?.let { url ->
        ImageRequest.Builder(context)
            .data(url)
            .allowHardware(false)
            .listener(
                onSuccess = { _, _ ->
                    isImageEmpty = false},
                onError = { _, _ ->
                    isImageEmpty = true
                }
            )
            .build()
    }
    Box(
        modifier = modifier
            .background(Color.Gray.copy(alpha = 0.1f)),
        contentAlignment = Alignment.Center) {
        if(!isImageEmpty) {
            AsyncImage(
                model = request,
                contentScale = ContentScale.Crop,
                contentDescription = "Thumbnail for item $itemName",
                modifier = modifier,
            )
        } else {
            Icon(
                painter = painterResource(id = placeholderID),
                tint = IconButtonDefaults.iconButtonColors().contentColor,
                contentDescription = "Placeholder for item $itemName",
                modifier = Modifier.size(28.dp).alpha(0.8f)
            )
        }
    }
}
