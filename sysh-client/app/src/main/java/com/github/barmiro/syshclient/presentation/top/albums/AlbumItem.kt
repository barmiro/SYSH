package com.github.barmiro.syshclient.presentation.top.albums

import androidx.compose.foundation.MarqueeSpacing
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.github.barmiro.syshclient.domain.top.TopAlbum
import com.github.barmiro.syshclient.presentation.top.components.TopItemSortParamDisplay
import com.github.barmiro.syshclient.presentation.top.components.TopListIndexText
import com.github.barmiro.syshclient.util.drawMarqueeTextFadedEdge

@Composable
fun AlbumItem(
    index: Int,
    album: TopAlbum,
    sort: String?,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp),
        shape = RoundedCornerShape(8.dp),
        color = NavigationBarDefaults.containerColor
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
                AsyncImage(
                    model = album.thumbnailUrl,
                    contentDescription = "thumbnail for album " + album.name,
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
                        text = album.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
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
                    text = album.primaryArtistName,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 12.sp,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier.alpha(0.5F)
                        .padding(start = 8.dp)
                )
            }
            TopItemSortParamDisplay(
                sort = sort,
                minutesPlayed = album.minutesPlayed,
                streamCount = album.streamCount
            )
        }

    }
}