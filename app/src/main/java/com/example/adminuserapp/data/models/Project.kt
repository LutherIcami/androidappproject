package com.example.adminuserapp.data.models

data class Project(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val status: String = "",
    val budget: Double = 0.0,
    val managerId: String = "",
    val teamMembers: List<String> = listOf()
) 