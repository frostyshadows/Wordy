package com.sherryyuan.wordy.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.sherryyuan.wordy.R

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val bottomScreens = remember {
        mapOf(
            NavDestination.Logs to R.drawable.list_icon,
            NavDestination.Root to R.drawable.pen_icon,
            NavDestination.Projects to R.drawable.project_icon,
        )
    }

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        bottomScreens.forEach { (screen, icon) ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = null,
                    )
                },
                // TODO this works for logs and projects, but not root since it's dynamic
                selected = currentDestination?.hierarchy?.any { it.route.orEmpty().contains(screen.javaClass.simpleName) } == true,
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
