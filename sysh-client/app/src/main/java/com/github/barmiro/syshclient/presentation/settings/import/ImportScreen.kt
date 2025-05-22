package com.github.barmiro.syshclient.presentation.settings.import

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.github.barmiro.syshclient.R
import com.github.barmiro.syshclient.data.settings.dataimport.UploadStatus
import com.github.barmiro.syshclient.presentation.settings.ImportViewModel
import com.github.barmiro.syshclient.presentation.startup.InfoItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportScreen(
    importVM: ImportViewModel,
    onPickZipFile: () -> Unit,
    restartApp: () -> Unit
) {

    val statusList by importVM.fileStatusList.collectAsState()
    val zipFileStatus by importVM.zipFileStatus.collectAsState()
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    var openAlertDialog by remember { mutableStateOf(false) }

    val isImportInProgress = zipFileStatus?.status == UploadStatus.Waiting
            || zipFileStatus?.status == UploadStatus.Processing

    val importItemModifier = if (isImportInProgress) Modifier.alpha(0.7f)
            else Modifier.clickable(onClick = { onPickZipFile() })
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
                                tint = MaterialTheme.colorScheme.onBackground,
                                contentDescription = "Go back")
                        }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                ),
                actions = {
                    IconButton(
                        onClick = {
                            openAlertDialog = true
                        },
                        content = {
                            Icon(
                                painter = painterResource(R.drawable.help_24dp),
                                tint = MaterialTheme.colorScheme.onBackground,
                                contentDescription = "Help")
                        }
                    )
                }
            )
        }
    ) { innerPadding ->
        if (openAlertDialog) {
            InfoDialog(
                onDismiss = {
                    openAlertDialog = false
                },
                titleText = "What .zip file?",
                bodyText = "1. Go to spotify.com/account/privacy\n" +
                        "2. Scroll down to \"Download your data\"\n" +
                        "3. Select only \"Extended Streaming History\" and click \"Request data\"\n" +
                        "4. Confirm your request in the received email\n\n" +
                        "You should receive your data within a couple of days. Download the .zip archive and select it for import.",
                dismissText = "Got it!"
            )
        }
        Row(modifier = Modifier.fillMaxSize().padding(top = innerPadding.calculateTopPadding()),
            horizontalArrangement = Arrangement.Center) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item() {
                    Row() {
                        SettingsItem(
                            itemText = if (isImportInProgress) "Import in progress" else "Select .zip file",
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    tint = MaterialTheme.colorScheme.primary,
                                    contentDescription = "Add"
                                )
                            },
                            modifier = importItemModifier
                                .padding(top = 8.dp, start = 8.dp, end = 8.dp)

                        )
                    }
                }

                zipFileStatus?.let {
                    item() {
                        ImportFileStatusItem(
                            zipFileStatus = it,
                            totalStreams = totalStreams(statusList),
                            restartApp = { restartApp() }
                        )
                        InfoItem(
                            icon = {
                                Icon(imageVector = Icons.Default.Info,
                                    contentDescription = "Info",
                                    modifier = Modifier.alpha(0.8f))
                            },
                            title = "Each entry is a valid play event.",
                            text = "Plays shorter than 30 seconds don't count as streams, but are included in other statistics."
                        )
                    }
                }
                items(items = statusList, key = { it.file.name }) { item ->
                    val index = statusList.indexOf(item)
                    JsonFileUploadItem(
                        index + 1,
                        statusList[index],
                        Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp).animateItem(
                            placementSpec = spring()
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