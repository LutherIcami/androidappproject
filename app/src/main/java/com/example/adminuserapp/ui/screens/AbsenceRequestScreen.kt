package com.example.adminuserapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.adminuserapp.repository.TimesheetRepository
import java.time.LocalDate
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AbsenceRequestScreen(navController: NavController) {
    var startDate by remember { mutableStateOf(LocalDate.now()) }
    var endDate by remember { mutableStateOf(LocalDate.now()) }
    var reason by remember { mutableStateOf("") }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val timesheetRepository = remember { TimesheetRepository() }
    val currentUser = remember { "current_user_id" } // Replace with actual user ID

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Request Absence") },
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
            // Start Date
            OutlinedTextField(
                value = startDate.toString(),
                onValueChange = { },
                label = { Text("Start Date") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showStartDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Select start date")
                    }
                }
            )

            // End Date
            OutlinedTextField(
                value = endDate.toString(),
                onValueChange = { },
                label = { Text("End Date") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showEndDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Select end date")
                    }
                }
            )

            // Reason
            OutlinedTextField(
                value = reason,
                onValueChange = { reason = it },
                label = { Text("Reason") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                minLines = 3
            )

            // Submit Button
            Button(
                onClick = {
                    scope.launch {
                        timesheetRepository.requestAbsence(
                            currentUser,
                            startDate,
                            endDate,
                            reason
                        )
                        navController.navigateUp()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = reason.isNotBlank() && endDate >= startDate
            ) {
                Icon(
                    Icons.Default.Send,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Submit Request")
            }
        }
    }

    // Date Pickers
    if (showStartDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = { showStartDatePicker = false }
                ) {
                    Text("OK")
                }
            }
        ) {
            DatePicker(
                state = rememberDatePickerState(
                    initialSelectedDateMillis = startDate.toEpochDay() * 24 * 60 * 60 * 1000
                ),
                title = { Text("Select Start Date") },
                headline = { Text("Start Date") },
                showModeToggle = false
            )
        }
    }

    if (showEndDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = { showEndDatePicker = false }
                ) {
                    Text("OK")
                }
            }
        ) {
            DatePicker(
                state = rememberDatePickerState(
                    initialSelectedDateMillis = endDate.toEpochDay() * 24 * 60 * 60 * 1000
                ),
                title = { Text("Select End Date") },
                headline = { Text("End Date") },
                showModeToggle = false
            )
        }
    }
} 