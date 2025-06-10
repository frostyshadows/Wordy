package com.sherryyuan.wordy.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker.Result.success
import androidx.work.WorkerParameters
import com.sherryyuan.wordy.MainActivity
import com.sherryyuan.wordy.R
import com.sherryyuan.wordy.entitymodels.Goal
import com.sherryyuan.wordy.repositories.EntryRepository
import com.sherryyuan.wordy.repositories.ProjectRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import java.time.LocalDate

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    private val projectRepository: ProjectRepository,
    private val entryRepository: EntryRepository,
    private val notificationConfig: NotificationConfig,
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
) : CoroutineWorker(context, params) {

    private val notificationManager =
        applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

    override suspend fun doWork(): Result {
        createNotificationChannel()
        sendNotification()

        return success()
    }

    private fun createNotificationChannel() {
        val name = context.getString(R.string.notification_channel_name)
        val descriptionText = context.getString(R.string.notification_channel_description)
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            name,
            NotificationManager.IMPORTANCE_LOW,
        ).apply {
            description = descriptionText
        }
        val notificationManager =
            applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private suspend fun sendNotification() {
        val id = inputData.getLong(NOTIFICATION_ID, 0).toInt()
        val openAppIntent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
            putExtra(NOTIFICATION_ID, id)
        }
        val openAppPendingIntent =
            getActivity(applicationContext, 0, openAppIntent, PendingIntent.FLAG_IMMUTABLE)

        combine(
            projectRepository.getSelectedProject(),
            entryRepository.getEntries(),
            notificationConfig.showRemoteInputWarningFlow,
        ) { selectedProject, entries, showRemoteInputWarning ->
            val selectedProjectEntries = entries.filter { it.projectId == selectedProject?.id }
            val wordCount = selectedProjectEntries
                .firstOrNull { it.date == LocalDate.now() }
                ?.wordCount
                ?: 0
            if (selectedProject == null) return@combine

            val wordCountGoal = when (val goal = selectedProject.goal) {
                is Goal.DailyWordCountGoal -> goal.initialDailyWordCount
                is Goal.DeadlineGoal -> goal.adjustedDailyWordCount(selectedProjectEntries)
            }
            val title = buildString {
                append(
                    context.getString(
                        R.string.words_today_message_template,
                        wordCount,
                        wordCountGoal,
                    )
                )
                if (wordCount >= wordCountGoal) {
                    append(" • ")
                    append(context.getString(R.string.goal_achieved))
                    append(" \uD83C\uDF89") // 🎉
                } else {
                    val remainingWordCount = wordCountGoal - wordCount
                    append(" • ")
                    append(
                        context.getString(R.string.words_to_go_message, remainingWordCount)
                    )
                }
            }
            val addWordCountAction = createRemoteInputAction(
                intentAction = ADD_WORD_COUNT_ACTION,
                remoteInputKey = KEY_ADD_WORD_COUNT,
                remoteInputLabel = R.string.notification_add_words_label,
            )
            val updateWordCountAction = createRemoteInputAction(
                intentAction = UPDATE_WORD_COUNT_ACTION,
                remoteInputKey = KEY_UPDATE_WORD_COUNT,
                remoteInputLabel = R.string.notification_update_words_label,
            )
            val notification =
                NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle(title)
                    .maybeSetMessage(
                        context.getString(
                            R.string.in_project_message_template,
                            selectedProject.title,
                        )
                    )
                    .maybeSetProgress(wordCountGoal, wordCount)
                    .maybeSetRemoteInputWarning(showRemoteInputWarning)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(false)
                    .setContentIntent(openAppPendingIntent)
                    .addAction(addWordCountAction)
                    .addAction(updateWordCountAction)

            notificationManager.notify(id, notification.build())
        }.collect()
    }

    private fun createRemoteInputAction(
        intentAction: String,
        remoteInputKey: String,
        @StringRes remoteInputLabel: Int,
    ): NotificationCompat.Action {
        val intent = Intent(applicationContext, NotificationBroadcastReceiver::class.java).apply {
            action = intentAction
            putExtra(NOTIFICATION_ID, id)
        }
        val addWordsPendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            NOTIFICATION_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_MUTABLE,
        )
        val remoteInput = RemoteInput.Builder(remoteInputKey)
            .setLabel(context.getString(remoteInputLabel))
            .build()

        return NotificationCompat.Action.Builder(
            R.drawable.ic_launcher_background,
            context.getString(remoteInputLabel),
            addWordsPendingIntent,
        )
            .addRemoteInput(remoteInput)
            .build()
    }

    private fun NotificationCompat.Builder.maybeSetMessage(message: String?): NotificationCompat.Builder {
        return message?.let {
            setContentText(it)
        } ?: this
    }

    private fun NotificationCompat.Builder.maybeSetProgress(
        max: Int?,
        progress: Int
    ): NotificationCompat.Builder {
        return max?.let {
            setProgress(it, progress, false)
        } ?: this
    }

    private fun NotificationCompat.Builder.maybeSetRemoteInputWarning(
        showWarning: Boolean,
  ): NotificationCompat.Builder {
        return if (showWarning) {
            setRemoteInputHistory(arrayOf(context.getString(R.string.invalid_input_warning)))
        } else {
            this
        }
    }

    companion object {
        const val NOTIFICATION_WORK_NAME = "wordy_notification_work"
        const val NOTIFICATION_ID = "wordy_notification_id"
        const val NOTIFICATION_REQUEST_CODE = 100

        const val ADD_WORD_COUNT_ACTION = "add_word_count_action"
        const val KEY_ADD_WORD_COUNT = "key_add_word_count"

        const val UPDATE_WORD_COUNT_ACTION = "update_word_count_action"
        const val KEY_UPDATE_WORD_COUNT = "key_update_word_count"

        private const val NOTIFICATION_CHANNEL_ID = "wordy_notification_channel_id"
    }
}
