package com.example.adminuserapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.adminuserapp.data.Absence
import com.example.adminuserapp.data.AbsenceStatus
import com.example.adminuserapp.repository.TimesheetRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AbsenceListScreen(navController: NavController) {
    var absences by remember { mutableStateOf<List<Absence>>(emptyList()) }
    val scope = rememberCoroutineScope()
    val timesheetRepository = remember { TimesheetRepository() }
    val currentUser = remember { "current_user_id" } // Replace with actual user ID

    LaunchedEffect(Unit) {
        timesheetRepository.getAbsencesForUser(currentUser).collectLatest { absenceList ->
            absences = absenceList
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Absence Requests") },
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
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(absences) { absence ->
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
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "From: ${absence.startDate.format(DateTimeFormatter.ISO_DATE)}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "To: ${absence.endDate.format(DateTimeFormatter.ISO_DATE)}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            
                            Text(
                                text = "Reason: ${absence.reason}",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Text(
                                    text = "Status: ${absence.status}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = when (absence.status) {
                                        AbsenceStatus.APPROVED -> MaterialTheme.colorScheme.primary
                                        AbsenceStatus.REJECTED -> MaterialTheme.colorScheme.error
                                        AbsenceStatus.PENDING -> MaterialTheme.colorScheme.secondary
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
} 