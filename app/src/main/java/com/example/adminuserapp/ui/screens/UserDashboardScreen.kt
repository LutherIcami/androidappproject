package com.example.adminuserapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.adminuserapp.navigation.Screen
import com.example.adminuserapp.repository.AuthRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDashboardScreen(navController: NavController) {
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    val authRepository = remember { AuthRepository() }
    val currentUser = remember { authRepository.getCurrentUser() }

    var weekOffset by remember { mutableStateOf(0) }
    val currentWeekStart = remember(weekOffset) {
        LocalDate.now()
            .with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1L)
            .plusWeeks(weekOffset.toLong())
    }
    var selectedDay by remember { mutableStateOf(LocalDate.now()) }

    LaunchedEffect(Unit) {
        // Simulate loading
        delay(1000)
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Timesheet Dashboard")
                },
                actions = {
                    IconButton(onClick = { /* Handle search */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = { /* Handle full screen */ }) {
                        Icon(Icons.Default.Fullscreen, contentDescription = "Full screen")
                    }
                    IconButton(onClick = { navController.navigate(Screen.Profile.route) }) {
                        Icon(Icons.Default.Person, contentDescription = "Profile")
                    }
                    IconButton(onClick = {
                        scope.launch {
                            authRepository.signOut()
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.UserDashboard.route) { inclusive = true }
                            }
                        }
                    }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Summary Cards
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SummaryCard(Icons.Default.EventAvailable, "Expected Hours", "40:00", "hours")
                    SummaryCard(Icons.Default.Schedule, "Work Hours (Week)", "0:00", "hours")
                    SummaryCard(Icons.Default.PersonOff, "Absence", "0:00", "hours")
                    SummaryCard(Icons.Default.Money, "Expenses", "KES 0", "")
                    SummaryCard(Icons.Default.ShowChart, "Productivity", "0.0%", "")
                }

                // Weekly Date Selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { weekOffset-- }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Previous Week")
                        Text("Previous Week")
                    }

                    val daysOfWeek = (0..6).map { i -> currentWeekStart.plusDays(i.toLong()) }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        daysOfWeek.forEach { date ->
                            val isSelected = date == selectedDay
                            Button(
                                onClick = { selectedDay = date },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                modifier = Modifier.size(60.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(date.format(DateTimeFormatter.ofPattern("EEE")).substring(0, 1))
                                    Text(date.format(DateTimeFormatter.ofPattern("MM/dd")))
                                }
                            }
                        }
                    }

                    TextButton(onClick = { weekOffset++ }) {
                        Text("Next Week")
                        Icon(Icons.Default.ArrowForward, contentDescription = "Next Week")
                    }
                }

                // Action Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Button(
                        onClick = { navController.navigate(Screen.Timesheet.route) },
                        modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                    ) {
                        Icon(Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                        Text("Add Work Hours")
                    }
                    Button(
                        onClick = { navController.navigate(Screen.AbsenceRequest.route) },
                        modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                    ) {
                        Icon(Icons.Default.EventBusy, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                        Text("Add Absence")
                    }
                    Button(
                        onClick = { /* Handle Add Expense */ },
                        modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                    ) {
                        Icon(Icons.Default.Money, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                        Text("Add Expense")
                    }
                }

                // Timesheet Details
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Timesheet Details",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = "Logs for ${selectedDay.format(DateTimeFormatter.ofPattern("yyyy-MM-dd (EEEE)"))}",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp) // Placeholder height
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Timesheet logs for ${selectedDay.format(DateTimeFormatter.ofPattern("MM/dd"))} will appear here.")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RowScope.SummaryCard(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, value: String, unit: String) {
    Card(
        modifier = Modifier.weight(1f).padding(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp))
            Spacer(Modifier.height(8.dp))
            Text(title, style = MaterialTheme.typography.labelSmall)
            Text(value, style = MaterialTheme.typography.headlineSmall)
            if (unit.isNotEmpty()) {
                Text(unit, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
} 