package com.github.barmiro.syshclient.presentation.top.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.barmiro.syshclient.presentation.top.TopScreenState
import kotlinx.coroutines.delay

@Composable
fun TopScreenTopText(
    state: TopScreenState) {
    var oldRangeText by remember { mutableStateOf("") }
    var oldSortText by remember { mutableStateOf("") }
    var targetRangeText by remember { mutableStateOf("") }
    var targetSortText by remember { mutableStateOf("") }
    val delayBase = 150L

    val rangeText = when (state.dateRangeMode) {
        null -> "All time"
        "yearly" -> "Yearly"
        "monthly" -> "Monthly"
        "custom" -> "Custom date range"
        else -> "Invalid date range mode"
    }

    var sortText = "by stream count"
    if (state.sort == "time") {
        sortText = "by listening time"
    }

    LaunchedEffect(rangeText) {
        targetRangeText = oldRangeText
        for(i in 1..targetRangeText.length) {
            targetRangeText = targetRangeText.dropLast(1)
            delay(delayBase / oldRangeText.length)
        }
        delay(delayBase)
        for(i in 1..rangeText.length) {
            targetRangeText += rangeText[i - 1]
            delay(delayBase / rangeText.length)
        }
        oldRangeText = rangeText
    }

    LaunchedEffect(sortText) {
        targetSortText = oldSortText
        for(i in 1..targetSortText.length) {
            targetSortText = targetSortText.dropLast(1)
            delay(delayBase / oldSortText.length)
        }
        delay(delayBase)
        for(i in 1..sortText.length) {
            targetSortText += sortText[i - 1]
            delay(delayBase / sortText.length)
        }
        oldSortText = sortText
    }

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Bottom, horizontalAlignment = Alignment.CenterHorizontally) {
        Row (Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text(text = targetRangeText, fontSize = 14.sp, lineHeight = 14.sp, maxLines = 1, modifier = Modifier.padding(0.dp)
                .animateContentSize(spring(1f, 3000f)))
            Text(text = " • ", fontSize = 14.sp, lineHeight = 14.sp, maxLines = 1, modifier = Modifier.padding(0.dp))
            Text(text = targetSortText, fontSize = 14.sp, lineHeight = 14.sp, maxLines = 1, modifier = Modifier.alpha(0.5f).padding(0.dp)
                .animateContentSize(spring(1f, 3000f)))
        }
    }
}

@Composable
fun StatsScreenTopText(
    state: TopScreenState) {
    var oldRangeText by remember { mutableStateOf("") }
    var targetRangeText by remember { mutableStateOf("") }
    val delayBase = 150L

    val rangeText = when (state.dateRangeMode) {
        null -> "All time"
        "yearly" -> "Yearly"
        "monthly" -> "Monthly"
        "custom" -> "Custom date range"
        else -> "Invalid date range mode"
    }


    LaunchedEffect(rangeText) {
        targetRangeText = oldRangeText
        for(i in 1..targetRangeText.length) {
            targetRangeText = targetRangeText.dropLast(1)
            delay(delayBase / oldRangeText.length)
        }
        delay(delayBase)
        for(i in 1..rangeText.length) {
            targetRangeText += rangeText[i - 1]
            delay(delayBase / rangeText.length)
        }
        oldRangeText = rangeText
    }

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Bottom, horizontalAlignment = Alignment.CenterHorizontally) {
        Row (Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text(text = targetRangeText, fontSize = 14.sp, lineHeight = 14.sp, maxLines = 1, modifier = Modifier.padding(0.dp)
                .animateContentSize(spring(1f, 3000f)))
        }
    }
}

@Composable
fun HomeScreenTopText(
    state: State<String?>
) {
    var oldRangeText by remember { mutableStateOf("") }
    var targetRangeText by remember { mutableStateOf("") }
    val delayBase = 150L


    LaunchedEffect(state.value) {
        targetRangeText = oldRangeText
        for(i in 1..targetRangeText.length) {
            targetRangeText = targetRangeText.dropLast(1)
            delay(delayBase / oldRangeText.length)
        }
        delay(delayBase)
        state.value?.let {
            for(i in 1..it.length) {
                targetRangeText += it[i - 1]
                delay(delayBase / it.length)
            }
            oldRangeText = it
        }
    }

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Bottom, horizontalAlignment = Alignment.CenterHorizontally) {
        Row (Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text(text = targetRangeText, fontSize = 14.sp, lineHeight = 14.sp, maxLines = 1, modifier = Modifier.padding(0.dp)
                .animateContentSize(spring(1f, 3000f)))
        }
    }
}