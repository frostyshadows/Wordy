package com.sherryyuan.wordy.screens.newproject

import com.sherryyuan.wordy.utils.projectDaysCount
import java.time.LocalDate

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
            val projectStartDate: LocalDate = LocalDate.now(),
            val targetProjectEndDate: LocalDate = LocalDate.now().plusMonths(3L),
        ) : NewProjectGoal {
            override val saveButtonEnabled = targetTotalWordCount.isNotBlank()
            val dailyWordCount: Int
                get() {
                    val days = projectDaysCount(projectStartDate, targetProjectEndDate)
                    return targetTotalWordCount.toInt() / days
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
