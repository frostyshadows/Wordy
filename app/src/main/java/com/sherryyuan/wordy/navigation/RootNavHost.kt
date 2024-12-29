package com.sherryyuan.wordy.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.End
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.Start
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sherryyuan.wordy.defaultproject.CreateDefaultProjectScreen
import com.sherryyuan.wordy.entries.EntriesScreen
import com.sherryyuan.wordy.home.HomeScreen
import com.sherryyuan.wordy.newproject.CreateNewProjectScreen
import com.sherryyuan.wordy.onboarding.RootScreen
import com.sherryyuan.wordy.onboarding.WelcomeScreen
import kotlin.reflect.KType

@Composable
fun RootNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = WordyNavDestination.Root,
    ) {
        composable<WordyNavDestination.Root> {
            RootScreen(navController)
        }
        composableWithDefaultTransitions<WordyNavDestination.Welcome> {
            WelcomeScreen(navController)
        }
        composable<WordyNavDestination.Entries> {
            EntriesScreen()
        }
        composableWithDefaultTransitions<WordyNavDestination.Home> {
            HomeScreen()
        }
        composable<WordyNavDestination.Projects> {
            Text("projects")
        }
        composable<WordyNavDestination.CreateNewProject> {
            CreateNewProjectScreen(navController)
        }
        composable<WordyNavDestination.CreateDefaultProject> {
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
