package com.sherryyuan.wordy.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sherryyuan.wordy.entitymodels.DEFAULT_JUST_WRITE_PROJECT_ID
import com.sherryyuan.wordy.entitymodels.Goal
import com.sherryyuan.wordy.entitymodels.Project
import com.sherryyuan.wordy.entitymodels.ProjectStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {

    @Query("SELECT * FROM Project")
    fun getAll(): Flow<List<Project>>

    @Query("SELECT * FROM Project WHERE id = :id")
    fun getProjectById(id: Long): Flow<Project>

    @Query("SELECT EXISTS(SELECT * FROM Project WHERE id = :defaultId)")
    suspend fun hasDefaultProject(defaultId: Long = DEFAULT_JUST_WRITE_PROJECT_ID): Boolean

    /**
     * @return ID of the inserted project
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: Project): Long

    @Query(
        """
        UPDATE Project 
        SET 
            title = COALESCE(:title, title),
            description = COALESCE(:description, description),
            goal = COALESCE(:goal, goal),
            status = COALESCE(:status, status)
        WHERE id = :projectId
    """
    )
    suspend fun updateProject(
        projectId: Long,
        title: String? = null,
        description: String? = null,
        goal: Goal? = null,
        status: ProjectStatus? = null,
    )

    @Query("DELETE FROM Project WHERE id = :defaultId")
    suspend fun deleteDefaultProject(defaultId: Long = DEFAULT_JUST_WRITE_PROJECT_ID)
}
