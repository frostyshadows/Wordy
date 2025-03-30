package com.sherryyuan.wordy.screens.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.sherryyuan.wordy.navigation.WordyNavDestination
import kotlinx.coroutines.flow.first

@Composable
fun RootScreen(
    navController: NavHostController,
    viewModel: RootScreenViewModel = hiltViewModel<RootScreenViewModel>(),
) {
    LaunchedEffect(Unit) {
        val landingScreen = viewModel.landingScreen.first()
        navController.navigate(landingScreen) {
            popUpTo(WordyNavDestination.Root) {
                inclusive = true
            }
        }
    }
}
