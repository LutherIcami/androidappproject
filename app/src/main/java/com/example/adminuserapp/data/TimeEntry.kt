package com.example.adminuserapp.data

import java.time.LocalDateTime

data class TimeEntry(
    val id: String,
    val userId: String,
    val clockIn: LocalDateTime,
    val clockOut: LocalDateTime? = null,
    val hoursWorked: Double? = null,
    val date: LocalDateTime = clockIn
) 