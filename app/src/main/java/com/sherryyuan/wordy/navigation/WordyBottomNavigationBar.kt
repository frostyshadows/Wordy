package com.sherryyuan.wordy.navigation

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.sherryyuan.wordy.R
import com.sherryyuan.wordy.utils.isOnDestination

@Composable
fun WordyBottomNavigationBar(
    navController: NavHostController,
    navBackStack: NavBackStackEntry?,
    modifier: Modifier = Modifier,
) {
    val bottomNavScreens = remember {
        listOf(
            BottomNavScreen(
                iconRes = R.drawable.list_icon,
                defaultDestination = WordyNavDestination.Entries,
            ),
            BottomNavScreen(
                iconRes = R.drawable.pen_icon,
                defaultDestination = WordyNavDestination.Home,
            ),
            BottomNavScreen(
                iconRes = R.drawable.project_icon,
                defaultDestination = WordyNavDestination.ProjectsList,
                otherDestinationClassNames = listOf(WordyNavDestination.ProjectDetail::class.simpleName)
            ),
        )
    }

    Column(modifier) {
        HorizontalDivider()
        NavigationBar {
            bottomNavScreens.forEach { screen ->
                val isOnDefaultDestination =
                    navBackStack?.destination?.isOnDestination(screen.defaultDestination::class.simpleName) == true
                val isOnOtherDestination =
                    screen.otherDestinationClassNames.any {
                        navBackStack?.destination?.isOnDestination(it) == true
                    }
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(screen.iconRes),
                            contentDescription = null,
                        )
                    },
                    selected = isOnDefaultDestination || isOnOtherDestination,
                    onClick = {
                        navController.navigate(screen.defaultDestination) {
                            navController.popBackStack()
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}

data class BottomNavScreen(
    @DrawableRes val iconRes: Int,
    val defaultDestination: WordyNavDestination,
    val otherDestinationClassNames: List<String?> = emptyList(),
)
