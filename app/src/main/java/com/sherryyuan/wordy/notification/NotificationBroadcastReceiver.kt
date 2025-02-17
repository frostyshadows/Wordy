package com.sherryyuan.wordy.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.RemoteInput
import com.sherryyuan.wordy.notification.NotificationWorker.Companion.ADD_WORD_COUNT_ACTION
import com.sherryyuan.wordy.notification.NotificationWorker.Companion.KEY_ADD_WORD_COUNT
import com.sherryyuan.wordy.notification.NotificationWorker.Companion.KEY_UPDATE_WORD_COUNT
import com.sherryyuan.wordy.notification.NotificationWorker.Companion.UPDATE_WORD_COUNT_ACTION
import com.sherryyuan.wordy.repositories.EntryRepository
import com.sherryyuan.wordy.repositories.ProjectRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NotificationBroadcastReceiver : BroadcastReceiver() {

    @Inject
    lateinit var projectRepository: ProjectRepository

    @Inject
    lateinit var entryRepository: EntryRepository

    private val scope = CoroutineScope(Dispatchers.Default)

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ADD_WORD_COUNT_ACTION -> {
                scope.launch {
                    val wordCountInput = RemoteInput.getResultsFromIntent(intent)
                        ?.getCharSequence(KEY_ADD_WORD_COUNT)
                    println("Adding word count: $wordCountInput")
                    val selectedProject = projectRepository.getSelectedProject().first()
                    val wordCount =
                        wordCountInput?.toString()?.toInt() // TODO: handle non-numeric input
                    if (selectedProject != null && wordCount != null) {
                        entryRepository.insertEntry(
                            timestamp = System.currentTimeMillis(),
                            wordCount = wordCount,
                            projectId = selectedProject.id,
                            updateWordCountStrategy = EntryRepository.UpdateWordCountStrategy.ADD,
                        )
                    }
                }
            }

            UPDATE_WORD_COUNT_ACTION -> {
                scope.launch {
                    val wordCountInput = RemoteInput.getResultsFromIntent(intent)
                        ?.getCharSequence(KEY_UPDATE_WORD_COUNT)
                    println("Updating word count: $wordCountInput")
                    val selectedProject = projectRepository.getSelectedProject().first()
                    val wordCount =
                        wordCountInput?.toString()?.toInt() // TODO: handle non-numeric input
                    if (selectedProject != null && wordCount != null) {
                        entryRepository.insertEntry(
                            timestamp = System.currentTimeMillis(),
                            wordCount = wordCount,
                            projectId = selectedProject.id,
                            updateWordCountStrategy = EntryRepository.UpdateWordCountStrategy.REPLACE,
                        )
                    }
                }
            }
        }
    }
}
