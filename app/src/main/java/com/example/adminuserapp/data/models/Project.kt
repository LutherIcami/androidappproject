package com.example.adminuserapp.data.models

import java.time.LocalDate
import java.util.UUID

data class Project(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val status: ProjectStatus,
    val budget: Double,
    val spentAmount: Double,
    val managerId: String,
    val teamMembers: List<String>,
    val customerId: String,
    val priority: ProjectPriority,
    val riskLevel: RiskLevel,
    val progress: Int = 0,
    val createdAt: LocalDate = LocalDate.now(),
    val updatedAt: LocalDate = LocalDate.now()
)

enum class ProjectStatus {
    PLANNING,
    IN_PROGRESS,
    ON_HOLD,
    COMPLETED,
    CANCELLED
}

enum class ProjectPriority {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

enum class RiskLevel {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
} 