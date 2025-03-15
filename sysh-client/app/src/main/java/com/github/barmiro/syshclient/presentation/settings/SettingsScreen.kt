package com.github.barmiro.syshclient.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    settingsVM: SettingsViewModel,
    onPickZipFile: () -> Unit
) {

    Row(modifier = Modifier.fillMaxSize().padding(8.dp),
        horizontalArrangement = Arrangement.Center) {
        Column(modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally)
        {
            Button(
                onClick = onPickZipFile
            ) {
                Text(
                    text = "Import Spotify Extended Streaming History"
                )
            }
        }
    }
}