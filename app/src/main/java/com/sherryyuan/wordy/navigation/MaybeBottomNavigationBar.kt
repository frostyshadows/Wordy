package com.sherryyuan.wordy.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.sherryyuan.wordy.R
import com.sherryyuan.wordy.utils.isOnDestination

@Composable
fun MaybeBottomNavigationBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    if (navBackStackEntry?.shouldShowNavigationBar() != true) {
        return
    }
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
                selected = navBackStackEntry?.destination?.isOnDestination(screen) == true,
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

private fun NavBackStackEntry.shouldShowNavigationBar() =
    !destination.isOnDestination(WordyNavDestination.Root) &&
            !destination.isOnDestination(WordyNavDestination.Welcome) &&
            !destination.isOnDestination(WordyNavDestination.CreateNewProject) &&
            !destination.isOnDestination(WordyNavDestination.CreateDefaultProject)
