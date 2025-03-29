package com.sherryyuan.wordy.utils

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.sherryyuan.wordy.navigation.WordyNavDestination


fun NavBackStackEntry.shouldShowBottomAppBar() =
    !destination.isOnDestination(WordyNavDestination.Root::class.simpleName) &&
            !destination.isOnDestination(WordyNavDestination.Welcome::class.simpleName) &&
            !destination.isOnDestination(WordyNavDestination.CreateNewProject::class.simpleName) &&
            !destination.isOnDestination(WordyNavDestination.CreateDefaultProject::class.simpleName)

fun NavDestination.isOnDestination(destinationClassName: String?): Boolean =
    destinationClassName != null &&
            hierarchy.any { it.route.orEmpty().contains(destinationClassName) }
