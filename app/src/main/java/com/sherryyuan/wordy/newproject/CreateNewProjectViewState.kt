package com.sherryyuan.wordy.newproject

import java.util.Calendar
import java.util.Date

data class CreateNewProjectViewState(
    val title: String = "",
    val description: String? = null,
    val goal: NewProjectGoal = NewProjectGoal.WordCount(),
    val state: State = State.EDITING_INFO,
) {

    sealed interface NewProjectGoal {

        val saveButtonEnabled: Boolean

        data class WordCount(
            val wordCount: String = "500",
        ) : NewProjectGoal {
            override val saveButtonEnabled = wordCount.isNotBlank()
        }

        data class Deadline(
            val targetTotalWordCount: String = "50,000",
            val projectStartTime: Date = Calendar.getInstance().time,
            val targetProjectEndTime: Date = Calendar.getInstance().apply {
                add(Calendar.MONTH, 3)
            }.time,
        ) : NewProjectGoal {
            override val saveButtonEnabled = targetTotalWordCount.isNotBlank()
        }
    }

    enum class State {
        EDITING_INFO,
        EDITING_WORD_COUNT_GOAL,
        EDITING_DEADLINE_GOAL,
        SUBMITTING_WORD_COUNT_GOAL,
        SUBMITTING_DEADLINE_GOAL,
        SUBMITTED,
    }
}
