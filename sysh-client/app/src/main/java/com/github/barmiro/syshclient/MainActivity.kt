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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.barmiro.syshclient.presentation.top.TopTracksScreen
import com.github.barmiro.syshclient.presentation.top.TopTracksViewModel
import com.github.barmiro.syshclient.ui.theme.SyshClientTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
//    @Inject lateinit var statsRepo: StatsRepository
//    @Inject lateinit var topRepo: TopRepository
    private val topTracksVM: TopTracksViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SyshClientTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = MaterialTheme.colorScheme.background)
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = MainScreen
                    ) {
                        composable<MainScreen> {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Button(onClick = {
                                    navController.navigate(TopTracks)
                                }) {
                                    Text(text = "Let's go!")
                                }
                            }
                        }
                        composable<TopTracks> {
                            TopTracksScreen(topTracksVM)
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

@Serializable
object MainScreen

@Serializable
object TopTracks


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