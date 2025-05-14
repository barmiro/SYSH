package com.github.barmiro.syshclient.presentation.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.github.barmiro.syshclient.R

@Composable
fun BottomNavBar(navController: NavHostController) {


    val navItems = listOf(
        BottomNavigationItem(
            title = "Home",
            selectedIcon = rememberVectorPainter(Icons.Filled.Home),
            unselectedIcon = rememberVectorPainter(Icons.Outlined.Home),
            navigateTo = Home
        ),
        BottomNavigationItem(
            title = "Top",
            selectedIcon = painterResource(R.drawable.leaderboard_filled_24dp),
            unselectedIcon = painterResource(R.drawable.leaderboard_24dp),
            navigateTo = Top
        ),
        BottomNavigationItem(
            title = "Stats",
            selectedIcon = painterResource(R.drawable.insert_chart_24dp),
            unselectedIcon = painterResource(R.drawable.show_chart_24dp),
            navigateTo = Stats
        ),
        BottomNavigationItem(
            title = "Settings",
            selectedIcon = rememberVectorPainter(Icons.Filled.Settings),
            unselectedIcon = rememberVectorPainter(Icons.Outlined.Settings),
            navigateTo = Settings
        )
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
    ) {
        navItems.forEach { item ->
            val isSelected = currentDestination?.route?.substringAfterLast('.') == item.title
            NavigationBarItem(
                colors = NavigationBarItemColors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    selectedIndicatorColor = Color.Transparent,
                    unselectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
                    unselectedTextColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
                    disabledIconColor = MaterialTheme.colorScheme.error,
                    disabledTextColor = MaterialTheme.colorScheme.error
                ),
                selected = isSelected,
                onClick = {
                    navController.navigate(item.navigateTo) {
                        popUpTo(currentDestination?.id ?: 0) {
                            inclusive = true
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        painter = if (isSelected) {
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

data class BottomNavigationItem(
    val title: String,
    val selectedIcon: Painter,
    val unselectedIcon: Painter,
    val navigateTo: Any
)