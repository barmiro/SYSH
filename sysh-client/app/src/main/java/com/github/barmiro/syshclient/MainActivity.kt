package com.github.barmiro.syshclient

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.github.barmiro.syshclient.presentation.common.AppNavHost
import com.github.barmiro.syshclient.presentation.common.BottomNavBar
import com.github.barmiro.syshclient.presentation.common.ConnectionError
import com.github.barmiro.syshclient.presentation.common.Home
import com.github.barmiro.syshclient.presentation.common.Login
import com.github.barmiro.syshclient.presentation.common.PasswordChange
import com.github.barmiro.syshclient.presentation.common.Splash
import com.github.barmiro.syshclient.presentation.common.SpotifyAuth
import com.github.barmiro.syshclient.presentation.common.Startup
import com.github.barmiro.syshclient.presentation.home.HomeViewModel
import com.github.barmiro.syshclient.presentation.login.SessionViewModel
import com.github.barmiro.syshclient.presentation.settings.ImportViewModel
import com.github.barmiro.syshclient.presentation.settings.SettingsViewModel
import com.github.barmiro.syshclient.presentation.settings.admin.AdminViewModel
import com.github.barmiro.syshclient.presentation.settings.import.restartApp
import com.github.barmiro.syshclient.presentation.startup.StartupViewModel
import com.github.barmiro.syshclient.presentation.stats.StatsViewModel
import com.github.barmiro.syshclient.presentation.top.TopScreenViewModel
import com.github.barmiro.syshclient.presentation.top.albums.TopAlbumsViewModel
import com.github.barmiro.syshclient.presentation.top.artists.TopArtistsViewModel
import com.github.barmiro.syshclient.presentation.top.tracks.TopTracksViewModel
import com.github.barmiro.syshclient.ui.theme.SyshClientTheme
import com.github.barmiro.syshclient.util.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

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
    private val settingsVM: SettingsViewModel by viewModels()


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
            val username by sessionVM.username.collectAsState()
            val importStatus by importVM.importStatus.collectAsState()
            val isDemoVersion by sessionVM.isDemoVersion.collectAsState()
            val isCallbackSuccessful by sessionVM.isCallbackSuccessful.collectAsState()
            val appTheme by settingsVM.appTheme.collectAsState()
            val isGradientEnabled by settingsVM.isGradientEnabled.collectAsState()


            val snackbarHostState = remember { SnackbarHostState() }
            val coroutineScope = rememberCoroutineScope()
            val errorMessage by sessionVM.errorMessage.collectAsState()
            var loginSuccessful by remember { mutableStateOf(false) }


            val config = LocalConfiguration.current
            val context = LocalContext.current


            LaunchedEffect(storedUrl) {
//                if (storedUrl != null) {
                    startupVM.getServerInfo()
//                }
            }

            LaunchedEffect(isCallbackSuccessful) {
                if (isCallbackSuccessful == true) {
                    startupVM.getServerInfo()
                }
            }


            LaunchedEffect(serverResponded, isLoggedIn) {
                serverResponded?.let {
                    if (it) {
                        if (isLoggedIn) {
                            sessionVM.getUserData()
                        } else if (isDemoVersion != true) {
                            loginSuccessful = false
                            navController.navigate(Login) {
                                popUpTo(navController.graph.startDestinationId) {
                                    inclusive = true
                                }
                            }
                            if (responseCode != 0) {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = errorMessage.takeIf { message ->
                                            !message.isNullOrBlank()
                                        } ?: "You have been logged out",
                                        actionLabel = "Dismiss",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                        } else {
                            navController.navigate(Splash) {
                                popUpTo(navController.graph.startDestinationId) {
                                    inclusive = true
                                }
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
                            importVM.closeImportStateSseConnection()
                            if (!storedUrl.isNullOrBlank()){
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
                when (responseCode) {
                    200 -> {
                        navController.navigate(Home) {
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = true
                            }
                        }
                    }
                    403 -> {
                        navController.navigate(PasswordChange)
                    }
                    412 -> {
                        navController.navigate(SpotifyAuth)
                    }
//            this means no error was encountered
                    0 -> { importVM.closeImportStateSseConnection() }
                    401 -> {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = errorMessage.takeIf {
                                    !it.isNullOrBlank()
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
//                        importVM.closeImportStateSseConnection()
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
                                    !it.isNullOrBlank()
                                } ?: "An unknown error occurred",
                                actionLabel = "Dismiss",
                                duration = SnackbarDuration.Short
                            )
                        }
                    }
                }
            }


            SyshClientTheme(
                appTheme = appTheme
            ) {
                // Override status bar icon colors
                val statusBarColor = MaterialTheme.colorScheme.background.toArgb()
                val navigationBarColor = MaterialTheme.colorScheme.secondaryContainer.toArgb()

                val isLightTheme: Boolean = if (appTheme == AppTheme.SYSTEM_DEFAULT) {
                    !isSystemInDarkTheme()
                } else {
                    appTheme == AppTheme.LIGHT
                }

                LaunchedEffect(isLightTheme, loginSuccessful) {
                    if (isLightTheme) {
                        enableEdgeToEdge(
                            statusBarStyle = SystemBarStyle.light(
                                statusBarColor, statusBarColor
                            ),
                            navigationBarStyle = if (loginSuccessful) {
                                SystemBarStyle.light(
                                    navigationBarColor, navigationBarColor
                                )
                            } else {
                                SystemBarStyle.light(
                                    statusBarColor, statusBarColor
                                )
                            }
                        )
                    } else {
                        enableEdgeToEdge(
                            statusBarStyle = SystemBarStyle.dark(
                                statusBarColor,
                            ),
                            navigationBarStyle = if(loginSuccessful) {
                                SystemBarStyle.dark(
                                    navigationBarColor
                                )
                            } else {
                                SystemBarStyle.dark(
                                    statusBarColor
                                )
                            }
                        )
                    }
                }

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
                                    if(isDemoVersion != true) importVM.startImportStateSseConnection()

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
                                settingsVM,
                                importStatus,
                                isGradientEnabled,
                                onPickZipFile = { pickZipFile(isDemoVersion) },
                                restartApp = { restartApp(applicationContext) }
                            )
                        }

                    }
                }
            }
        }
        handleSpotifyCallback(intent)
    }

    private fun Context.setLocale(locale: Locale): Context {
        val config = resources.configuration
        val newConfig = Configuration(config)
        newConfig.setLocale(locale)
        return createConfigurationContext(newConfig)
    }

    override fun attachBaseContext(newBase: Context) {
        // i know this is bad, but it's the only way to force ISO date format
        val locale = Locale.CANADA
        val context = newBase.setLocale(locale)
        super.attachBaseContext(context)
    }


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleSpotifyCallback(intent)
    }

    private val zipPickerLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            importVM.handleZipFile(it, applicationContext)
        }
    }

    private fun pickZipFile(isDemoVersion: Boolean?) {
        if (isDemoVersion == true) {
            importVM.mockZipImport()
        } else {
            zipPickerLauncher.launch(arrayOf("application/zip"))
        }
    }

    private fun handleSpotifyCallback(intent: Intent?) {
        val data = intent?.data ?: return

        if (data.scheme == "sysh" && data.host == "open" && data.path?.startsWith("/callback") == true) {
            val code = data.getQueryParameter("code")
            val state = data.getQueryParameter("state")

            sessionVM.callback(state, code)
        }
    }
}



