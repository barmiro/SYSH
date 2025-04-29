package com.github.barmiro.syshclient.presentation.settings

import android.content.Context
import android.content.Intent
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.github.barmiro.syshclient.R
import com.github.barmiro.syshclient.data.common.dataimport.FileStatus
import com.github.barmiro.syshclient.data.common.dataimport.UploadStatus
import com.github.barmiro.syshclient.presentation.common.Import
import com.github.barmiro.syshclient.presentation.login.SessionViewModel
import com.github.barmiro.syshclient.presentation.startup.InfoItem
import com.github.barmiro.syshclient.presentation.top.components.SettingsScreenTopBar

@Composable
fun SettingsScreen(
    settingsVM: SettingsViewModel,
    sessionVM: SessionViewModel,
    navController: NavHostController
) {

    val username by sessionVM.username.collectAsState()
    val displayName by sessionVM.userDisplayName.collectAsState()
    val userImageUrl by sessionVM.userImageUrl.collectAsState()

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
                    SettingsScreenUserItem(
                        username = username,
                        displayName = displayName,
                        userImageUrl = userImageUrl,
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp, start = 8.dp, end = 8.dp))
                }

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


@Composable
fun SettingsScreenUserItem(username: String?,
                           displayName: String?,
                           userImageUrl: String?,
                           modifier: Modifier) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = modifier,
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.secondaryContainer
        ) {
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    userImageUrl?.let {
                        AsyncImage(
                            model = it,
                            contentScale = ContentScale.Crop,
                            contentDescription = "User profile image",
                            modifier = Modifier.height(100.dp).width(100.dp)
                                .clip(RoundedCornerShape(120.dp))
                        )
                    } ?: Box(
                        modifier = Modifier.height(100.dp)
                            .width(100.dp)
                            .clip(RoundedCornerShape(120.dp))
                            .background(Color.Gray.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center) {
                        Icon(
                            painter = painterResource(id = R.drawable.person_2_24dp),
                            tint = IconButtonDefaults.iconButtonColors().contentColor,
                            contentDescription = "Sort icon",
                            modifier = Modifier.size(36.dp)
                        )
                    }

                }
                VerticalDivider(
                    Modifier.height(48.dp).padding(horizontal = 8.dp)
                )
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                    ) {
                        Text(
                            text = username ?: "unknown username",
//                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp,
                            color = MaterialTheme.colorScheme.onBackground,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                    }
                    displayName?.let {
                        Row(
                        ) {
                            Text(
                                text = "Spotify @$it",
//                        fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onBackground,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 2
                            )
                        }
                    }
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
    onPickZipFile: () -> Unit,
    restartApp: () -> Unit
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
                                tint = MaterialTheme.colorScheme.onBackground,
                                contentDescription = "Go back")
                        }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                ),
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
                        val archiveText: String = when (it.status) {
                            is UploadStatus.Waiting -> "Processing... "
                            is UploadStatus.Processing -> "Finalizing..."
                            is UploadStatus.Success -> "Imported ${totalStreams(statusList)} entries"
                            is UploadStatus.Failed -> "Something went wrong"
                        }

                        Surface(
                            modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp)
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Column() {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Importing archive: ${it.file.name}"
                                    )
                                }
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = archiveText
                                    )
                                }
                                if (it.status is UploadStatus.Success) {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Button(onClick = { restartApp() }) {
                                            Text("Reload app")
                                        }
                                    }
                                }

                            }
                        }
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

fun totalStreams(statusList: List<FileStatus>): Int {
    var sum = 0
    for (item in statusList) {
        if (item.status is UploadStatus.Success) {
            sum += item.status.message
        }
    }
    return sum
}

fun restartApp(context: Context) {
    val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
    intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
    Runtime.getRuntime().exit(0) // kill the current process
}

