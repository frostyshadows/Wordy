package com.sherryyuan.wordy.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.RemoteInput
import androidx.lifecycle.viewModelScope
import com.sherryyuan.wordy.entitymodels.Entry
import com.sherryyuan.wordy.notification.NotificationWorker.Companion.ADD_WORD_COUNT_ACTION
import com.sherryyuan.wordy.notification.NotificationWorker.Companion.KEY_ADD_WORD_COUNT
import com.sherryyuan.wordy.repositories.EntryRepository
import com.sherryyuan.wordy.repositories.ProjectRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.components.ActivityComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NotificationBroadcastReceiver : BroadcastReceiver() {

    @Inject lateinit var projectRepository: ProjectRepository
    @Inject lateinit var entryRepository: EntryRepository

    private val scope = CoroutineScope(Dispatchers.Default)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ADD_WORD_COUNT_ACTION) {
            scope.launch {
            val wordCountInput = RemoteInput.getResultsFromIntent(intent)?.getCharSequence(KEY_ADD_WORD_COUNT)
            println("Adding word count: $wordCountInput")
            val selectedProject = projectRepository.getSelectedProject()
                .first()
                val wordCount = wordCountInput?.toString()?.toInt() // TODO: handle non-numeric input
            if (selectedProject != null && wordCount != null) {
                entryRepository.insertEntry(
                    Entry(
                        timestamp = System.currentTimeMillis(),
                        wordCount = wordCount,
                        projectId = selectedProject.id,
                    )
                )
            }
                }
        }
    }
}
