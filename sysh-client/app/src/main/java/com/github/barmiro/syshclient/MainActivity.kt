package com.github.barmiro.syshclient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.github.barmiro.syshclient.presentation.common.AppNavHost
import com.github.barmiro.syshclient.presentation.common.BottomNavBar
import com.github.barmiro.syshclient.presentation.common.ConnectionError
import com.github.barmiro.syshclient.presentation.common.Home
import com.github.barmiro.syshclient.presentation.common.Login
import com.github.barmiro.syshclient.presentation.common.SpotifyAuth
import com.github.barmiro.syshclient.presentation.common.Startup
import com.github.barmiro.syshclient.presentation.home.HomeViewModel
import com.github.barmiro.syshclient.presentation.login.SessionViewModel
import com.github.barmiro.syshclient.presentation.settings.ImportViewModel
import com.github.barmiro.syshclient.presentation.settings.admin.AdminViewModel
import com.github.barmiro.syshclient.presentation.settings.import.restartApp
import com.github.barmiro.syshclient.presentation.startup.StartupViewModel
import com.github.barmiro.syshclient.presentation.stats.StatsViewModel
import com.github.barmiro.syshclient.presentation.top.TopScreenViewModel
import com.github.barmiro.syshclient.presentation.top.albums.TopAlbumsViewModel
import com.github.barmiro.syshclient.presentation.top.artists.TopArtistsViewModel
import com.github.barmiro.syshclient.presentation.top.tracks.TopTracksViewModel
import com.github.barmiro.syshclient.ui.theme.SyshClientTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val startupVM: StartupViewModel by viewModels()
    private val homeVM: HomeViewModel by viewModels()
    private val topScreenVM: TopScreenViewModel by viewModels()
    private val topTracksVM: TopTracksViewModel by viewModels()
    private val topAlbumsVM: TopAlbumsViewModel by viewModels()
    private val topArtistsVM: TopArtistsViewModel by viewModels()
    private val statsVM: StatsViewModel by viewModels()
    private val sessionVM: SessionViewModel by viewModels()
    private val importVM: ImportViewModel by viewModels()
    private val adminVM: AdminViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {

            val isLoggedIn by sessionVM.isLoggedIn.collectAsState()
            val isAuthorizedWithSpotify by sessionVM.isAuthorizedWithSpotify.collectAsState()
            val navController = rememberNavController()
            val storedUrl by sessionVM.serverUrl.collectAsState()
            val serverResponded by startupVM.serverResponded.collectAsState()
            val responseCode by sessionVM.responseCode.collectAsState()

            val snackbarHostState = remember { SnackbarHostState() }
            val coroutineScope = rememberCoroutineScope()
            val errorMessage by sessionVM.errorMessage.collectAsState()

//            TODO: find out why there's a loop
            var loginSuccessful by remember { mutableStateOf(false) }


            LaunchedEffect(storedUrl) {
//                if (storedUrl != null) {
                    startupVM.getServerInfo()
//                }
            }

            LaunchedEffect(serverResponded, isLoggedIn) {
                serverResponded?.let {
                    if (it) {
                        if (isLoggedIn) {
                            sessionVM.getUserData()
                        } else {
                            loginSuccessful = false
                            navController.navigate(Login) {
                                popUpTo(navController.graph.startDestinationId) {
                                    inclusive = true
                                }
                            }
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    message = errorMessage.takeIf { message ->
                                        !message.isNullOrEmpty()
                                    } ?: "You have been logged out",
                                    actionLabel = "Dismiss",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    } else {
                        loginSuccessful = false
                        if (!isLoggedIn) {
                            navController.navigate(Startup) {
                                popUpTo(navController.graph.startDestinationId) {
                                    inclusive = true
                                }
                            }
                            if (!storedUrl.isNullOrEmpty()){
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "Couldn't connect to the server"
                                    )
                                }
                            }
                        } else {
                            navController.navigate(ConnectionError) {
                                popUpTo(navController.graph.startDestinationId) {
                                    inclusive = true
                                }
                            }
                        }

                    }
                }
            }

            LaunchedEffect(responseCode) {
//                no better idea where to stop it for now
                sessionVM.stopRedirectServer()

                when (responseCode) {
                    200 -> {
                        navController.navigate(Home) {
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = true
                            }
                        }
                    }
                    403 -> {
                        navController.navigate(SpotifyAuth)
                    }
//            this means no error was encountered
                    0 -> {}
                    401 -> {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = errorMessage.takeIf {
                                    !it.isNullOrEmpty()
                                } ?: "You have been logged out",
                                actionLabel = "Dismiss",
                                duration = SnackbarDuration.Short
                            )
                        }
                        navController.navigate(Login) {
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = true
                            }
                        }
                    }
                    else -> {
                        navController.navigate(Login) {
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = true
                            }
                        }
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

            SyshClientTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = MaterialTheme.colorScheme.background)
                ) {
                    Scaffold(
                        snackbarHost = {
                            SnackbarHost(hostState = snackbarHostState)
                        },
                        bottomBar = {
                            if (serverResponded == true && isLoggedIn && isAuthorizedWithSpotify) {
                                if (!loginSuccessful) {
                                    topScreenVM.getOldestStreamDate()
                                    homeVM.getStats()
                                    loginSuccessful = true
                                }
                            }
                            if (loginSuccessful) {
                                BottomNavBar(navController)
                            }
                        }
                    ) { innerPadding ->
                        Column(modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding(), top = 0.dp)){
                            AppNavHost(navController,
                                startupVM,
                                homeVM,
                                topScreenVM,
                                topTracksVM,
                                topAlbumsVM,
                                topArtistsVM,
                                statsVM,
                                sessionVM,
                                importVM,
                                adminVM,
                                onPickZipFile = { pickZipFile() },
                                restartApp = { restartApp(applicationContext) }
                            )
                        }

                    }
                }
            }
        }
    }

    private val zipPickerLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            importVM.handleZipFile(it, applicationContext)
        }
    }

    private fun pickZipFile() {
        zipPickerLauncher.launch(arrayOf("application/zip"))
    }

    private fun finalizeImport() {
        topScreenVM.getOldestStreamDate()
        homeVM.getStats()

    }
}

