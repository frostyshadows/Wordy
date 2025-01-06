package com.sherryyuan.wordy.utils

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.sherryyuan.wordy.navigation.WordyNavDestination

fun NavBackStackEntry.shouldShowAppBars() =
    !destination.isOnDestination(WordyNavDestination.Root) &&
            !destination.isOnDestination(WordyNavDestination.Welcome) &&
            !destination.isOnDestination(WordyNavDestination.CreateNewProject) &&
            !destination.isOnDestination(WordyNavDestination.CreateDefaultProject)

fun NavDestination.isOnDestination(destination: WordyNavDestination): Boolean =
    hierarchy.any { it.route.orEmpty().contains(destination.javaClass.simpleName) }
