package com.example.adminuserapp.data

import java.time.LocalDate

data class Absence(
    val id: String,
    val userId: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val reason: String,
    val status: AbsenceStatus = AbsenceStatus.PENDING
)

enum class AbsenceStatus {
    PENDING,
    APPROVED,
    REJECTED
} 