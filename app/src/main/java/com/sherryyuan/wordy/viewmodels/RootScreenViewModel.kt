package com.sherryyuan.wordy.viewmodels

import androidx.lifecycle.ViewModel
import com.sherryyuan.wordy.navigation.NavDestination
import com.sherryyuan.wordy.repositories.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class RootScreenViewModel @Inject constructor(
    projectRepository: ProjectRepository
) : ViewModel() {

    val landingScreen: Flow<NavDestination> = projectRepository.getProjects()
        .map {
            if (it.isEmpty()) {
                NavDestination.Welcome
            } else {
                NavDestination.Home
            }
        }
}
