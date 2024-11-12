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
    data object CreateNewProject: NavDestination // TODO also use for editing existing project?

    // Default project for tracking a daily word count goal without a specific project.
    @Serializable
    data object CreateDefaultProject: NavDestination
}
