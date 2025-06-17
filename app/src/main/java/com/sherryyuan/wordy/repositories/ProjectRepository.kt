package com.sherryyuan.wordy.repositories

import com.sherryyuan.wordy.database.ProjectDao
import com.sherryyuan.wordy.database.SelectedProjectDao
import com.sherryyuan.wordy.entitymodels.Goal
import com.sherryyuan.wordy.entitymodels.Project
import com.sherryyuan.wordy.entitymodels.ProjectStatus
import com.sherryyuan.wordy.entitymodels.SelectedProject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class ProjectRepository @Inject constructor(
    private val projectDao: ProjectDao,
    private val selectedProjectDao: SelectedProjectDao,
) {

    fun getProjects(): Flow<List<Project>> {
        return projectDao.getAll()
    }

    fun getProjectsById(id: Long): Flow<Project?> {
        return projectDao.getProjectById(id)
    }

    /**
     * @return ID of the inserted project
     */
    suspend fun insertProject(project: Project): Long {
        return projectDao.insertProject(project)
    }

    suspend fun updateProject(
        id: Long,
        title: String? = null,
        description: String? = null,
        goal: Goal? = null,
        status: ProjectStatus? = null,
    ) {
        projectDao.updateProject(id, title, description, goal, status)
    }

    suspend fun deleteProject(id: Long) {
        projectDao.deleteProject(id)
    }

    fun getSelectedProject(): Flow<Project?> {
        return combine(
            getProjects(),
            selectedProjectDao.getSelectedProject()
        ) { allProjects, selectedProject ->
            allProjects.firstOrNull { it.id == selectedProject?.projectId }
        }
    }

    suspend fun updateSelectedProject(projectId: Long) {
        selectedProjectDao.insertSelectedProject(SelectedProject(projectId))
    }
}
