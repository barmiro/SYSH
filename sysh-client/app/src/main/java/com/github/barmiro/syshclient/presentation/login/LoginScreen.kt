package com.github.barmiro.syshclient.presentation.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.github.barmiro.syshclient.presentation.common.Register
import com.github.barmiro.syshclient.presentation.common.Splash
import com.github.barmiro.syshclient.presentation.startup.StartupViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(sessionVM: SessionViewModel,
                startupVM: StartupViewModel,
                navController: NavHostController
) {
    val serverUrl by sessionVM.serverUrl.collectAsState()
    val serverInfo by startupVM.serverInfo.collectAsState()
    var isLoading by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) { innerPadding ->

        if (isLoading) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(modifier = Modifier.size(48.dp))
            }
        } else {
            Column(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val username = remember {
                        mutableStateOf(TextFieldValue())
                    }
                    val password = remember {
                        mutableStateOf(TextFieldValue())
                    }

                    val passwordFieldFocusRequester = remember { FocusRequester() }

                    Text(text = "Welcome to SYSH!",
                        color = MaterialTheme.colorScheme.onBackground )

                    Text(text = "Please log in:",
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
                        maxLines = 1
                    )
                    OutlinedTextField(value = password.value,
                        onValueChange = { password.value = it },
                        label = {
                            Text("Password")
                        },
                        modifier = Modifier
                            .width(256.dp)
                            .focusRequester(passwordFieldFocusRequester),
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done,
                            keyboardType = KeyboardType.Password
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                sessionVM.getToken(username.value.text, password.value.text)
                                navController.navigate(Splash)
                            }
                        ),
                        singleLine = true,
                        maxLines = 1
                    )

                    Button(
                        onClick = {
                            sessionVM.getToken(username.value.text, password.value.text)
                            navController.navigate(Splash)
                        }
                    ) {
                        Text("Log In")
                    }



                    TextButton(
                        onClick = {
                            serverInfo?.let {
                                if (it.is_restricted_mode && it.users_exist) {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(
                                            message = "User registration disabled. Contact your system administrator to create an account.",
                                            actionLabel = "Dismiss",
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                } else {
                                    navController.navigate(Register)
                                }
                            }
                        }
                    ) {
                        Text("Create an account")
                    }
                    Spacer(modifier = Modifier.height(128.dp))
                    Text(text = "Connecting to server",
                        modifier = Modifier.alpha(0.8f))
                    Text(text = serverUrl ?: "unknown",
                        modifier = Modifier.alpha(0.8f))
                    TextButton(
                        onClick = {
                            isLoading = true
                            sessionVM.clearAllPreferences()
                        }

                    ) {
                        Text("Change server address")
                    }
                }
            }
        

    }
}