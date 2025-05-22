package com.github.barmiro.syshclient.presentation.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.github.barmiro.syshclient.presentation.settings.SettingsViewModel
import com.github.barmiro.syshclient.presentation.startup.UrlInfoItem

@Composable
fun PasswordChangeScreen(settingsVM: SettingsViewModel,
                         sessionVM: SessionViewModel
) {

    val isPasswordChanged by settingsVM.isPasswordChanged.collectAsState()

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

    val isValueTooLong = oldPassword.value.text.length > 72
            || newPassword.value.text.length > 72
            || confirmation.value.text.length > 72

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isPasswordChanged == true) {
                UrlInfoItem(
                    icon = {
                        Icon(imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = Color(
                                red = 52,
                                green = 178,
                                blue = 51
                            )
                        )
                    },
                    text = "Password changed successfully. You can now log in using your new password."
                )
            } else {

                val newPasswordFieldFocusRequester = remember { FocusRequester() }
                val confirmationFieldFocusRequester = remember { FocusRequester() }


                Text(text = "You need to change your password:",
                    color = MaterialTheme.colorScheme.onBackground )

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
                            || oldPassword.value.text.length > 72
                )

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
                            || newPassword.value.text.length > 72
                )

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
                            settingsVM.changePassword(oldPassword.value.text, newPassword.value.text)
                        }
                    ),
                    singleLine = true,
                    maxLines = 1,
                    isError = newPassword.value.text != confirmation.value.text
                            || !canEncodeInput(confirmation.value.text)
                            || confirmation.value.text.length > 72
                )


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

                if (isValueTooLong) {
                    UrlInfoItem(
                        icon = {
                            Icon(imageVector = Icons.Default.Close,
                                tint = MaterialTheme.colorScheme.error,
                                contentDescription = "Error")
                        },
                        text = "Password can't be longer than 72 characters"
                    )
                }

                if (newPassword.value.text != confirmation.value.text
                    && newPassword.value.text.isNotEmpty()) {
                    UrlInfoItem(
                        icon = {
                            Icon(imageVector = Icons.Default.Close,
                                tint = MaterialTheme.colorScheme.error,
                                contentDescription = "Error")
                        },
                        text = "Passwords do not match"
                    )
                }
                if (newPassword.value.text == oldPassword.value.text
                    && newPassword.value.text.isNotEmpty()
                ) {
                    UrlInfoItem(
                        icon = {
                            Icon(imageVector = Icons.Default.Close,
                                tint = MaterialTheme.colorScheme.error,
                                contentDescription = "Error")
                        },
                        text = "New password can't be the same as the old password"
                    )
                }

                Button(
                    onClick = {
                        settingsVM.changePassword(oldPassword.value.text, newPassword.value.text)
                    },
                    enabled = newPassword.value.text == confirmation.value.text
                            && newPassword.value.text != oldPassword.value.text
                            && newPassword.value.text.isNotEmpty()
                            && oldPassword.value.text.isNotEmpty()
                            && isInputValid
                            && !isValueTooLong
                ) {
                    Text("Change Password")
                }
            }

            TextButton(
                onClick = {
                    sessionVM.logout()
                }
            ) {
                Text("Return to login screen")
            }

            if (isPasswordChanged == false) {
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