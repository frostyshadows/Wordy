package com.sherryyuan.wordy.viewmodels

data class CreateDefaultProjectViewState(
    val wordCount: String = "500",
    val state: State = State.EDITING,
) {
    enum class State {
        EDITING,
        SUBMITTING,
        SUBMITTED,
    }
}
