package com.sherryyuan.wordy.screens.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.sherryyuan.wordy.navigation.WordyNavDestination
import kotlinx.coroutines.flow.first

@Composable
fun RootScreen(
    onNavigateToLandingScreen: (WordyNavDestination) -> Unit,
    viewModel: RootScreenViewModel = hiltViewModel<RootScreenViewModel>(),
) {
    LaunchedEffect(Unit) {
        val landingScreen = viewModel.landingScreen.first()
        onNavigateToLandingScreen(landingScreen)
    }
}
