package com.sherryyuan.wordy.repositories

import com.sherryyuan.wordy.database.ProjectDao
import com.sherryyuan.wordy.database.SelectedProjectDao
import com.sherryyuan.wordy.entitymodels.DEFAULT_JUST_WRITE_PROJECT_TITLE
import com.sherryyuan.wordy.entitymodels.Project
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

    /**
     * @return ID of the inserted project
     */
    suspend fun insertProject(project: Project): Long {
        if (project.title == DEFAULT_JUST_WRITE_PROJECT_TITLE) {
            projectDao.deleteDefaultProject()
        }
        return projectDao.insertProject(project)
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
