package com.sherryyuan.wordy.newproject

import com.sherryyuan.wordy.utils.getDaysBetween
import java.util.Calendar

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
            val targetTotalWordCount: String = "50000",
            val projectStartDateMillis: Long = Calendar.getInstance().timeInMillis,
            val targetProjectEndDateMillis: Long = Calendar.getInstance().apply {
                add(Calendar.MONTH, 3)
            }.timeInMillis,
        ) : NewProjectGoal {
            override val saveButtonEnabled = targetTotalWordCount.isNotBlank()
            val dailyWordCount: Int
                get() {
                    // TODO handle case where start date is in wrong time zone (eg. select tomorrow but get 0 as days between when it should be 1)
                    // start and end dates are inclusive
                    val days = getDaysBetween(projectStartDateMillis, targetProjectEndDateMillis) + 1
                    return (targetTotalWordCount.toInt() / days).toInt()
                }
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
