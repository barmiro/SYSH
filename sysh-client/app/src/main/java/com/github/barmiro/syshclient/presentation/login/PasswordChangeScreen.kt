package com.github.barmiro.syshclient.presentation.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import com.github.barmiro.syshclient.presentation.settings.SettingsViewModel

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

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            Text(text = "You need to change your password:",
                color = MaterialTheme.colorScheme.onBackground )

            OutlinedTextField(value = oldPassword.value,
                onValueChange = { oldPassword.value = it },
                label = {
                    Text("Old password")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation()
            )
            OutlinedTextField(value = newPassword.value,
                onValueChange = { newPassword.value = it },
                label = {
                    Text("New password")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation()
            )

            OutlinedTextField(value = confirmation.value,
                onValueChange = { confirmation.value = it },
                label = {
                    Text("Confirm new password")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
                isError = newPassword.value.text != confirmation.value.text
            )

            if (newPassword.value.text != confirmation.value.text) {
                Text("Passwords do not match")
            }
            if (newPassword.value.text == oldPassword.value.text) {
                Text("New password can't be the same as the old password")
            }

            Button(
                onClick = {
                    settingsVM.changePassword(oldPassword.value.text, newPassword.value.text)
                },
                enabled = newPassword.value.text == confirmation.value.text
                        && newPassword.value.text != oldPassword.value.text
                        && newPassword.value.text.isNotEmpty()
                        && oldPassword.value.text.isNotEmpty()
            ) {
                Text("Change Password")
            }

            isPasswordChanged?.let { changed ->
                if (changed) {
                    Text("Password changed successfully")
                    TextButton(
                        onClick = {
                            sessionVM.logout()
                        }

                    ) {
                        Text("Return to login screen")
                    }
                } else {
                    Text("Password change failed")
                }
            }

        }
    }
}