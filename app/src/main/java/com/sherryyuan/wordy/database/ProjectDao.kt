package com.sherryyuan.wordy.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sherryyuan.wordy.entitymodels.DEFAULT_JUST_WRITE_PROJECT_TITLE
import com.sherryyuan.wordy.entitymodels.Project
import com.sherryyuan.wordy.entitymodels.ProjectStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {

    @Query("SELECT * FROM Project")
    fun getAll(): Flow<List<Project>>

    @Query("SELECT EXISTS(SELECT * FROM Project WHERE title = :defaultTitle)")
    suspend fun hasDefaultProject(defaultTitle: String = DEFAULT_JUST_WRITE_PROJECT_TITLE): Boolean

    /**
     * @return ID of the inserted project
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: Project): Long

    @Query("UPDATE Project " +
            "SET title= :title, " +
            "description= :description, " +
            "targetTotalWordCount = :targetTotalWordCount, " +
            "projectStartTime = :projectStartTime, " +
            "targetProjectEndTime = :targetProjectEndTime, " +
            "dailyWordCountGoal = :dailyWordCountGoal, " +
            "status = :status " +
            "WHERE id = :projectId")
    suspend fun updateProject(
        projectId: Long,
        title: String,
        description: String?,
        targetTotalWordCount: Int,
        projectStartTime: Long,
        targetProjectEndTime: Long?,
        dailyWordCountGoal: Int,
        status: ProjectStatus,
    )

    @Query("DELETE FROM Project WHERE title = :defaultTitle")
    suspend fun deleteDefaultProject(defaultTitle: String = DEFAULT_JUST_WRITE_PROJECT_TITLE)
}
