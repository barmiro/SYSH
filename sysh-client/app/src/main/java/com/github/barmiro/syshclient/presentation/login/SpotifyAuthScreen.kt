package com.github.barmiro.syshclient.presentation.login

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun SpotifyAuthScreen(
    sessionVM: SessionViewModel
) {

    val spotifyAuthUrl by sessionVM.spotifyAuthUrl.collectAsState()
    val username by sessionVM.username.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(spotifyAuthUrl) {
        spotifyAuthUrl?.let { url ->
            coroutineScope.launch {
                openWebsite(context, url)
            }
//            sessionVM.startRedirectServer()
        }
    }
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Hi, $username!",
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(12.dp))
            Text(
                text = "Before we can proceed, you need to link your Spotify account.",
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                lineHeight = 20.sp,
                modifier = Modifier.padding(horizontal = 44.dp)
            )

            Spacer(Modifier.height(16.dp))
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

fun openWebsite(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(intent)
}