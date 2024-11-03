package com.sherryyuan.wordy.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sherryyuan.wordy.entitymodels.SelectedProject
import kotlinx.coroutines.flow.Flow

@Dao
interface SelectedProjectDao {

    @Query("SELECT * FROM SelectedProject LIMIT 1")
    fun getSelectedProject(): Flow<SelectedProject?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSelectedProject(project: SelectedProject)

    @Query("DELETE FROM SelectedProject")
    suspend fun deleteSelectedProject()
}
