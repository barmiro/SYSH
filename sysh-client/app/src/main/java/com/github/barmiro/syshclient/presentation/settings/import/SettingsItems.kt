package com.github.barmiro.syshclient.presentation.settings.import

import android.content.Context
import android.content.Intent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.github.barmiro.syshclient.R
import com.github.barmiro.syshclient.data.common.startup.FileProcessingStatus
import com.github.barmiro.syshclient.data.common.startup.ImportStatusDTO
import com.github.barmiro.syshclient.data.common.startup.JsonInfo
import com.github.barmiro.syshclient.data.common.startup.ZipUploadItem
import com.github.barmiro.syshclient.data.settings.dataimport.FileStatus
import com.github.barmiro.syshclient.data.settings.dataimport.UploadStatus
import com.github.barmiro.syshclient.presentation.login.canEncodeInput
import com.github.barmiro.syshclient.presentation.startup.UrlInfoItem
import com.github.barmiro.syshclient.util.AppTheme
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun JsonFileUploadItem(
    index: Int,
    jsonInfo: JsonInfo,
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
                        text = jsonInfo.filename
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
                    when (jsonInfo.status) {

                        FileProcessingStatus.WAITING -> {
                            Text(
                                text = "Waiting",
                                modifier = Modifier.alpha(0.5f),
                                color = MaterialTheme.colorScheme.onBackground,
                                fontStyle = FontStyle.Italic,
                            )
                        }
                        FileProcessingStatus.PREPARING -> {
                            Text(
                                text = "Preparing",
                                modifier = Modifier.alpha(0.5f),
                                color = MaterialTheme.colorScheme.onBackground,
                                fontStyle = FontStyle.Italic,
                            )
                        }

                        FileProcessingStatus.PROCESSING -> {
                            Text(
                                text = "Processing",
                                modifier = Modifier.alpha(0.5f),
                                color = MaterialTheme.colorScheme.onBackground,
                                fontStyle = FontStyle.Italic,
                            )
                            CircularProgressIndicator(modifier = Modifier.size(42.dp).padding(8.dp),
                                strokeCap = StrokeCap.Round)
                        }
                        FileProcessingStatus.FINALIZING -> {
                            Text(
                                text = "Finalizing",
                                modifier = Modifier.alpha(0.5f),
                                color = MaterialTheme.colorScheme.onBackground,
                                fontStyle = FontStyle.Italic,
                            )
                            CircularProgressIndicator(modifier = Modifier.size(42.dp).padding(8.dp),
                                strokeCap = StrokeCap.Round)
                        }
                        FileProcessingStatus.SUCCESS -> {
                            Text(
                                text = jsonInfo.entriesAdded?.let{ streams ->
                                    "$streams entries"
                                } ?: "File processed, but no message from server",
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 16.sp,
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
                        FileProcessingStatus.ERROR -> {
                            Text(
                                text = "File upload failed",
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Error",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(32.dp).padding(start = 8.dp)
                            )
                        }

                        else -> {}
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
    val newPasswordFieldFocusRequester = remember { FocusRequester() }
    val confirmationFieldFocusRequester = remember { FocusRequester() }
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

                val isInputValid = canEncodeInput(oldPassword.value.text)
                        && canEncodeInput(newPassword.value.text)
                        && canEncodeInput(confirmation.value.text)

                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(value = oldPassword.value,
                        onValueChange = { oldPassword.value = it },
                        label = {
                            Text("Old password")
                        },
                        modifier = Modifier.width(256.dp),
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next,
                            keyboardType = KeyboardType.Password
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = {
                                newPasswordFieldFocusRequester.requestFocus()
                            }
                        ),
                        singleLine = true,
                        maxLines = 1,
                        isError = !canEncodeInput(oldPassword.value.text)
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
                        modifier = Modifier
                            .width(256.dp)
                            .focusRequester(newPasswordFieldFocusRequester),
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next,
                            keyboardType = KeyboardType.Password
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = {
                                confirmationFieldFocusRequester.requestFocus()
                            }
                        ),
                        singleLine = true,
                        maxLines = 1,
                        isError = !canEncodeInput(newPassword.value.text)
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
                        modifier = Modifier
                            .width(256.dp)
                            .focusRequester(confirmationFieldFocusRequester),
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done,
                            keyboardType = KeyboardType.Password
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                onChangePassword(oldPassword.value.text, newPassword.value.text)
                            }
                        ),
                        singleLine = true,
                        maxLines = 1,
                        isError = newPassword.value.text != confirmation.value.text
                                && !canEncodeInput(confirmation.value.text)
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
                                && isInputValid
                    ) {
                        Text("Change Password")
                    }
                }
                if (!isInputValid) {
                    UrlInfoItem(
                        icon = {
                            Icon(imageVector = Icons.Default.Close,
                                tint = MaterialTheme.colorScheme.error,
                                contentDescription = "Error")
                        },
                        text = "Some fields contain invalid characters"
                    )
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
    modifier: Modifier,
    selectedAppTheme: AppTheme,
    onChangeAppTheme: (AppTheme) -> Unit
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
                HorizontalDivider(Modifier.fillMaxWidth())
                Row(Modifier.height(IntrinsicSize.Min)) {
                    Column(modifier = Modifier.weight(1f).padding(12.dp).fillMaxHeight(),
                        verticalArrangement = Arrangement.Center) {
                        Text(text = "Display SYSH username on Home Screen",
                            fontSize = 16.sp,
                            lineHeight = 18.sp

                        )

                    }
                    Column(modifier = Modifier.padding(top = 12.dp, bottom = 12.dp, end = 12.dp).fillMaxHeight(),
                        verticalArrangement = Arrangement.Center) {
                        Switch(
                            checked = isUsernameDisplayed,
                            onCheckedChange = {
                                onChangeDisplayName()
                            },
                        )
                    }
                }
                HorizontalDivider(Modifier.fillMaxWidth().padding(horizontal = 8.dp))

                var isAppThemeExpanded by remember { mutableStateOf(false) }
                Row(Modifier.height(IntrinsicSize.Min)) {
                    Column(modifier = Modifier.weight(1f).padding(12.dp).fillMaxHeight(),
                        verticalArrangement = Arrangement.Center) {
                        Text(text = "App theme",
                            fontSize = 16.sp,
                            lineHeight = 18.sp

                        )

                    }
                    Column(modifier = Modifier.padding(top = 12.dp, bottom = 12.dp, end = 12.dp).fillMaxHeight(),
                        verticalArrangement = Arrangement.Center) {
                        IconButton(onClick = { isAppThemeExpanded = !isAppThemeExpanded }) {
                            RotatingArrowIcon(isAppThemeExpanded)
                        }
                        DropdownMenu(
                            expanded = isAppThemeExpanded,
                            onDismissRequest = { isAppThemeExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("System default") },
                                onClick = {
                                    onChangeAppTheme(AppTheme.SYSTEM_DEFAULT)
                                    isAppThemeExpanded = false
                                },
                                enabled = selectedAppTheme != AppTheme.SYSTEM_DEFAULT
                            )
                            DropdownMenuItem(
                                text = { Text("Dark") },
                                onClick = {
                                    onChangeAppTheme(AppTheme.DARK)
                                    isAppThemeExpanded = false
                                },
                                enabled = selectedAppTheme != AppTheme.DARK
                            )
                            DropdownMenuItem(
                                text = { Text("Light") },
                                onClick = {
                                    onChangeAppTheme(AppTheme.LIGHT)
                                    isAppThemeExpanded = false
                                },
                                enabled = selectedAppTheme != AppTheme.LIGHT
                            )
                        }
                    }
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
fun ImportFileStatusItem(zipFile: ZipUploadItem,
                         totalStreams: Int?,
                         progress: Float?,
                         restartApp: () -> Unit) {

    val fluidProgress = updateTransition(progress).animateFloat(
        targetValueByState = { it ?: 0f }
    )


    Surface(
        modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth()
        ) {
            Row(
            ) {
                Text(
                    text = if (zipFile.status == FileProcessingStatus.COMPLETE) {
                        "Last import"
                    } else {
                        "Importing archive"
                    },
                    fontSize = 24.sp
                )
            }
            Row(
            ) {
                Text(
                    text = zipFile.zipName,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.alpha(0.5f)
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                val archiveText: String
                when (zipFile.status) {
                    FileProcessingStatus.WAITING -> {
                        archiveText = "Uploading... "
                    }
                    FileProcessingStatus.PREPARING -> {
                        archiveText = "Unpacking... "
                    }
                    FileProcessingStatus.PROCESSING -> {
                        archiveText = "Processing..."
                    }
                    FileProcessingStatus.FINALIZING -> {
                        archiveText = "Finalizing... "
                    }
                    FileProcessingStatus.SUCCESS -> {
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
                    FileProcessingStatus.ERROR -> {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(48.dp).padding(horizontal = 8.dp)
                        )
                        archiveText = "Import failed" // uploadStatusMessageParser(zipFileStatus.status.message, "Something went wrong")
                    }


                    FileProcessingStatus.COMPLETE -> {
                        val completedOn = zipFile.completedOn?.toLocalDateTime()
                        val dateString = DateTimeFormatter.ofPattern("MMMM dd, yyyy", Locale.US)
                            .format(completedOn)
                        val timeString = DateTimeFormatter.ofPattern("h:mm a", Locale.US)
                            .format(completedOn)
                        archiveText = "Completed on $dateString at $timeString"
                    }
                }

                Text(
                    text = archiveText
                )
            }
            if (zipFile.status == FileProcessingStatus.PROCESSING) {
                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LinearProgressIndicator(
                        progress = { fluidProgress.value },
                        trackColor = Color.Gray.copy(alpha = 0.2f),
                        drawStopIndicator = {},
                        modifier = Modifier.fillMaxWidth(),
                        gapSize = (-2).dp
                    )
                }
            } else if (
                    listOf(
                        FileProcessingStatus.WAITING,
                        FileProcessingStatus.PREPARING,
                        FileProcessingStatus.FINALIZING)
                        .contains(zipFile.status)
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LinearProgressIndicator(
                        trackColor = Color.Gray.copy(alpha = 0.2f),
                        modifier = Modifier.fillMaxWidth(),
                        gapSize = (-2).dp
                    )
                }
            }
            if (zipFile.status == FileProcessingStatus.SUCCESS) {
                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
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


@Composable
fun ImportProgressOverlay(importStatus: ImportStatusDTO?, restartApp: () -> Unit) {
    importStatus?.let { status ->
        status.zipUploadItem?.let { zipFile ->
            if (zipFile.status != FileProcessingStatus.COMPLETE) {
                val processingProgressDividend: Int? = importStatus.jsonInfoList?.filter {
                    listOf(FileProcessingStatus.SUCCESS, FileProcessingStatus.ERROR).contains(it.status)
                }?.size?.takeIf { it > 0}
                val processingProgressDivisor: Int? = importStatus.jsonInfoList?.size?.takeIf { it > 0}
                val processingProgress: Float? = processingProgressDividend?.let { dividend ->
                    processingProgressDivisor?.let { divisor ->
                        1f * dividend / divisor
                    }
                }

                val fluidProgress = updateTransition(processingProgress).animateFloat(
                    targetValueByState = { it ?: 0f }
                )
                Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Bottom) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        shape = RoundedCornerShape(6.dp),
                        colors = CardColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            contentColor = MaterialTheme.colorScheme.onBackground,
                            disabledContainerColor = MaterialTheme.colorScheme.background,
                            disabledContentColor = MaterialTheme.colorScheme.onBackground
                        ),
                        border = BorderStroke(width = 1.dp, MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.2f))
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            .animateContentSize()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                ) {
                                    Text(
                                        text = if (zipFile.status == FileProcessingStatus.SUCCESS) {
                                            "Import successful"
                                        } else {
                                            "Import in progress"
                                        }
                                    )
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val archiveText: String
                                    when (zipFile.status) {
                                        FileProcessingStatus.WAITING -> {
                                            archiveText = "Uploading... "
                                        }
                                        FileProcessingStatus.PREPARING -> {
                                            archiveText = "Unpacking... "
                                        }
                                        FileProcessingStatus.PROCESSING -> {
                                            archiveText = "Processing..."
                                        }
                                        FileProcessingStatus.FINALIZING -> {
                                            archiveText = "Finalizing... "
                                        }
                                        FileProcessingStatus.SUCCESS -> {
                                            archiveText = "Reload app to see new data"
                                        }
                                        FileProcessingStatus.ERROR -> {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Error",
                                                tint = MaterialTheme.colorScheme.error,
                                                modifier = Modifier.size(48.dp).padding(horizontal = 8.dp)
                                            )
                                            archiveText = "Import failed" // uploadStatusMessageParser(zipFileStatus.status.message, "Something went wrong")
                                        }
                                        else -> { archiveText = "Complete"}
                                    }

                                    Text(
                                        text = archiveText,
                                        fontStyle = FontStyle.Italic,
                                        modifier = Modifier.alpha(0.5f)

                                    )
                                }
                                if (zipFile.status == FileProcessingStatus.PROCESSING) {
                                    Row(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        LinearProgressIndicator(
                                            progress = { fluidProgress.value },
                                            trackColor = Color.Gray.copy(alpha = 0.2f),
                                            drawStopIndicator = {},
                                            modifier = Modifier.fillMaxWidth(),
                                            gapSize = (-2).dp
                                        )
                                    }
                                } else if (
                                    listOf(
                                        FileProcessingStatus.WAITING,
                                        FileProcessingStatus.PREPARING,
                                        FileProcessingStatus.FINALIZING)
                                        .contains(zipFile.status)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        LinearProgressIndicator(
                                            trackColor = Color.Gray.copy(alpha = 0.2f),
                                            modifier = Modifier.fillMaxWidth(),
                                            gapSize = (-2).dp
                                        )
                                    }
                                }
                            }
                            if (zipFile.status == FileProcessingStatus.SUCCESS) {
                                Column() {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        IconButton(onClick = { restartApp() }) {
                                            Icon(
                                                imageVector = Icons.Default.Refresh,
                                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                                contentDescription = "Reload app"
                                            )
                                        }
                                    }
                                }
                            }

                        }
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