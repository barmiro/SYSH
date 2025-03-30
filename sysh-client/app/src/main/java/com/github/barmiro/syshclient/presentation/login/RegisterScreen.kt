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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation.NavController
import com.github.barmiro.syshclient.presentation.common.Login
import com.github.barmiro.syshclient.presentation.common.SessionViewModel

@Composable
fun RegisterScreen(authVM: AuthViewModel,
                   sessionVM: SessionViewModel,
                   navController: NavController
) {

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
    val isRegistered = authVM.isRegistered.collectAsState().value
    val registerState = authVM.registerState.collectAsState().value
    LaunchedEffect(isRegistered) {
        if (isRegistered) {
            navController.navigate(Login)
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


            Text(text = "Create an account:",
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

            OutlinedTextField(value = confirmation.value,
                onValueChange = { confirmation.value = it },
                label = {
                    Text("Confirm password")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
                isError = password.value.text != confirmation.value.text
            )

            if (password.value.text != confirmation.value.text) {
                Text("Passwords do not match")
            }

            Button(
                onClick = {
                    authVM.register(username.value.text, password.value.text)
                },
                enabled = password.value.text == confirmation.value.text && password.value.text.isNotEmpty()
            ) {
                Text("Create Account")
            }

        }
    }
}