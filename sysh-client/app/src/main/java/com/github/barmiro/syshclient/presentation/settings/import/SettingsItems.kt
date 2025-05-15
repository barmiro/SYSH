package com.github.barmiro.syshclient.presentation.settings.import

import android.content.Context
import android.content.Intent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.github.barmiro.syshclient.R
import com.github.barmiro.syshclient.data.settings.dataimport.FileStatus
import com.github.barmiro.syshclient.data.settings.dataimport.UploadStatus
import com.github.barmiro.syshclient.presentation.startup.UrlInfoItem

@Composable
fun JsonFileUploadItem(
    index: Int,
    fileStatus: FileStatus,
    modifier: Modifier
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
                Text(
                    text = "File $index",
                    modifier = Modifier.alpha(0.5f)
                )
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
                        text = fileStatus.file.name
                            .substringAfter("Streaming_History_Audio_")
                            .substringBefore("_"),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                }
            }
            Column(
                modifier = Modifier.weight(2f),
                horizontalAlignment = Alignment.End
            ) {
                Row(
                    modifier = Modifier.animateContentSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    when (fileStatus.status) {
                        is UploadStatus.Waiting -> {
                            Text(
                                text = "Waiting",
                                modifier = Modifier.alpha(0.5f),
                                color = MaterialTheme.colorScheme.onBackground,
                                fontStyle = FontStyle.Italic,
                                lineHeight = 48.sp
                            )
                        }
                        is UploadStatus.Processing -> {
                            Text(
                                text = "Processing",
                                modifier = Modifier.alpha(0.5f),
                                color = MaterialTheme.colorScheme.onBackground,
                                fontStyle = FontStyle.Italic,
                                lineHeight = 48.sp
                            )
                            CircularProgressIndicator(modifier = Modifier.size(42.dp).padding(8.dp),
                                strokeCap = StrokeCap.Round)
                        }
                        is UploadStatus.Success -> {
                            Text(
                                text = fileStatus.status.message?.let{ streams ->
                                    "$streams entries"
                                } ?: "File processed, but no message from server",
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 16.sp,
                                lineHeight = 48.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(end = 4.dp)
                            )
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
                        }
                        is UploadStatus.Failed -> {
                            Text(
                                text = fileStatus.status.message ?: "File upload failed",
                                color = MaterialTheme.colorScheme.onBackground,
                                lineHeight = 48.sp
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun SettingsItem(
    itemText: String,
    icon: @Composable () -> Unit,
    modifier: Modifier
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
                icon()
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
                        text = itemText,
//                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                }
            }
            Column(
                modifier = Modifier.weight(0.15f),
                horizontalAlignment = Alignment.End
            ) {
                Row(
                    modifier = Modifier.animateContentSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Go",
                        modifier = Modifier.size(24.dp).alpha(0.8f)

                    )
                }
            }
        }
    }
}

@Composable
fun CreateUserItem(
    itemText: String,
    icon: @Composable () -> Unit,
    onCreateUser: (String, String, String) -> Unit,
    modifier: Modifier,
    resultString: String?
) {
    var isExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.animateContentSize(),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Column() {
            Box(modifier = Modifier.clickable(onClick = {
                isExpanded = !isExpanded
            })) {

                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        icon()
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
                                text = itemText,
//                        fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onBackground,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )
                        }
                    }
                    Column(
                        modifier = Modifier.weight(0.15f),
                        horizontalAlignment = Alignment.End
                    ) {
                        Row(
                            modifier = Modifier.animateContentSize(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RotatingArrowIcon(isExpanded)
                        }
                    }
                }
            }

            if (isExpanded) {
                val username = remember {
                    mutableStateOf(TextFieldValue())
                }
                val password = remember {
                    mutableStateOf(TextFieldValue())
                }

                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(value = username.value,
                        onValueChange = { username.value = it },
                        label = {
                            Text("Username")
                        }
                    )
                }

                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(value = password.value,
                        onValueChange = { password.value = it },
                        label = {
                            Text("Password")
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation = PasswordVisualTransformation()
                    )

                }

                var userRole by remember { mutableStateOf("USER") }
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Make admin ")
                    Switch(
                        checked = userRole == "ADMIN",
                        onCheckedChange = {
                            userRole = when {
                                userRole == "USER" -> "ADMIN"
                                else -> "USER"
                            }
                        }
                    )
                }


                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            onCreateUser(username.value.text, password.value.text, userRole)
                        },
                        enabled = username.value.text.isNotEmpty() && password.value.text.isNotEmpty()
                    ) {
                        Text("Create User")
                    }
                    resultString?.let {
                        Text(it)
                    }
                }
            }
        }
    }
}

@Composable
fun ManageUserItem(
    itemText: String,
    icon: @Composable () -> Unit,
    onDeleteUser: () -> Unit,
    modifier: Modifier,
    isCurrentUser: Boolean = false
) {
    var isExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.animateContentSize(),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Column() {
            Box(modifier = Modifier.clickable(onClick = {
                isExpanded = !isExpanded
            })) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        icon()
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
                                text = if (isCurrentUser) "$itemText (You)" else itemText,
//                        fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onBackground,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )
                        }
                    }
                    Column(
                        modifier = Modifier.weight(0.15f),
                        horizontalAlignment = Alignment.End
                    ) {
                        Row(
                            modifier = Modifier.animateContentSize(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RotatingArrowIcon(isExpanded)
                        }
                    }
                }
            }
            if (isExpanded) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            onDeleteUser()
                            isExpanded = false
                        },
                        enabled = !isCurrentUser
                    ) {
                        Text("Delete user")
                    }
                }
            }

        }
    }
}

@Composable
fun RotatingArrowIcon(isExpanded: Boolean) {
    val rotation by animateFloatAsState(
        targetValue = if (isExpanded) -180f else 0f,
        label = "arrowRotation"
    )

    Icon(
        imageVector = Icons.Default.KeyboardArrowDown, // a down-facing chevron
        contentDescription = "Expand",
        modifier = Modifier.rotate(rotation).size(24.dp).alpha(0.8f)
    )
}

@Composable
fun ChangePasswordItem(
    itemText: String,
    icon: @Composable () -> Unit,
    onChangePassword: (String, String) -> Unit,
    modifier: Modifier,
    passwordChangeResult: Boolean?,
    onClick: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    Surface(
        modifier = modifier.animateContentSize(),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Column() {
            Box(modifier = Modifier.clickable(onClick = {
                isExpanded = !isExpanded
                onClick()
            })) {

                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        icon()
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
                                text = itemText,
//                        fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onBackground,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )
                        }
                    }
                    Column(
                        modifier = Modifier.weight(0.15f),
                        horizontalAlignment = Alignment.End
                    ) {
                        Row(
                            modifier = Modifier.animateContentSize(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RotatingArrowIcon(isExpanded)
                        }
                    }
                }
            }

            if (isExpanded) {
                val oldPassword = remember {
                    mutableStateOf(TextFieldValue())
                }
                val newPassword = remember {
                    mutableStateOf(TextFieldValue())
                }
                val confirmation = remember {
                    mutableStateOf(TextFieldValue())
                }

                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(value = oldPassword.value,
                        onValueChange = { oldPassword.value = it },
                        label = {
                            Text("Old password")
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation = PasswordVisualTransformation()
                    )
                }

                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(value = newPassword.value,
                        onValueChange = { newPassword.value = it },
                        label = {
                            Text("New password")
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation = PasswordVisualTransformation()
                    )

                }

                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(value = confirmation.value,
                        onValueChange = { confirmation.value = it },
                        label = {
                            Text("Confirm new password")
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation = PasswordVisualTransformation(),
                        isError = newPassword.value.text != confirmation.value.text
                    )
                }


                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            onChangePassword(oldPassword.value.text, newPassword.value.text)
                        },
                        enabled = newPassword.value.text == confirmation.value.text
                                && newPassword.value.text.isNotEmpty()
                                && oldPassword.value.text.isNotEmpty()
                    ) {
                        Text("Change Password")
                    }
                }
                if (newPassword.value.text != confirmation.value.text) {
                    UrlInfoItem(
                        icon = {
                            Icon(imageVector = Icons.Default.Close,
                                tint = MaterialTheme.colorScheme.error,
                                contentDescription = "Error")
                        },
                        text = "Passwords do not match"
                    )
                }
                passwordChangeResult?.let { changed ->
                    Row() {
                        if (changed) {
                            UrlInfoItem(
                                icon = {
                                    Icon(imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Success",
                                        tint = Color(
                                            red = 52,
                                            green = 178,
                                            blue = 51
                                        ))
                                },
                                text = "Password changed successfully"
                            )
                        } else {
                            UrlInfoItem(
                                icon = {
                                    Icon(imageVector = Icons.Default.Close,
                                        tint = MaterialTheme.colorScheme.error,
                                        contentDescription = "Error")
                                },
                                text = "Password change failed"
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun AppPreferencesItem(
    itemText: String,
    icon: @Composable () -> Unit,
    onChangeDisplayName: () -> Unit,
    isUsernameDisplayed: Boolean,
    modifier: Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    Surface(
        modifier = modifier.animateContentSize(),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Column() {
            Box(modifier = Modifier.clickable(onClick = {
                isExpanded = !isExpanded
            })) {

                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        icon()
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
                                text = itemText,
//                        fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onBackground,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )
                        }
                    }
                    Column(
                        modifier = Modifier.weight(0.15f),
                        horizontalAlignment = Alignment.End
                    ) {
                        Row(
                            modifier = Modifier.animateContentSize(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RotatingArrowIcon(isExpanded)
                        }
                    }
                }
            }

            if (isExpanded) {
                Row() {
                    Text("Display SYSH username on Home Screen")
                    Switch(
                        checked = isUsernameDisplayed,
                        onCheckedChange = {
                            onChangeDisplayName()
                        }
                    )
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
                    Modifier.height(72.dp).padding(horizontal = 10.dp)
                )
//                Spacer(Modifier.width(16.dp))
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


@Composable
fun ImportFileStatusItem(zipFileStatus: FileStatus,
                         totalStreams: Int?,
                         restartApp: () -> Unit) {


    Surface(
        modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Column(
            modifier = Modifier.padding(8.dp).fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp),
            ) {
                Text(
                    text = "Importing archive",
                    fontSize = 24.sp
                )
            }
            Row(
                modifier = Modifier.padding(horizontal = 8.dp),
            ) {
                Text(
                    text = zipFileStatus.file.name,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.alpha(0.5f)
                )
            }
            Row(
                modifier = Modifier.padding(end = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val archiveText: String
                when (zipFileStatus.status) {
                    is UploadStatus.Waiting -> {
                        CircularProgressIndicator(modifier = Modifier.size(42.dp).padding(8.dp),
                            strokeCap = StrokeCap.Round)
                        archiveText = "Processing... "
                    }
                    is UploadStatus.Processing -> {
                        CircularProgressIndicator(modifier = Modifier.size(42.dp).padding(8.dp),
                            strokeCap = StrokeCap.Round)
                        archiveText = "Finalizing..."
                    }
                    is UploadStatus.Success -> {
                        Spacer(modifier = Modifier.width(8.dp))
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
                        Spacer(modifier = Modifier.width(4.dp))
                        archiveText = "Imported $totalStreams entries"
                    }
                    is UploadStatus.Failed -> {
                        archiveText = "Something went wrong"
                    }
                }

                Text(
                    text = archiveText
                )
            }
            if (zipFileStatus.status is UploadStatus.Success) {
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
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    titleText: String,
    bodyText: String? = null,
    confirmText: String
) {
    BasicAlertDialog(
        onDismissRequest = { onDismiss() },
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardColors(
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(vertical = 24.dp, horizontal = 16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = titleText,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                bodyText?.let {
                    Text(
                        text = it,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    TextButton(
                        onClick = { onDismiss() },
                        modifier = Modifier.padding(horizontal = 8.dp),
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = { onConfirm() },
                        modifier = Modifier.padding(horizontal = 8.dp),
                    ) {
                        Text(confirmText)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoDialog(
    onDismiss: () -> Unit,
    titleText: String,
    bodyText: String? = null,
    dismissText: String
) {
    BasicAlertDialog(
        onDismissRequest = { onDismiss() },
        modifier = Modifier.fillMaxWidth()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardColors(
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(vertical = 24.dp, horizontal = 16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = titleText,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                bodyText?.let {
                    Text(
                        text = it,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Button(
                        onClick = { onDismiss() },
                        modifier = Modifier.padding(horizontal = 8.dp),
                    ) {
                        Text(dismissText)
                    }
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