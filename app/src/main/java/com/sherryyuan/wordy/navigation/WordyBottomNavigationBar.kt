package com.sherryyuan.wordy.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.sherryyuan.wordy.R
import com.sherryyuan.wordy.utils.isOnDestination

@Composable
fun WordyBottomNavigationBar(
    navController: NavHostController,
    navBackStack: NavBackStackEntry?,
) {
    val bottomScreens = remember {
        mapOf(
            WordyNavDestination.Entries to R.drawable.list_icon,
            WordyNavDestination.Home to R.drawable.pen_icon,
            WordyNavDestination.Projects to R.drawable.project_icon,
        )
    }

    NavigationBar {
        bottomScreens.forEach { (screen, icon) ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = null,
                    )
                },
                selected = navBackStack?.destination?.isOnDestination(screen) == true,
                onClick = {
                    navController.navigate(screen) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
