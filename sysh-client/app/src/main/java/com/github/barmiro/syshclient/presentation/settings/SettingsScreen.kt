package com.github.barmiro.syshclient.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.barmiro.syshclient.presentation.top.components.SettingsScreenTopBar

@Composable
fun SettingsScreen(
    settingsVM: SettingsViewModel,
    onPickZipFile: () -> Unit
) {

    val statusList by settingsVM.fileStatusList.collectAsState()

    Scaffold(
        topBar = {
            SettingsScreenTopBar(
                titleText = "Settings",
                actions = {
                    IconButton(
                        onClick = {
                        }
                    ) {
                    }
                }
            )
        }
    ) { innerPadding ->
        Row(modifier = Modifier.fillMaxSize().padding(top = innerPadding.calculateTopPadding()),
            horizontalArrangement = Arrangement.Center) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item() {
                    Row() {
                        Button(
                            onClick = onPickZipFile
                        ) {
                            Text(
                                text = "Import Spotify Extended Streaming History"
                            )
                        }
                    }
                }
                items(statusList.size) { i ->
                    JsonFileUploadItem(
                        i + 1,
                        statusList[i],
                        Modifier.padding(8.dp))
                }
                item() {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }


    }

}



