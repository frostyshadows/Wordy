package com.sherryyuan.wordy.utils

import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.sherryyuan.wordy.navigation.WordyNavDestination

fun NavDestination.isOnDestination(destination: WordyNavDestination): Boolean =
    hierarchy.any { it.route.orEmpty().contains(destination.javaClass.simpleName) }
