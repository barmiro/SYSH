package com.github.barmiro.syshclient.presentation.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.github.barmiro.syshclient.presentation.common.Login
import com.github.barmiro.syshclient.presentation.startup.StartupViewModel
import com.github.barmiro.syshclient.presentation.startup.UrlInfoItem

@Composable
fun RegisterScreen(sessionVM: SessionViewModel,
                   startupVM: StartupViewModel,
                   navController: NavController
) {

    val serverInfo by startupVM.serverInfo.collectAsState()

    val username = remember {
        mutableStateOf(TextFieldValue())
    }
    val password = remember {
        mutableStateOf(TextFieldValue())
    }
    val confirmation = remember {
        mutableStateOf(TextFieldValue())
    }


//    I'll put this here for now, but I'll have to reconsider this
    val isRegistered = sessionVM.isRegistered.collectAsState().value
    val registerState = sessionVM.registerState.collectAsState().value
    LaunchedEffect(isRegistered) {
        if (isRegistered) {
            navController.navigate(Login) {
                popUpTo(0) {
                    inclusive = true
                }
            }
        }
    }


    val isInputValid = canEncodeInput(username.value.text)
            && canEncodeInput(password.value.text)
            && canEncodeInput(confirmation.value.text)

    val isValueTooLong = username.value.text.length > 64
            || password.value.text.length > 72
            || confirmation.value.text.length > 72


    val passwordFieldFocusRequester = remember { FocusRequester() }
    val confirmationFieldFocusRequester = remember { FocusRequester() }

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


            Text(text = "Create an account:",
                color = MaterialTheme.colorScheme.onBackground )

            OutlinedTextField(value = username.value,
                onValueChange = { username.value = it },
                label = {
                    Text("Username")
                },
                modifier = Modifier.width(256.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        passwordFieldFocusRequester.requestFocus()
                    }
                ),
                singleLine = true,
                maxLines = 1,
                isError = !canEncodeInput(username.value.text)
                        || username.value.text.length > 64
            )
            OutlinedTextField(value = password.value,
                onValueChange = { password.value = it },
                label = {
                    Text("Password")
                },
                modifier = Modifier.width(256.dp)
                    .focusRequester(passwordFieldFocusRequester),
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
                isError = !canEncodeInput(password.value.text)
                        || password.value.text.length > 72
            )

            OutlinedTextField(value = confirmation.value,
                onValueChange = { confirmation.value = it },
                label = {
                    Text("Confirm password")
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
                        sessionVM.register(username.value.text, password.value.text)
                    }
                ),
                singleLine = true,
                maxLines = 1,
                isError = (password.value.text != confirmation.value.text
                        && confirmation.value.text.isNotEmpty())
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
                    text = "Password or username is too long"
                )
            }

            if (password.value.text != confirmation.value.text
                && confirmation.value.text.isNotEmpty()) {
                UrlInfoItem(
                    icon = {
                        Icon(imageVector = Icons.Default.Close,
                            tint = MaterialTheme.colorScheme.error,
                            contentDescription = "Error")
                    },
                    text = "Passwords do not match"
                )
            }

            Button(
                onClick = {
                    sessionVM.register(username.value.text, password.value.text)
                },
                enabled = password.value.text == confirmation.value.text
                        && password.value.text.isNotEmpty()
                        && username.value.text.isNotEmpty()
                        && isInputValid
                        && !isValueTooLong
            ) {
                Text("Create Account")
            }

        }
    }
}