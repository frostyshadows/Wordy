package com.sherryyuan.wordy

import android.Manifest
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.sherryyuan.wordy.NotificationWorker.Companion.NOTIFICATION_ID
import com.sherryyuan.wordy.NotificationWorker.Companion.NOTIFICATION_WORK_NAME
import com.sherryyuan.wordy.navigation.RootNavHost
import com.sherryyuan.wordy.ui.theme.WordyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        scheduleNotification()

        enableEdgeToEdge()
        setContent {
            WordyTheme {
                RootNavHost()
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
