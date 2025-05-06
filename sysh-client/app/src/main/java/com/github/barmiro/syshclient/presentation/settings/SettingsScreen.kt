package com.github.barmiro.syshclient.presentation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.github.barmiro.syshclient.presentation.common.Import
import com.github.barmiro.syshclient.presentation.common.ManageUsers
import com.github.barmiro.syshclient.presentation.login.SessionViewModel
import com.github.barmiro.syshclient.presentation.settings.import.SettingsItem
import com.github.barmiro.syshclient.presentation.settings.import.SettingsScreenUserItem
import com.github.barmiro.syshclient.presentation.top.components.SettingsScreenTopBar

@Composable
fun SettingsScreen(
    sessionVM: SessionViewModel,
    navController: NavHostController
) {

    val username by sessionVM.username.collectAsState()
    val displayName by sessionVM.userDisplayName.collectAsState()
    val userImageUrl by sessionVM.userImageUrl.collectAsState()
    val userRole by sessionVM.userRole.collectAsState()
    val isDemoVersion by sessionVM.isDemoVersion.collectAsState()
    var isLoading by remember { mutableStateOf(false) }

    if (isLoading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(modifier = Modifier.size(48.dp))
        }
    } else {
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
                    if (isDemoVersion == true) {
                        item() {
                            Row() {
                                SettingsItem(
                                    itemText = "Leave demo mode",
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
                                            onClick = {
                                                sessionVM.clearAllPreferences()
                                            }
                                        )
                                )
                            }
                        }
                    } else {
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
                                                isLoading = true
                                                sessionVM.logout()
                                            }
                                        )
                                )
                            }
                        }

                        if(userRole == "ADMIN") {
                            item() {
                                Spacer(modifier = Modifier.height(16.dp))
                                Row() {
                                    Text(
                                        text = "Admin panel",
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 24.sp
                                    )
                                }
                                Row() {
                                    SettingsItem(
                                        itemText = "Manage users",
                                        icon = {
                                            Icon(
                                                imageVector = Icons.Default.AccountBox,
                                                tint = MaterialTheme.colorScheme.primary,
                                                contentDescription = "Manage users"
                                            )
                                        },
                                        modifier = Modifier
                                            .padding(top = 8.dp, start = 8.dp, end = 8.dp)
                                            .clickable(
                                                onClick = {
                                                    navController.navigate(ManageUsers)
                                                }
                                            )
                                    )
                                }
                                Row() {
                                    SettingsItem(
                                        itemText = "About server",
                                        icon = {
                                            Icon(
                                                imageVector = Icons.Default.Info,
                                                tint = MaterialTheme.colorScheme.primary,
                                                contentDescription = "About server"
                                            )
                                        },
                                        modifier = Modifier
                                            .padding(top = 8.dp, start = 8.dp, end = 8.dp)
                                            .clickable(
                                                onClick = {
//                                        TODO
                                                }
                                            )
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

    }

}

