package com.github.barmiro.syshclient.presentation.top.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp

@Composable
fun TopListIndexText(index: Int) {
    Box() {

        var indexFontSize = 22.sp

        if (index >= 1000) {
            indexFontSize = 12.sp
        } else if (index >= 100) {
            indexFontSize = 16.sp
        }

        var textSize by remember { mutableStateOf(indexFontSize) } // Initial font size



        Text(
            text = index.toString(),
            fontSize = textSize,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            softWrap = false,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Clip,
            onTextLayout = { textLayoutResult ->
                if (textLayoutResult.didOverflowWidth) {
                    textSize *= 0.9f // Reduce size by 10% if it overflows
                }
            }
        )
    }
}