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
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.github.barmiro.syshclient.presentation.common.Import
import com.github.barmiro.syshclient.presentation.common.ManageUsers
import com.github.barmiro.syshclient.presentation.login.SessionViewModel
import com.github.barmiro.syshclient.presentation.settings.import.AppPreferencesItem
import com.github.barmiro.syshclient.presentation.settings.import.ChangePasswordItem
import com.github.barmiro.syshclient.presentation.settings.import.ConfirmDialog
import com.github.barmiro.syshclient.presentation.settings.import.SettingsItem
import com.github.barmiro.syshclient.presentation.settings.import.SettingsScreenUserItem
import com.github.barmiro.syshclient.presentation.startup.InfoItem
import com.github.barmiro.syshclient.presentation.top.components.SettingsScreenTopBar
import java.time.ZoneId

@Composable
fun SettingsScreen(
    sessionVM: SessionViewModel,
    settingsVM: SettingsViewModel,
    navController: NavHostController,
    restartApp: () -> Unit
) {

    val username by sessionVM.username.collectAsState()
    val displayName by sessionVM.userDisplayName.collectAsState()
    val userImageUrl by sessionVM.userImageUrl.collectAsState()
    val userRole by sessionVM.userRole.collectAsState()
    val isDemoVersion by sessionVM.isDemoVersion.collectAsState()
    val userTimezone by sessionVM.userTimezone.collectAsState()
    val isUsernameDisplayed by settingsVM.isUsernameDisplayed.collectAsState()
    val isPasswordChanged by settingsVM.isPasswordChanged.collectAsState()
    val isTimezoneChanged by settingsVM.isTimezoneChanged.collectAsState()
    val appTheme by settingsVM.appTheme.collectAsState()
    val isGradientEnabled by settingsVM.isGradientEnabled.collectAsState()

    var isLoading by remember { mutableStateOf(false) }
    var openLogoutAlertDialog by remember { mutableStateOf(false) }
    var openTimezoneAlertDialog by remember { mutableStateOf(false) }

    val systemTimezone = ZoneId.systemDefault().id
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
            if (openLogoutAlertDialog) {
                ConfirmDialog(
                    onDismiss = {
                        openLogoutAlertDialog = false
                    },
                    onConfirm = {
                        openLogoutAlertDialog = false
                        isLoading = true
                        sessionVM.logout(restartApp)

                    },
                    titleText = "Are you sure?",
                    confirmText = "Log Out"
                )
            }
            if (openTimezoneAlertDialog) {
                ConfirmDialog(
                    onDismiss = {
                        openTimezoneAlertDialog = false
                    },
                    onConfirm = {
                        openTimezoneAlertDialog = false
                        settingsVM.updateTimezone(systemTimezone)
                    },
                    titleText = "Update timezone to $systemTimezone?",
                    bodyText = "Your current timezone is $userTimezone.",
                    confirmText = "Change timezone"
                )
            }
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
                                    itemText = "Import Streaming Data [DEMO]",
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
                            AppPreferencesItem(
                                itemText = "App Preferences",
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        tint = MaterialTheme.colorScheme.primary,
                                        contentDescription = "App Preferences"
                                    )
                                },
                                modifier = Modifier
                                    .padding(top = 8.dp, start = 8.dp, end = 8.dp),
                                isUsernameDisplayed = isUsernameDisplayed ?: false,
                                onChangeDisplayName = {
                                    settingsVM.setIsUsernameDisplayed(!(isUsernameDisplayed ?: true))
                                },
                                selectedAppTheme = appTheme,
                                onChangeAppTheme = { settingsVM.changeAppTheme(it) },
                                isGradientEnabled = isGradientEnabled,
                                onSetIsGradientEnabled = { settingsVM.setIsGradientEnabled(!isGradientEnabled) }
                            )
                        }
                        item() {
                            Row() {
                                SettingsItem(
                                    itemText = "Leave Demo Mode",
                                    icon = {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                            tint = MaterialTheme.colorScheme.primary,
                                            contentDescription = "Leave demo mode"
                                        )
                                    },
                                    modifier = Modifier
                                        .padding(top = 8.dp, start = 8.dp, end = 8.dp)
                                        .clickable(
                                            onClick = {
                                                isLoading = true
                                                sessionVM.clearAllPreferences(restartApp)
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
                            ChangePasswordItem(
                                itemText = "Change Password",
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        tint = MaterialTheme.colorScheme.primary,
                                        contentDescription = "Change Password"
                                    )
                                },
                                onChangePassword = { oldPassword, newPassword ->
                                    settingsVM.changePassword(oldPassword, newPassword)
                                },
                                modifier = Modifier
                                    .padding(top = 8.dp, start = 8.dp, end = 8.dp),
                                passwordChangeResult = isPasswordChanged,
                                onClick = { settingsVM.onChangePasswordReset() }
                            )
                        }

                        item() {
                            AppPreferencesItem(
                                itemText = "App Preferences",
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        tint = MaterialTheme.colorScheme.primary,
                                        contentDescription = "App Preferences"
                                    )
                                },
                                modifier = Modifier
                                    .padding(top = 8.dp, start = 8.dp, end = 8.dp),
                                isUsernameDisplayed = isUsernameDisplayed ?: false,
                                onChangeDisplayName = {
                                    settingsVM.setIsUsernameDisplayed(!(isUsernameDisplayed ?: true))
                                },
                                selectedAppTheme = appTheme,
                                onChangeAppTheme = { settingsVM.changeAppTheme(it) },
                                isGradientEnabled = isGradientEnabled,
                                onSetIsGradientEnabled = { settingsVM.setIsGradientEnabled(!isGradientEnabled) }
                            )
                        }

                        if (systemTimezone != userTimezone && isTimezoneChanged != true) {
                            item() {
                                Row() {
                                    SettingsItem(
                                        itemText = "Update timezone",
                                        icon = {
                                            Icon(
                                                imageVector = Icons.Default.LocationOn,
                                                tint = MaterialTheme.colorScheme.primary,
                                                contentDescription = "Update timezone"
                                            )
                                        },
                                        modifier = Modifier
                                            .padding(top = 8.dp, start = 8.dp, end = 8.dp)
                                            .clickable(
                                                onClick = { openTimezoneAlertDialog = true }
                                            )
                                    )
                                }
                            }
                        } else if (isTimezoneChanged == true) {
                            item() {
                                Row() {
                                    InfoItem(
                                        icon = {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Success",
                                                tint = Color(
                                                    red = 52,
                                                    green = 178,
                                                    blue = 51
                                                ),
                                                modifier = Modifier.size(18.dp).alpha(0.8f)
                                            )
                                        },
                                        title = "Timezone updated",
                                        text = "It may take a couple of minutes for changes to take effect.",
                                        lineHeight = 16.sp
                                    )
                                }
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
                                            onClick = { openLogoutAlertDialog = true }
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

