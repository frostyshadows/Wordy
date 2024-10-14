package com.sherryyuan.wordy

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.RemoteInput
import com.sherryyuan.wordy.NotificationWorker.Companion.ADD_WORD_COUNT_ACTION
import com.sherryyuan.wordy.NotificationWorker.Companion.KEY_ADD_WORD_COUNT

class NotificationInputReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ADD_WORD_COUNT_ACTION) {
            val wordCount = RemoteInput.getResultsFromIntent(intent)?.getCharSequence(KEY_ADD_WORD_COUNT)
            println("Added word count: $wordCount")
        }
    }
}
