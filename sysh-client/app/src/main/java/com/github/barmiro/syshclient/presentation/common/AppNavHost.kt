package com.github.barmiro.syshclient.presentation.common

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.github.barmiro.syshclient.presentation.home.HomeScreen
import com.github.barmiro.syshclient.presentation.home.HomeViewModel
import com.github.barmiro.syshclient.presentation.login.LoginScreen
import com.github.barmiro.syshclient.presentation.login.RegisterScreen
import com.github.barmiro.syshclient.presentation.login.SessionViewModel
import com.github.barmiro.syshclient.presentation.login.SpotifyAuthScreen
import com.github.barmiro.syshclient.presentation.settings.SettingsScreen
import com.github.barmiro.syshclient.presentation.settings.admin.AdminViewModel
import com.github.barmiro.syshclient.presentation.settings.admin.ManageUsersScreen
import com.github.barmiro.syshclient.presentation.settings.import.ImportScreen
import com.github.barmiro.syshclient.presentation.settings.ImportViewModel
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
               onPickZipFile: () -> Unit,
               restartApp: () -> Unit) {

    NavHost(
        navController = navController,
        startDestination = Splash
    ) {
        composable<Splash> {
            SplashScreen()
        }

        composable<Startup> {
            StartupScreen(startupVM, sessionVM, navController)
        }

        composable<Login> {
            LoginScreen(sessionVM, navController)
        }
        composable<Register> {
            RegisterScreen(sessionVM, navController)
        }
        composable<Home> {
            HomeScreen(homeVM)
        }
        composable<Top> {
            TopScreen(topScreenVM, topTracksVM, topAlbumsVM, topArtistsVM)
        }
        composable<Stats> {
            StatsScreen(statsVM)
        }
        composable<TopAlbums> {
            TopAlbumsScreen(topAlbumsVM)
        }
        composable<SpotifyAuth> {
            SpotifyAuthScreen(sessionVM)
        }
        composable<Settings> {
            SettingsScreen(sessionVM, navController)
        }
        composable<Import> {
            ImportScreen(importVM, onPickZipFile, restartApp)
        }
        composable<ConnectionError> {
            ConnectionErrorScreen(navController)
        }
        composable<ManageUsers> {
            ManageUsersScreen(adminVM)
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