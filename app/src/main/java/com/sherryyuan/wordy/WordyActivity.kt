package com.sherryyuan.wordy

import android.Manifest
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.sherryyuan.wordy.navigation.RootNavHost
import com.sherryyuan.wordy.navigation.WordyBottomNavigationBar
import com.sherryyuan.wordy.navigation.ProjectSwitcherTopAppBar
import com.sherryyuan.wordy.navigation.ProjectsListTopAppBar
import com.sherryyuan.wordy.navigation.TopAppBarStyle
import com.sherryyuan.wordy.navigation.WordyNavDestination
import com.sherryyuan.wordy.notification.NotificationWorker
import com.sherryyuan.wordy.notification.NotificationWorker.Companion.NOTIFICATION_ID
import com.sherryyuan.wordy.notification.NotificationWorker.Companion.NOTIFICATION_WORK_NAME
import com.sherryyuan.wordy.screens.projectswitcher.ProjectSwitcherSheet
import com.sherryyuan.wordy.ui.theme.WordyTheme
import com.sherryyuan.wordy.navigation.shouldShowAppBars
import com.sherryyuan.wordy.navigation.topBarStyle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        scheduleNotification()

        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()

            var showBottomSheet by remember { mutableStateOf(false) }

            WordyTheme {
                Scaffold(
                    topBar = {
                        when (navBackStackEntry?.topBarStyle()) {
                            TopAppBarStyle.PROJECT_SWITCHER ->
                                ProjectSwitcherTopAppBar(
                                    onProjectSwitcherClick = { showBottomSheet = true }
                                )

                            TopAppBarStyle.PROJECTS_LIST ->
                                ProjectsListTopAppBar(
                                    onAddProjectClick = { navController.navigate(WordyNavDestination.CreateNewProject)}
                                )

                            else -> {}
                        }
                    },
                    bottomBar = {
                        if (navBackStackEntry?.shouldShowAppBars() == true) {
                            WordyBottomNavigationBar(navController, navBackStackEntry)
                        }
                    }
                ) { contentPadding ->
                    Box {
                        RootNavHost(
                            modifier = Modifier
                                .padding(contentPadding)
                                .background(MaterialTheme.colorScheme.background),
                            navController = navController,
                        )
                        if (showBottomSheet) {
                            ProjectSwitcherSheet(
                                onDismiss = { showBottomSheet = false },
                                navController = navController,
                            )
                        }
                    }
                }
            }
        }
    }

    private fun scheduleNotification() {
        val notificationManager =
            applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (notificationManager.areNotificationsEnabled() ||
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
        ) {
            beginNotificationWork()
        } else {
            val requestPermissionLauncher =
                registerForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { isGranted: Boolean ->
                    if (isGranted) {
                        beginNotificationWork()
                    }
                }
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun beginNotificationWork() {
        val data = Data.Builder().putInt(NOTIFICATION_ID, 0).build()
        val notificationWork = OneTimeWorkRequest.Builder(NotificationWorker::class.java)
            .setInputData(data)
            .build()

        val instanceWorkManager = WorkManager.getInstance(baseContext)
        instanceWorkManager.beginUniqueWork(
            NOTIFICATION_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            notificationWork,
        ).enqueue()
    }
}
