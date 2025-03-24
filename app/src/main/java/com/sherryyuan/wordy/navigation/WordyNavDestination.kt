package com.sherryyuan.wordy.navigation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

sealed interface WordyNavDestination {

    @Serializable
    data object Root : WordyNavDestination

    @Serializable
    data object Welcome : WordyNavDestination

    @Serializable
    data object CreateNewProject :
        WordyNavDestination // TODO also use for editing existing project?

    // Default project for tracking a daily word count goal without a specific project.
    @Serializable
    data object CreateDefaultProject : WordyNavDestination

    @Serializable
    data object Entries : WordyNavDestination

    @Serializable
    data object Home : WordyNavDestination

    @Serializable
    data object ProjectsList : WordyNavDestination

    @Serializable
    data class ProjectDetail(
        @SerialName(NAV_ARG_PROJECT_ID)
        val projectId: Long,
    ) : WordyNavDestination

    companion object {
        const val NAV_ARG_PROJECT_ID = "nav_arg_project_id"
    }
}
