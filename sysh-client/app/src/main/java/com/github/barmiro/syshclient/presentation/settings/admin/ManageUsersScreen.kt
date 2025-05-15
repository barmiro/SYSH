package com.github.barmiro.syshclient.presentation.settings.admin

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.barmiro.syshclient.data.common.startup.UserDataDTO
import com.github.barmiro.syshclient.presentation.login.SessionViewModel
import com.github.barmiro.syshclient.presentation.settings.import.ConfirmDialog
import com.github.barmiro.syshclient.presentation.settings.import.CreateUserItem
import com.github.barmiro.syshclient.presentation.settings.import.ManageUserItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageUsersScreen(
    adminVM: AdminViewModel,
    sessionVM: SessionViewModel
) {

    val username by sessionVM.username.collectAsState()
    LaunchedEffect(Unit) {
        adminVM.getUsers()
    }

    val usernameList by adminVM.usernameList.collectAsState()
    val userCreationString by adminVM.userCreationString.collectAsState()
    var openAlertDialog by remember { mutableStateOf(false) }
    var selectedUser by remember { mutableStateOf<UserDataDTO?>(null) }
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage users") },
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
        if (openAlertDialog) {
            ConfirmDialog(
                onDismiss = {
                    openAlertDialog = false
                    selectedUser = null
                },
                onConfirm = {
                    selectedUser?.let {
                        adminVM.deleteUser(it.username)
                        selectedUser = null
                        openAlertDialog = false
                    }
                },
                titleText = "Delete user ${selectedUser?.username}?",
                bodyText = "This action cannot be undone.",
                confirmText = "Delete"
            )
        }
        Row(modifier = Modifier.fillMaxSize().padding(top = innerPadding.calculateTopPadding()),
            horizontalArrangement = Arrangement.Center) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item() {
                    CreateUserItem(
                        itemText = "Create User",
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Add,
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = "Create User"
                            )
                        },
                        onCreateUser = { username, password, role ->
                            adminVM.createUser(username, password, role)
                        },
                        modifier = Modifier
                            .padding(top = 8.dp, start = 8.dp, end = 8.dp),
                        resultString = userCreationString
                    )
                }


                usernameList?.let {
                    items(it.size) { index ->
                        ManageUserItem(
                            itemText = it[index].username,
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    tint = MaterialTheme.colorScheme.primary,
                                    contentDescription = "Icon for user ${it[index].username}"
                                )
                            },
                            onDeleteUser = {
                                selectedUser = it[index]
                                openAlertDialog = true
                            },
                            modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp),
                            isCurrentUser = it[index].username == username

                        )
                        }
                    }

                item() {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }


}