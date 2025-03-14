package com.github.barmiro.syshclient.presentation.stats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GeneralStatsItem(
    itemValue: String,
    itemText: String,
    modifier: Modifier = Modifier,
    dateRangeMode: String? = null,
    perDayValue: String? = null
) {
    Surface(
        modifier = Modifier.padding(6.dp),
        shape = RoundedCornerShape(16.dp),
        color = NavigationBarDefaults.containerColor
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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
                    fontSize = 32.sp,
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
fun CollectionStatsItem(
    itemValue: String,
    itemText: String,
    modifier: Modifier = Modifier,
    dateRangeMode: String? = null,
    perDayValue: String? = null
) {
    Surface(
        modifier = Modifier.padding(6.dp),
        shape = RoundedCornerShape(16.dp),
        color = NavigationBarDefaults.containerColor
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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