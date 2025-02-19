package com.github.barmiro.syshclient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.barmiro.syshclient.presentation.top.TopScreen
import com.github.barmiro.syshclient.presentation.top.albums.TopAlbumsScreen
import com.github.barmiro.syshclient.presentation.top.albums.TopAlbumsViewModel
import com.github.barmiro.syshclient.presentation.top.artists.TopArtistsViewModel
import com.github.barmiro.syshclient.presentation.top.tracks.TopTracksScreen
import com.github.barmiro.syshclient.presentation.top.tracks.TopTracksViewModel
import com.github.barmiro.syshclient.ui.theme.SyshClientTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
//    @Inject lateinit var statsRepo: StatsRepository
//    @Inject lateinit var topRepo: TopRepository
    private val topTracksVM: TopTracksViewModel by viewModels()
    private val topAlbumsVM: TopAlbumsViewModel by viewModels()
    private val topArtistsVM: TopArtistsViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SyshClientTheme {

                val navItems = listOf(
                    BottomNavigationItem(
                        title = "Home",
                        selectedIcon = Icons.Filled.Home,
                        unselectedIcon = Icons.Outlined.Home,
                        navigateTo = MainScreen
                    ),
                    BottomNavigationItem(
                        title = "Top",
                        selectedIcon = Icons.Filled.Star,
                        unselectedIcon = Icons.Outlined.Star,
                        navigateTo = Top
                    ),
                    BottomNavigationItem(
                        title = "Stats",
                        selectedIcon = Icons.Filled.Info,
                        unselectedIcon = Icons.Outlined.Info,
                        navigateTo = TopAlbums
                    ),
                    BottomNavigationItem(
                        title = "Settings",
                        selectedIcon = Icons.Filled.Settings,
                        unselectedIcon = Icons.Outlined.Settings,
                        navigateTo = MainScreen
                    )
                )

                var selectedNavItemIndex by rememberSaveable {
                    mutableIntStateOf(0)
                }
                val navController = rememberNavController()

                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = MaterialTheme.colorScheme.background)
                ) {
                    Scaffold(
                        bottomBar = {
                            NavigationBar {
                                navItems.forEachIndexed { index, item ->
                                    NavigationBarItem(
                                        selected = selectedNavItemIndex == index,
                                        onClick = {
                                            selectedNavItemIndex = index
                                            navController.navigate(item.navigateTo)
                                        },
                                        icon = {
                                            Icon(
                                                imageVector =
                                                    if (index == selectedNavItemIndex) {
                                                        item.selectedIcon
                                                    } else {
                                                        item.unselectedIcon
                                                },
                                                contentDescription = item.title

                                            )
                                        },
                                        label = {
                                            Text(item.title)
                                        }
                                    )
                                }
                            }
                        }
                    ) { innerPadding ->
                        Column(modifier = Modifier.padding(innerPadding)){
                            NavHost(
                                navController = navController,
                                startDestination = MainScreen
                            ) {
                                composable<MainScreen> {
                                    Column(
                                        modifier = Modifier.fillMaxSize().padding(32.dp),
                                    ) {
                                        Row(

                                        ) {
                                            Text(text = "Welcome to SYSH",
                                                fontSize = 32.sp,
                                                color = MaterialTheme.colorScheme.onBackground)
                                        }
                                        Row(

                                        ) {
                                            Text(text = "This is where your stats will be",
                                                fontSize = 24.sp,
                                                color = MaterialTheme.colorScheme.onBackground)
                                        }
                                        Button(onClick = {
                                            navController.navigate(Top)
                                        }) {
                                            Text(text = "Let's go!")
                                        }
                                    }
                                }
                                composable<Top> {
                                    TopScreen(topTracksVM, topAlbumsVM, topArtistsVM)
                                }
                                composable<TopTracks> {
                                    TopTracksScreen(topTracksVM)
                                }
                                composable<TopAlbums> {
                                    TopAlbumsScreen(topAlbumsVM)
                                }
                            }
                        }

                    }
                }
            }
        }
    }
}

data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val navigateTo: Any
)

@Serializable
object MainScreen

@Serializable
object TopTracks

@Serializable
object TopAlbums

@Serializable
object Top


//@Composable
//fun Greeting(name: String, modifier: Modifier = Modifier) {
//    Text(
//        text = "Hello $name!",
//        modifier = modifier
//    )
//}
//
//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    SyshClientTheme {
//        Greeting("Android")
//    }
//}