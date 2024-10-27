package com.sherryyuan.wordy.repositories

import com.sherryyuan.wordy.database.ProjectDao
import com.sherryyuan.wordy.entitymodels.Project
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class ProjectRepository @Inject constructor(private val projectDao: ProjectDao) {

    suspend fun insertProject(project: Project) {
        projectDao.insertProject(project)
    }

    fun getProjects(): Flow<List<Project>> {
        return projectDao.getAll()
    }
}
