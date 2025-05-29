package com.github.barmiro.syshclient.presentation.common

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.github.barmiro.syshclient.data.common.startup.ImportStatusDTO
import com.github.barmiro.syshclient.presentation.home.HomeScreen
import com.github.barmiro.syshclient.presentation.home.HomeViewModel
import com.github.barmiro.syshclient.presentation.login.LoginScreen
import com.github.barmiro.syshclient.presentation.login.PasswordChangeScreen
import com.github.barmiro.syshclient.presentation.login.RegisterScreen
import com.github.barmiro.syshclient.presentation.login.SessionViewModel
import com.github.barmiro.syshclient.presentation.login.SpotifyAuthScreen
import com.github.barmiro.syshclient.presentation.settings.ImportViewModel
import com.github.barmiro.syshclient.presentation.settings.SettingsScreen
import com.github.barmiro.syshclient.presentation.settings.SettingsViewModel
import com.github.barmiro.syshclient.presentation.settings.admin.AdminViewModel
import com.github.barmiro.syshclient.presentation.settings.admin.ManageUsersScreen
import com.github.barmiro.syshclient.presentation.settings.import.ImportProgressOverlay
import com.github.barmiro.syshclient.presentation.settings.import.ImportScreen
import com.github.barmiro.syshclient.presentation.startup.ConnectionErrorScreen
import com.github.barmiro.syshclient.presentation.startup.SplashScreen
import com.github.barmiro.syshclient.presentation.startup.StartupScreen
import com.github.barmiro.syshclient.presentation.startup.StartupViewModel
import com.github.barmiro.syshclient.presentation.stats.StatsScreen
import com.github.barmiro.syshclient.presentation.stats.StatsViewModel
import com.github.barmiro.syshclient.presentation.top.TopScreen
import com.github.barmiro.syshclient.presentation.top.TopScreenViewModel
import com.github.barmiro.syshclient.presentation.top.albums.TopAlbumsScreen
import com.github.barmiro.syshclient.presentation.top.albums.TopAlbumsViewModel
import com.github.barmiro.syshclient.presentation.top.artists.TopArtistsViewModel
import com.github.barmiro.syshclient.presentation.top.tracks.TopTracksViewModel
import kotlinx.serialization.Serializable

@Composable
fun AppNavHost(navController: NavHostController,
               startupVM: StartupViewModel,
               homeVM: HomeViewModel,
               topScreenVM: TopScreenViewModel,
               topTracksVM: TopTracksViewModel,
               topAlbumsVM: TopAlbumsViewModel,
               topArtistsVM: TopArtistsViewModel,
               statsVM: StatsViewModel,
               sessionVM: SessionViewModel,
               importVM: ImportViewModel,
               adminVM: AdminViewModel,
               settingsVM: SettingsViewModel,
               importStatus: ImportStatusDTO?,
               onPickZipFile: () -> Unit,
               restartApp: () -> Unit) {

    NavHost(
        navController = navController,
        startDestination = Splash,
        enterTransition = {
            fadeIn(animationSpec = tween(400))
        },
        exitTransition = {
            fadeOut(animationSpec = tween(400))
        }
    ) {
        composable<Splash> {
            SplashScreen()
        }
        composable<Startup> {
            StartupScreen(startupVM, sessionVM, navController)
        }

        composable<Login> {
            LoginScreen(sessionVM, startupVM, navController)
        }
        composable<Register> {
            RegisterScreen(sessionVM, startupVM, navController)
        }
        composable<Home> {
            HomeScreen(homeVM)
            ImportProgressOverlay(importStatus, restartApp)
        }
        composable<Top> {
            TopScreen(topScreenVM, topTracksVM, topAlbumsVM, topArtistsVM)
            ImportProgressOverlay(importStatus, restartApp)
        }
        composable<Stats> {
            StatsScreen(statsVM)
            ImportProgressOverlay(importStatus, restartApp)
        }
        composable<TopAlbums> {
            TopAlbumsScreen(topAlbumsVM)
        }
        composable<SpotifyAuth> {
            SpotifyAuthScreen(sessionVM)
        }
        composable<Settings> {
            SettingsScreen(sessionVM, settingsVM, navController)
            ImportProgressOverlay(importStatus, restartApp)
        }
        composable<Import> {
            ImportScreen(importVM, onPickZipFile, restartApp)
        }
        composable<ConnectionError> {
            ConnectionErrorScreen(navController)
        }
        composable<ManageUsers> {
            ManageUsersScreen(adminVM, sessionVM)
        }
        composable<PasswordChange> {
            PasswordChangeScreen(settingsVM, sessionVM)
        }

    }
}

@Serializable
object Splash

@Serializable
object Startup

@Serializable
object Home

@Serializable
object Stats

@Serializable
object TopAlbums

@Serializable
object Top

@Serializable
object Login

@Serializable
object Register

@Serializable
object SpotifyAuth

@Serializable
object Settings

@Serializable
object Import

@Serializable
object ConnectionError

@Serializable
object ManageUsers

@Serializable
object PasswordChange