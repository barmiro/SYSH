package com.github.barmiro.syshclient.presentation.settings

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.github.barmiro.syshclient.data.common.dataimport.UploadStatus
import com.github.barmiro.syshclient.presentation.common.Import
import com.github.barmiro.syshclient.presentation.login.SessionViewModel
import com.github.barmiro.syshclient.presentation.top.components.SettingsScreenTopBar

@Composable
fun SettingsScreen(
    settingsVM: SettingsViewModel,
    sessionVM: SessionViewModel,
    onPickZipFile: () -> Unit,
    navController: NavHostController
) {

    Scaffold(
        topBar = {
            SettingsScreenTopBar(
                titleText = "Settings"
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
                        SettingsItem(
                            itemText = "Import Streaming Data",
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    tint = MaterialTheme.colorScheme.primary,
                                    contentDescription = "Add"
                                )
                            },
                            modifier = Modifier
                                .padding(top = 8.dp, start = 8.dp, end = 8.dp)
                                .clickable(
                                onClick = { navController.navigate(Import) }
                            )
                        )
                    }
                }
                item() {
                    Row() {
                        SettingsItem(
                            itemText = "Log Out",
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    tint = MaterialTheme.colorScheme.primary,
                                    contentDescription = "Log Out"
                                )
                            },
                            modifier = Modifier
                                .padding(top = 8.dp, start = 8.dp, end = 8.dp)
                                .clickable(
                                onClick = {
                                    sessionVM.logout()
                                }
                            )
                        )
                        Button(
                            onClick = {
                                sessionVM.logout()
                            }
                        ) {
                            Text(
                                text = "Log out"
                            )
                        }
                    }
                }

                item() {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportScreen(
    settingsVM: SettingsViewModel,
    sessionVM: SessionViewModel,
    onPickZipFile: () -> Unit
) {

    val statusList by settingsVM.fileStatusList.collectAsState()
    val zipFileStatus by settingsVM.zipFileStatus.collectAsState()
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Import streaming data") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            backDispatcher?.onBackPressed()
                        },
                        content = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.KeyboardArrowLeft,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                contentDescription = "Go back")
                        }
                    )
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
                        SettingsItem(
                            itemText = "Select .zip file",
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    tint = MaterialTheme.colorScheme.primary,
                                    contentDescription = "Add"
                                )
                            },
                            modifier = Modifier
                                .padding(top = 8.dp, start = 8.dp, end = 8.dp)
                                .clickable(
                                    onClick = onPickZipFile
                                )
                        )
                    }
                }
                zipFileStatus?.let {
                    item() {
                        Row() {
                            val archiveText: String = when (it.status) {
                                is UploadStatus.Waiting -> "Processing archive: ${it.file.name}"
                                is UploadStatus.Processing -> "Finalizing..."
                                is UploadStatus.Success -> "${it.file.name} imported successfully"
                                is UploadStatus.Failed -> "Something went wrong"
                            }
                            Text(
                                text = archiveText
                            )
                        }
                    }
                }
                items(items = statusList, key = { it.file.name }) {
                    val index = statusList.indexOf(it)
                    JsonFileUploadItem(
                        index + 1,
                        statusList[index],
                        Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp).animateItem(
                            placementSpec = spring(Spring.StiffnessLow)
                        ))
                }
//                items(statusList.size) { i ->
//                    JsonFileUploadItem(
//                        i + 1,
//                        statusList[i],
//                        Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp).animateItem())
//                }

                item() {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}



