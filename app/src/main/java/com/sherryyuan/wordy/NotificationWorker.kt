package com.sherryyuan.wordy

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import androidx.core.content.ContextCompat.getString
import androidx.work.ListenableWorker.Result.success
import androidx.work.Worker
import androidx.work.WorkerParameters


class NotificationWorker(
    private val context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    private val notificationManager =
        applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

    override fun doWork(): Result {
        createNotificationChannel()
        sendNotification()

        return success()
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(context, R.string.notification_channel_name)
            val descriptionText = getString(context, R.string.notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager =
                applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification() {
        val id = inputData.getLong(NOTIFICATION_ID, 0).toInt()
        val openAppIntent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
            putExtra(NOTIFICATION_ID, id)
        }
        val openAppPendingIntent =
            getActivity(applicationContext, 0, openAppIntent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("X words today")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(openAppPendingIntent)
            .addAction(createAddWordsAction())
            // .setRemoteInputHistory(emptyArray())

        notificationManager.notify(id, notification.build())
    }

    private fun createAddWordsAction(): NotificationCompat.Action {
        val intent = Intent(applicationContext, NotificationInputReceiver::class.java).apply {
            action = ADD_WORD_COUNT_ACTION
            putExtra(NOTIFICATION_ID, id)
        }
        val addWordsPendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            NOTIFICATION_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_MUTABLE,
        )
        val remoteInput = RemoteInput.Builder(KEY_ADD_WORD_COUNT)
            .setLabel(context.getString(R.string.notification_add_words_label))
            .build()

        return NotificationCompat.Action.Builder(
            R.drawable.ic_launcher_background,
            getString(context, R.string.notification_add_words_label),
            addWordsPendingIntent,
        )
            .addRemoteInput(remoteInput)
            .build()
    }

    companion object {
        const val NOTIFICATION_WORK_NAME = "wordy_notification_work"
        const val NOTIFICATION_ID = "wordy_notification_id"
        const val NOTIFICATION_REQUEST_CODE = 100
        const val ADD_WORD_COUNT_ACTION = "add_word_count_action"
        const val KEY_ADD_WORD_COUNT = "key_add_word_count"

        private const val NOTIFICATION_CHANNEL_ID = "wordy_notification_channel_id"
    }
}
