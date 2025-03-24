package com.sherryyuan.wordy.screens.onboarding

import androidx.lifecycle.ViewModel
import com.sherryyuan.wordy.navigation.WordyNavDestination
import com.sherryyuan.wordy.repositories.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class RootScreenViewModel @Inject constructor(
    projectRepository: ProjectRepository,
) : ViewModel() {

    val landingScreen: Flow<WordyNavDestination> = projectRepository.getProjects()
        .map {
            if (it.isEmpty()) {
                WordyNavDestination.Welcome
            } else {
                WordyNavDestination.Home
            }
        }
}
