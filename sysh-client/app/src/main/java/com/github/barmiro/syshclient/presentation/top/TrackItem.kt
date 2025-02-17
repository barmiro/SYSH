package com.github.barmiro.syshclient.presentation.top

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.github.barmiro.syshclient.data.top.dto.TrackDTO

@Composable
fun TrackItem(
    index: Int,
    track: TrackDTO,
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
                model = track.thumbnail_url,
                contentDescription = "thumbnail for track " + track.name,
                modifier = Modifier.height(50.dp).width(50.dp)
            )
        }
        Column(
            modifier = Modifier.weight(5f).padding(horizontal = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = track.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier.weight(5f)
                )
            }
//            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = track.artist_names,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 12.sp,
                lineHeight = 12.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
            Text(
                text = track.album_name,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 12.sp,
                lineHeight = 12.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
        val sortParam: String
        val sortParamName: String
        val otherParam: String
        val otherParamName: String
        if (sort == "time") {
            sortParam = track.minutes_played.toString()
            sortParamName = "minutes"
            otherParam = track.stream_count.toString()
            otherParamName = "streams"
        } else {
            sortParam = track.stream_count.toString()
            sortParamName = "streams"
            otherParam = track.minutes_played.toString()
            otherParamName = "minutes"
        }
        Column(
            Modifier.weight(2f),
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
                    lineHeight = 12.sp
                )
            }
        }
    }
}