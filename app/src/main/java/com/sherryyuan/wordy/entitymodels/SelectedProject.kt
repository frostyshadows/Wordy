package com.sherryyuan.wordy.entitymodels

import androidx.room.Entity
import androidx.room.PrimaryKey

// Single row tracking the currently selected project
@Entity
data class SelectedProject(
    val projectId: Long
) {
    @PrimaryKey
    var id: Long = 0
        set(_) {
            field = 0
        }
}
