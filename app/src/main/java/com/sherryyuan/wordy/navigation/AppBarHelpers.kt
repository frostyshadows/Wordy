package com.sherryyuan.wordy.navigation

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy

fun NavBackStackEntry.topBarStyle() = when {
    !shouldShowAppBars() -> TopAppBarStyle.NONE
    destination.isOnDestination(WordyNavDestination.ProjectDetail::class.simpleName) -> TopAppBarStyle.NONE
    destination.isOnDestination(WordyNavDestination.ProjectsList::class.simpleName) -> TopAppBarStyle.PROJECTS_LIST
    else -> TopAppBarStyle.PROJECT_SWITCHER
}

fun NavBackStackEntry.shouldShowAppBars() =
    !destination.isOnDestination(WordyNavDestination.Root::class.simpleName) &&
            !destination.isOnDestination(WordyNavDestination.Welcome::class.simpleName) &&
            !destination.isOnDestination(WordyNavDestination.CreateNewProject::class.simpleName) &&
            !destination.isOnDestination(WordyNavDestination.CreateDefaultProject::class.simpleName)

fun NavDestination.isOnDestination(destinationClassName: String?): Boolean =
    destinationClassName != null &&
            hierarchy.any { it.route.orEmpty().contains(destinationClassName) }
