package com.example.adminuserapp.repository

import com.example.adminuserapp.data.Absence
import com.example.adminuserapp.data.AbsenceStatus
import com.example.adminuserapp.data.TimeEntry
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class TimesheetRepository {
    private val timeEntries = MutableStateFlow<List<TimeEntry>>(emptyList())
    private val absences = MutableStateFlow<List<Absence>>(emptyList())

    fun clockIn(userId: String): TimeEntry {
        val entry = TimeEntry(
            id = System.currentTimeMillis().toString(),
            userId = userId,
            clockIn = LocalDateTime.now()
        )
        timeEntries.value = timeEntries.value + entry
        return entry
    }

    fun clockOut(userId: String): TimeEntry? {
        val lastEntry = timeEntries.value
            .filter { it.userId == userId && it.clockOut == null }
            .maxByOrNull { it.clockIn }
            ?: return null

        val clockOut = LocalDateTime.now()
        val hoursWorked = ChronoUnit.MINUTES.between(lastEntry.clockIn, clockOut) / 60.0

        val updatedEntry = lastEntry.copy(
            clockOut = clockOut,
            hoursWorked = hoursWorked
        )

        timeEntries.value = timeEntries.value.map {
            if (it.id == lastEntry.id) updatedEntry else it
        }

        return updatedEntry
    }

    fun getTimeEntriesForUser(userId: String, startDate: LocalDate, endDate: LocalDate): Flow<List<TimeEntry>> {
        return timeEntries.map { entries ->
            entries.filter {
                it.userId == userId &&
                it.date.toLocalDate() in startDate..endDate
            }
        }
    }

    fun requestAbsence(userId: String, startDate: LocalDate, endDate: LocalDate, reason: String): Absence {
        val absence = Absence(
            id = System.currentTimeMillis().toString(),
            userId = userId,
            startDate = startDate,
            endDate = endDate,
            reason = reason
        )
        absences.value = absences.value + absence
        return absence
    }

    fun getAbsencesForUser(userId: String): Flow<List<Absence>> {
        return absences.map { it.filter { absence -> absence.userId == userId } }
    }

    fun updateAbsenceStatus(absenceId: String, status: AbsenceStatus) {
        absences.value = absences.value.map {
            if (it.id == absenceId) it.copy(status = status) else it
        }
    }
} 