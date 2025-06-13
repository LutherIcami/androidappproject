package com.example.adminuserapp.data.models

data class ProjectStatistics(
    val totalProjects: Int,
    val activeProjects: Int,
    val completedProjects: Int,
    val totalBudget: Double,
    val totalSpent: Double
)

data class FinancialStatistics(
    val totalRevenue: Double,
    val totalExpenses: Double,
    val totalPayments: Double,
    val pendingInvoices: Int,
    val overdueInvoices: Int,
    val profit: Double
) 