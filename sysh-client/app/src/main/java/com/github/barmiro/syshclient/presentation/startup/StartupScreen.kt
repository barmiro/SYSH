package com.github.barmiro.syshclient.presentation.startup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val serverResponded by startupVM.serverResponded.collectAsState()


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
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(modifier = Modifier.padding(top = 96.dp)) {
                    Text(text = "Welcome to SYSH!",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold)
                }
                Row {
                    Text(text = "Please enter your server's address:",
                        color = MaterialTheme.colorScheme.onBackground )

                }
                Row {
                    OutlinedTextField(value = urlInput,
                        onValueChange = startupVM::onUrlChanged,
                        label = {
                            Text("Server URL")
                        },
                        isError = urlValidation?.let {
                            !it.isValidUrl
                        } ?: false
                    )
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

                urlValidation?.let {
                    if (it.isValidUrl) {
                        if (!it.hasScheme) {
                            UrlInfoItem(
                                icon = {
                                    Icon(imageVector = Icons.Default.Info,
                                        contentDescription = "Info")
                                },
                                text = "No protocol specified,\nhttp will be used."
                            )
                        }
                        if (!it.hasPort) {
                            UrlInfoItem(
                                icon = {
                                    Icon(imageVector = Icons.Default.Info,
                                        contentDescription = "Info")
                                },
                                text = "No port specified,\nprotocol default will be used."
                            )
                        }
                    } else {
                        UrlInfoItem(
                            icon = {
                                Icon(imageVector = Icons.Default.Close,
                                    contentDescription = "Error")
                            },
                            text = "Invalid URL."
                        )
                    }
                }
            }
        }
    }


@Composable
fun UrlInfoItem(
    icon: @Composable () -> Unit,
    text: String
    ) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp).fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                icon()
            }
            Column(modifier = Modifier.padding(8.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = text,
                    textAlign = TextAlign.Center
                )

            }

        }
    }
}

@Composable
fun InfoItem(
    icon: @Composable () -> Unit,
    title: String,
    text: String
) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp).fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    icon()
                }
                Column(modifier = Modifier.padding(8.dp).fillMaxWidth(),
//                horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row() {
                        Text(
                            text = title,
//                    textAlign = TextAlign.Center
                        )
                    }
                    Row() {
                        Text(
                            text = text,
                            lineHeight = 14.sp,
                            fontSize = 14.sp,
                            fontStyle = FontStyle.Italic,
                            modifier = Modifier.alpha(0.5f)
//                    textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

    }
}