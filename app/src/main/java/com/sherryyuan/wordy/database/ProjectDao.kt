package com.sherryyuan.wordy.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sherryyuan.wordy.models.Project
import com.sherryyuan.wordy.models.ProjectStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {

    @Query(value = "SELECT * FROM Project")
    fun getAll(): Flow<List<Project>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: Project)

    @Query(value = "UPDATE Project " +
            "SET title= :title, " +
            "description= :description, " +
            "targetTotalWordCount = :targetTotalWordCount, " +
            "projectStartTime = :projectStartTime, " +
            "targetProjectEndTime = :targetProjectEndTime, " +
            "dailyWordCountGoal = :dailyWordCountGoal, " +
            "status = :status " +
            "WHERE id = :projectId")
    suspend fun updateProject(
        projectId: Int,
        title: String,
        description: String?,
        targetTotalWordCount: Int,
        projectStartTime: Long,
        targetProjectEndTime: Long?,
        dailyWordCountGoal: Int,
        status: ProjectStatus,
    )

    @Delete
    suspend fun deleteProject(project: Project)
}
