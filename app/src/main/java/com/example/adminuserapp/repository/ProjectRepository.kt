package com.example.adminuserapp.repository

import com.example.adminuserapp.data.models.Project
import com.example.adminuserapp.data.models.ProjectStatus
import com.example.adminuserapp.data.models.ProjectStatistics
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDate

class ProjectRepository {
    // In a real app, this would be connected to a database or API
    private val projects = mutableListOf<Project>()

    fun getProjects(): Flow<List<Project>> = flow {
        emit(projects)
    }

    fun getProjectById(id: String): Flow<Project?> = flow {
        emit(projects.find { it.id == id })
    }

    fun getProjectsByStatus(status: ProjectStatus): Flow<List<Project>> = flow {
        emit(projects.filter { it.status == status })
    }

    fun getProjectsByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<Project>> = flow {
        emit(projects.filter { it.startDate >= startDate && it.endDate <= endDate })
    }

    fun getProjectsByManager(managerId: String): Flow<List<Project>> = flow {
        emit(projects.filter { it.managerId == managerId })
    }

    fun getProjectsByTeamMember(userId: String): Flow<List<Project>> = flow {
        emit(projects.filter { it.teamMembers.contains(userId) })
    }

    fun getProjectsByCustomer(customerId: String): Flow<List<Project>> = flow {
        emit(projects.filter { it.customerId == customerId })
    }

    suspend fun createProject(project: Project) {
        projects.add(project)
    }

    suspend fun updateProject(project: Project) {
        val index = projects.indexOfFirst { it.id == project.id }
        if (index != -1) {
            projects[index] = project
        }
    }

    suspend fun deleteProject(id: String) {
        projects.removeIf { it.id == id }
    }

    fun getProjectStatistics(): Flow<ProjectStatistics> = flow {
        val totalProjects = projects.size
        val activeProjects = projects.count { it.status == ProjectStatus.IN_PROGRESS }
        val completedProjects = projects.count { it.status == ProjectStatus.COMPLETED }
        val totalBudget = projects.sumOf { it.budget }
        val totalSpent = projects.sumOf { it.spentAmount }
        
        emit(ProjectStatistics(
            totalProjects = totalProjects,
            activeProjects = activeProjects,
            completedProjects = completedProjects,
            totalBudget = totalBudget,
            totalSpent = totalSpent
        ))
    }
} 