package com.github.barmiro.syshclient.presentation.top

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.barmiro.syshclient.data.top.dto.TrackDTO

@Composable
fun TrackItem(
    index: Int,
    track: TrackDTO,
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
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
            }
        }
        Column(
            modifier = Modifier.weight(8f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = track.name,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier.weight(5f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = track.spotify_track_id,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 14.sp
            )
        }
        Column(
            Modifier.weight(3f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.End
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = track.sort_param.toString(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    textAlign = TextAlign.End
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "streams",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    textAlign = TextAlign.End
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = (track.sort_param * 3).toString() + " minutes",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    textAlign = TextAlign.End
                )
            }
        }
    }
}