package com.example.adminuserapp.data.models

data class Statistics(
    val totalProjects: Int = 0,
    val activeProjects: Int = 0,
    val completedProjects: Int = 0,
    val totalBudget: Double = 0.0,
    val totalTeamMembers: Int = 0,
    val projectCompletionRate: Double = 0.0,
    val averageProjectDuration: Double = 0.0
) 