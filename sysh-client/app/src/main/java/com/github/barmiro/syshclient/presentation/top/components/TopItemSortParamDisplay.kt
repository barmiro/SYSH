package com.github.barmiro.syshclient.presentation.top.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.util.Locale

@Composable
fun TopItemSortParamDisplay(sort: String?,
                            minutesPlayed: Int,
                            streamCount: Int) {
    val sortParam: String
    val sortParamName: String
    val otherParam: String
    val otherParamName: String
    val format = NumberFormat.getInstance(Locale.US)
    if (sort == "time") {
        sortParam = format.format(minutesPlayed)
        sortParamName = "minutes"
        otherParam = format.format(streamCount)
        otherParamName = "streams"
    } else {
        sortParam = format.format(streamCount)
        sortParamName = "streams"
        otherParam = format.format(minutesPlayed)
        otherParamName = "minutes"
    }
    Column(
        modifier = Modifier.padding(start = 4.dp),
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