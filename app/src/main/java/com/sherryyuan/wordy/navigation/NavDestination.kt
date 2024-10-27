package com.sherryyuan.wordy.navigation

import kotlinx.serialization.Serializable

sealed interface NavDestination {

    @Serializable
    data object Root: NavDestination

    @Serializable
    data object Welcome: NavDestination

    @Serializable
    data object Home: NavDestination

    @Serializable
    data object CreateDailyWordCount: NavDestination
}
