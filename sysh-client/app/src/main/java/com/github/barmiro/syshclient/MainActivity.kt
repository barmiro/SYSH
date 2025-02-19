package com.github.barmiro.syshclient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.barmiro.syshclient.presentation.top.albums.TopAlbumsScreen
import com.github.barmiro.syshclient.presentation.top.albums.TopAlbumsViewModel
import com.github.barmiro.syshclient.presentation.top.tracks.TopTracksScreen
import com.github.barmiro.syshclient.presentation.top.tracks.TopTracksViewModel
import com.github.barmiro.syshclient.ui.theme.SyshClientTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
//    @Inject lateinit var statsRepo: StatsRepository
//    @Inject lateinit var topRepo: TopRepository
    private val topTracksVM: TopTracksViewModel by viewModels()
    private val topAlbumsVM: TopAlbumsViewModel by viewModels()
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
                                    val pagerState = rememberPagerState(pageCount = { 2 })
//                                    not using lifecycleScope because the animations need a MonotonicFrameClock
                                    val coroutineScope = rememberCoroutineScope()
                                    val topNavItems = listOf("Tracks", "Albums")

                                    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 40.dp)) {
                                        HorizontalPager(state = pagerState) { page ->
                                            when (page) {
                                                0 -> TopTracksScreen(topTracksVM)
                                                1 -> TopAlbumsScreen(topAlbumsVM)
                                                else -> Text("Something went wrong with the pager")
                                            }
                                        }
                                    }
                                    Box (modifier = Modifier.fillMaxSize()) {
                                        Column(modifier = Modifier.fillMaxSize(),
                                            verticalArrangement = Arrangement.Bottom) {

                                            Row(modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.Center,
                                                verticalAlignment = Alignment.Bottom) {
                                                SingleChoiceSegmentedButtonRow {
                                                    topNavItems.forEachIndexed { index, label ->
                                                        SegmentedButton(
                                                            shape = RectangleShape,
                                                            onClick = {
                                                                coroutineScope.launch {
                                                                    pagerState.animateScrollToPage(index)
                                                                }
                                                            },
                                                            selected = index == pagerState.currentPage,
                                                            modifier = Modifier.height(30.dp)
                                                        ) {
                                                            Text(text = label,
                                                                fontSize = 14.sp,
                                                                lineHeight = 14.sp)
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

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

                    //                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    //                    Greeting(
                    //                        name = "Android",
                    //                        modifier = Modifier.padding(innerPadding)
                    //                    )
                    //                }
                }
            }
        }
//        lifecycleScope.launch {
//            topRepo.test()
//            statsRepo.getStats()
//        }

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