package com.github.barmiro.syshclient.presentation.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation.NavHostController
import com.github.barmiro.syshclient.Home
import com.github.barmiro.syshclient.Register
import com.github.barmiro.syshclient.SpotifyAuth
import com.github.barmiro.syshclient.presentation.common.SessionViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(authVM: AuthViewModel,
                sessionVM: SessionViewModel,
                navController: NavHostController
) {

    val isLoggedIn by sessionVM.isLoggedIn.collectAsState()
    val responseCode by authVM.responseCode.collectAsState()
    val errorMessage by authVM.errorMessage.collectAsState()

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            authVM.getUserData()
        }
    }
    val snackbarHostState = remember { SnackbarHostState() }

    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(responseCode) {
        when (responseCode) {
            200 -> {
                navController.navigate(Home)
            }
            403 -> {
                navController.navigate(SpotifyAuth)
            }
//            this means no error was encountered
            0 -> {}
            else -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = errorMessage.takeIf {
                            !it.isNullOrEmpty()
                        } ?: "An unknown error occurred",
                        actionLabel = "Dismiss",
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }


    SnackbarHost(hostState = snackbarHostState)
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { contentPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .background(color = MaterialTheme.colorScheme.background)
        ) {
            if (isLoggedIn && authVM.isLoading.collectAsState().value) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Loading...",
                        color = MaterialTheme.colorScheme.onBackground )
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val username = remember {
                        mutableStateOf(TextFieldValue())
                    }
                    val password = remember {
                        mutableStateOf(TextFieldValue())
                    }

                    Text(text = "Welcome to SYSH!",
                        color = MaterialTheme.colorScheme.onBackground )

                    Text(text = "Please log in:",
                        color = MaterialTheme.colorScheme.onBackground )


                    OutlinedTextField(value = username.value,
                        onValueChange = { username.value = it },
                        label = {
                            Text("Username")
                        }
                    )
                    OutlinedTextField(value = password.value,
                        onValueChange = { password.value = it },
                        label = {
                            Text("Password")
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation = PasswordVisualTransformation()
                    )

                    Button(
                        onClick = {
                            authVM.getToken(username.value.text, password.value.text)
                        }
                    ) {
                        Text("log in")
                    }

                    TextButton(
                        onClick = {
                            navController.navigate(Register)
                        }
                    ) {
                        Text("Create an account")
                    }
                }
            }
        }
    }
}
