package com.github.barmiro.syshclient.presentation.login

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch

@Composable
fun SpotifyAuthScreen(
    sessionVM: SessionViewModel
) {

    val spotifyAuthUrl by sessionVM.spotifyAuthUrl.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(spotifyAuthUrl) {
        spotifyAuthUrl?.let { url ->
            coroutineScope.launch {
                openWebsite(context, url)
            }
            sessionVM.startRedirectServer()
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

            Text(
                text = "To use SYSH, you need to link your Spotify account",
                color = MaterialTheme.colorScheme.onBackground
            )


            Button(
                onClick = {
                    sessionVM.spotifyAuthorization()
                }
            ) {
                Text("Open browser")
            }

            TextButton(
                onClick = {
                    sessionVM.logout()
                }
            ) {
                Text("Return to login screen")
            }
        }
    }
}

fun openWebsite(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(intent)
}