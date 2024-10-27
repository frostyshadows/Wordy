package com.sherryyuan.wordy.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController

@Composable
@ReadOnlyComposable
fun previewNavController() = NavHostController(LocalContext.current)
