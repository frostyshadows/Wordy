package com.sherryyuan.wordy.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sherryyuan.wordy.views.CreateDailyWordCountScreen
import com.sherryyuan.wordy.views.HomeScreen
import com.sherryyuan.wordy.views.RootScreen
import com.sherryyuan.wordy.views.WelcomeScreen

@Composable
fun RootNavHost() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = NavDestination.Root,
    ) {
        composable<NavDestination.Root> {
            RootScreen(navController)
        }
        composable<NavDestination.Welcome> {
            WelcomeScreen(navController)
        }
        composable<NavDestination.Home> {
            HomeScreen()
        }
        composable<NavDestination.CreateDailyWordCount> {
            CreateDailyWordCountScreen(navController)
        }
    }
}
