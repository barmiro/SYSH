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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import com.github.barmiro.syshclient.util.Resource

@Composable
fun RegisterScreen(viewModel: RegisterViewModel) {

    val username = remember {
        mutableStateOf(TextFieldValue())
    }
    val password = remember {
        mutableStateOf(TextFieldValue())
    }


//    I'll put this here for now, but I'll have to reconsider this
    val registerState by viewModel.registerState.collectAsState()
    LaunchedEffect(registerState) {
        when (registerState) {
            is Resource.Success -> {
//                LoginScreen(loginVM)
            }
            else -> {

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
                    viewModel.register(username.value.text, password.value.text)
                }
            ) {
                Text("log in")
            }

            TextButton(
                onClick = {

                }
            ) {
                Text("Create an account")
            }

        }
    }
}