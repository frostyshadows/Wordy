package com.sherryyuan.wordy.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.End
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.Start
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sherryyuan.wordy.views.CreateDefaultProjectScreen
import com.sherryyuan.wordy.views.HomeScreen
import com.sherryyuan.wordy.views.RootScreen
import com.sherryyuan.wordy.views.WelcomeScreen
import kotlin.reflect.KType

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
        composableWithDefaultTransitions<NavDestination.Welcome> {
            WelcomeScreen(navController)
        }
        composableWithDefaultTransitions<NavDestination.Home> {
            HomeScreen()
        }
        composable<NavDestination.CreateDefaultProject> {
            CreateDefaultProjectScreen(navController)
        }
    }
}

private inline fun <reified T : Any> NavGraphBuilder.composableWithDefaultTransitions(
    typeMap: Map<KType, @JvmSuppressWildcards NavType<*>> = emptyMap(),
    deepLinks: List<NavDeepLink> = emptyList(),
    noinline content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) = composable<T>(
    typeMap = typeMap,
    deepLinks = deepLinks,
    enterTransition = { slideIntoContainer(towards = Start) },
    popEnterTransition = { slideIntoContainer(towards = End) },
    exitTransition = { slideOutOfContainer(towards = Start) },
    popExitTransition = { slideOutOfContainer(towards = End) },
    content = content,
)
