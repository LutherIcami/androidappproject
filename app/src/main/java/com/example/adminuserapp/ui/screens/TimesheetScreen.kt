package com.example.adminuserapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.adminuserapp.data.TimeEntry
import com.example.adminuserapp.repository.TimesheetRepository
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimesheetScreen(navController: NavController) {
    var timeEntries by remember { mutableStateOf<List<TimeEntry>>(emptyList()) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var isClockedIn by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val timesheetRepository = remember { TimesheetRepository() }
    val currentUser = remember { "current_user_id" } // Replace with actual user ID

    LaunchedEffect(selectedDate) {
        timesheetRepository.getTimeEntriesForUser(
            currentUser,
            selectedDate,
            selectedDate
        ).collectLatest { entries ->
            timeEntries = entries
            isClockedIn = entries.any { it.clockOut == null }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Timesheet") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Date selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    selectedDate = selectedDate.minusDays(1)
                }) {
                    Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Previous day")
                }
                
                Text(
                    text = selectedDate.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")),
                    style = MaterialTheme.typography.titleMedium
                )
                
                IconButton(onClick = {
                    selectedDate = selectedDate.plusDays(1)
                }) {
                    Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Next day")
                }
            }

            // Clock in/out button
            Button(
                onClick = {
                    scope.launch {
                        if (isClockedIn) {
                            timesheetRepository.clockOut(currentUser)
                        } else {
                            timesheetRepository.clockIn(currentUser)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Icon(
                    if (isClockedIn) Icons.Default.ExitToApp else Icons.Default.PersonAdd,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(if (isClockedIn) "Clock Out" else "Clock In")
            }

            // Time entries list
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(timeEntries) { entry ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Clock In: ${entry.clockIn.format(DateTimeFormatter.ofPattern("HH:mm"))}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                entry.clockOut?.let {
                                    Text(
                                        text = "Clock Out: ${it.format(DateTimeFormatter.ofPattern("HH:mm"))}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                            entry.hoursWorked?.let {
                                Text(
                                    text = "Hours Worked: %.2f".format(it),
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
} 