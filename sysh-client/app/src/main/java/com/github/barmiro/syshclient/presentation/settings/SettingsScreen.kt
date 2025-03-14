package com.github.barmiro.syshclient.presentation.settings

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun SettingsScreen(
    settingsVM: SettingsViewModel,
    onPickZipFile: () -> Unit
) {

    Button(
        onClick = onPickZipFile
    ) {
        Text(
          text = "Import Spotify Extended Streaming History"
        )
    }
}