package com.github.barmiro.syshclient.presentation.home

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import java.text.NumberFormat
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
fun HomeTopItem(
    itemLabel: String,
    itemName: String,
    imageUrl: String?,
    streamCount: Int?,
    minutesStreamed: Int?
) {


    Surface(
        modifier = Modifier.padding(5.dp),
        shape = RoundedCornerShape(6.dp),
        color = NavigationBarDefaults.containerColor
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
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "thumbnail for $itemName",
                    modifier = Modifier.height(100.dp).width(100.dp)
                        .clip(RoundedCornerShape(5.dp))
                )
            }
        }
    }
}