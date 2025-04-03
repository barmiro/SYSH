package com.github.barmiro.syshclient.presentation.startup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.github.barmiro.syshclient.presentation.common.Login
import com.github.barmiro.syshclient.presentation.login.SessionViewModel

@Composable
fun StartupScreen(
    startupVM: StartupViewModel,
    sessionVM: SessionViewModel,
    navController: NavHostController
) {

    val urlInput by startupVM.urlInput.collectAsState()
    val urlValidation by startupVM.urlValidation.collectAsState()
    val storedUrl by sessionVM.serverUrl.collectAsState()
    val serverResponded by startupVM.serverResponded.collectAsState()


//    LaunchedEffect(storedUrl) {
//        if (!storedUrl.isNullOrEmpty()) {
//            startupVM.getServerInfo()
//        }
//    }
    LaunchedEffect(serverResponded) {
        serverResponded?.let {
            if (it) {
                navController.navigate(Login)

            }
        }
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
                Text(text = "Welcome to SYSH!",
                    color = MaterialTheme.colorScheme.onBackground )

                Text(text = "Please enter your server's address:",
                    color = MaterialTheme.colorScheme.onBackground )

                OutlinedTextField(value = urlInput,
                    onValueChange = startupVM::onUrlChanged,
                    label = {
                        Text("Server URL")
                    },
                    isError = urlValidation?.let {
                        !it.isValidUrl
                    } ?: false
                )

                urlValidation?.let {
                    if (it.isValidUrl) {
                        if (!it.hasScheme) {
                            Text("No protocol specified, http will be used.")
                        }
                        if (!it.hasPort) {
                            Text("No port specified,\nprotocol default will be used.")
                        }
                    } else {
                        Text("Invalid URL.")
                    }
                }

                Button(
                    onClick = {
                        urlValidation?.let {
                            val url = if (it.hasScheme) urlInput else "http://$urlInput"
                            sessionVM.saveServerUrl(url) {
                                startupVM.getServerInfo()
                            }
                        }
                    },
                    enabled = urlValidation?.isValidUrl ?: false
                ) {
                    Text("Confirm")
                }
            }
        }
    }