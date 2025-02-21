package com.github.barmiro.syshclient.presentation.top.tracks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.github.barmiro.syshclient.domain.top.TopTrack
import java.text.NumberFormat
import java.util.Locale

@Composable
fun TrackItem(
    index: Int,
    track: TopTrack,
    sort: String?,
    modifier: Modifier = Modifier
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
            if (index < 100) {
                Text(
                    text = index.toString(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
            } else {
                Text(
                    text = index.toString(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
            }
        }
        Column(
        ) {
            AsyncImage(
                model = track.thumbnailUrl,
                contentDescription = "thumbnail for track " + track.name,
                modifier = Modifier.height(50.dp).width(50.dp)
                    .clip(RoundedCornerShape(2.dp))
            )
        }
        Column(
            modifier = Modifier.weight(5f).padding(horizontal = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
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
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier.weight(5f)
                )
            }
//            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = track.primaryArtistName,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 14.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier.alpha(0.5F)
            )
            Text(
                text = track.albumName,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 12.sp,
                lineHeight = 14.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier.alpha(0.5F)
            )
        }
        val sortParam: String
        val sortParamName: String
        val otherParam: String
        val otherParamName: String
        val format = NumberFormat.getInstance(Locale.US)
        if (sort == "time") {
            sortParam = format.format(track.minutesPlayed)
            sortParamName = "minutes"
            otherParam = format.format(track.streamCount)
            otherParamName = "streams"
        } else {
            sortParam = format.format(track.streamCount)
            sortParamName = "streams"
            otherParam = format.format(track.minutesPlayed)
            otherParamName = "minutes"
        }
        Column(
            Modifier.weight(2.2f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.End
        ) {
            Row(
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = sortParam,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    textAlign = TextAlign.End,
                    lineHeight = 18.sp
                )
            }
            Row(
                modifier = Modifier.padding(0.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = sortParamName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    textAlign = TextAlign.End,
                    lineHeight = 12.sp
                )
            }
            Row(
                modifier = Modifier.padding(0.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "$otherParam $otherParamName",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    textAlign = TextAlign.End,
                    lineHeight = 12.sp,
                    modifier = Modifier.alpha(0.5F)
                )
            }
        }
    }
}