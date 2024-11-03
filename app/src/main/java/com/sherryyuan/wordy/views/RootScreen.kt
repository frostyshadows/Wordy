package com.sherryyuan.wordy.views

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.sherryyuan.wordy.viewmodels.RootScreenViewModel
import kotlinx.coroutines.flow.first

@Composable
fun RootScreen(
    navController: NavHostController,
    viewModel: RootScreenViewModel = hiltViewModel<RootScreenViewModel>(),
) {
    LaunchedEffect(Unit) {
        val landingScreen = viewModel.landingScreen.first()
        navController.navigate(landingScreen)
    }
}
