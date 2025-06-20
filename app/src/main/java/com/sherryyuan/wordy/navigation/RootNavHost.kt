package com.sherryyuan.wordy.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.End
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.Start
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sherryyuan.wordy.navigation.WordyNavDestination.Companion.NAV_ARG_IS_ONBOARDING
import com.sherryyuan.wordy.screens.defaultproject.CreateDefaultProjectScreen
import com.sherryyuan.wordy.screens.entries.EntriesScreen
import com.sherryyuan.wordy.screens.home.HomeScreen
import com.sherryyuan.wordy.screens.newproject.CreateNewProjectScreen
import com.sherryyuan.wordy.screens.onboarding.RootScreen
import com.sherryyuan.wordy.screens.onboarding.WelcomeScreen
import com.sherryyuan.wordy.screens.projectdetail.ProjectDetailScreen
import com.sherryyuan.wordy.screens.projectslist.ProjectsListScreen
import com.sherryyuan.wordy.utils.TOP_BAR_ANIMATION_KEY
import kotlinx.serialization.SerialName
import kotlin.reflect.KType

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun RootNavHost(
    navController: NavHostController,
    onProjectSwitcherClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SharedTransitionLayout(modifier = modifier) {
        NavHost(
            navController = navController,
            startDestination = WordyNavDestination.Root,
        ) {
            composable<WordyNavDestination.Root> {
                RootScreen(
                    onNavigateToLandingScreen = {
                        navController.navigate(it) {
                            popUpTo(WordyNavDestination.Root) {
                                inclusive = true
                            }
                        }
                    }
                )
            }
            composableWithDefaultTransitions<WordyNavDestination.Welcome> {
                WelcomeScreen(
                    onNavigateToNewProject = {
                        navController.navigate(
                            WordyNavDestination.CreateNewProject(isOnboarding = true)
                        )
                    },
                    onNavigateToDefaultProject = {
                        navController.navigate(
                            WordyNavDestination.CreateDefaultProject(isOnboarding = true)
                        )
                    }
                )
            }
            composable<WordyNavDestination.CreateNewProject> {
                CreateNewProjectScreen(
                    onNavigateAfterSubmit = {
                        val isOnboarding = it.arguments?.getBoolean(NAV_ARG_IS_ONBOARDING) == true
                        navController.navigate(WordyNavDestination.Home) {
                            popUpTo(if (isOnboarding) WordyNavDestination.Welcome else WordyNavDestination.CreateNewProject()) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable<WordyNavDestination.CreateDefaultProject> {
                CreateDefaultProjectScreen(
                    onNavigateAfterSubmit = {
                        val isOnboarding = it.arguments?.getBoolean(NAV_ARG_IS_ONBOARDING) == true
                        navController.navigate(WordyNavDestination.Home) {
                            popUpTo(if (isOnboarding) WordyNavDestination.Welcome else WordyNavDestination.CreateDefaultProject()) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable<WordyNavDestination.Entries> {
                EntriesScreen(
                    topBar = {
                        ProjectSwitcherTopAppBar(
                            modifier = Modifier
                                .sharedElement(
                                    state = rememberSharedContentState(key = TOP_BAR_ANIMATION_KEY),
                                    animatedVisibilityScope = this@composable,
                                )
                                .skipToLookaheadSize(),
                            onProjectSwitcherClick = onProjectSwitcherClick,
                        )
                    }
                )
            }
            composableWithDefaultTransitions<WordyNavDestination.Home> {
                HomeScreen(
                    topBar = {
                        ProjectSwitcherTopAppBar(
                            modifier = Modifier
                                .sharedElement(
                                    state = rememberSharedContentState(key = TOP_BAR_ANIMATION_KEY),
                                    animatedVisibilityScope = this@composableWithDefaultTransitions,
                                )
                                .skipToLookaheadSize(),
                            onProjectSwitcherClick = onProjectSwitcherClick,
                        )
                    }
                )
            }
            composable<WordyNavDestination.ProjectsList> {
                ProjectsListScreen(
                    onNavigateToAddProject = {
                        navController.navigate(WordyNavDestination.CreateNewProject())
                    },
                    onNavigateToProjectDetail = {
                        navController.navigate(WordyNavDestination.ProjectDetail(it))
                    },
                    topBarAnimatedVisibilityScope = this@composable,
                )
            }
            composable<WordyNavDestination.ProjectDetail> {
                ProjectDetailScreen(
                    onNavigateToProjectsList = {
                        navController.navigate(WordyNavDestination.ProjectsList)
                    },
                    topBarAnimatedVisibilityScope = this@composable,
                )
            }
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
