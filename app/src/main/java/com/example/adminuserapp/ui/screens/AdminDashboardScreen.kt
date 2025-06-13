package com.example.adminuserapp.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.adminuserapp.data.models.*
import com.example.adminuserapp.navigation.Screen
import com.example.adminuserapp.repository.AuthRepository
import com.example.adminuserapp.repository.FinancialRepository
import com.example.adminuserapp.repository.ProjectRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.collectAsState
import com.example.adminuserapp.data.models.Project
import com.example.adminuserapp.data.models.ProjectStatistics
import com.example.adminuserapp.data.models.FinancialStatistics
import com.example.adminuserapp.data.models.Invoice
import com.example.adminuserapp.data.models.Expense
import com.example.adminuserapp.data.models.ProjectStatus
import com.example.adminuserapp.data.models.InvoiceStatus
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun AdminDashboardScreen(navController: NavController) {
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    val authRepository = remember { AuthRepository() }
    val projectRepository = remember { ProjectRepository() }
    val financialRepository = remember { FinancialRepository() }
    val currentUser = remember { authRepository.getCurrentUser() }

    var weekOffset by remember { mutableStateOf(0) }
    val currentWeekStart = remember(weekOffset) {
        LocalDate.now()
            .with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1L)
            .plusWeeks(weekOffset.toLong())
    }
    var selectedDay by remember { mutableStateOf(LocalDate.now()) }
    var selectedTab by remember { mutableStateOf(0) }
    var showSearchBar by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    // Collect data from repositories
    val projectStats: State<ProjectStatistics> = projectRepository.getProjectStatistics().collectAsState(
        initial = ProjectStatistics(
            totalProjects = 0,
            activeProjects = 0,
            completedProjects = 0,
            totalBudget = 0.0,
            totalSpent = 0.0
        )
    )
    val financialStats: State<FinancialStatistics> = financialRepository.getFinancialStatistics().collectAsState(
        initial = FinancialStatistics(
            totalRevenue = 0.0,
            totalExpenses = 0.0,
            totalPayments = 0.0,
            pendingInvoices = 0,
            overdueInvoices = 0,
            profit = 0.0
        )
    )
    val projects: State<List<Project>> = projectRepository.getProjects().collectAsState(initial = emptyList())
    val invoices: State<List<Invoice>> = financialRepository.getInvoices().collectAsState(initial = emptyList())
    val expenses: State<List<Expense>> = financialRepository.getExpenses().collectAsState(initial = emptyList())

    LaunchedEffect(Unit) {
        delay(1000)
        isLoading = false
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text("Admin Dashboard")
                    },
                    actions = {
                        IconButton(onClick = { showSearchBar = !showSearchBar }) {
                            Icon(
                                if (showSearchBar) Icons.Default.Close else Icons.Default.Search,
                                contentDescription = if (showSearchBar) "Close Search" else "Search"
                            )
                        }
                        IconButton(onClick = { navController.navigate(Screen.Profile.route) }) {
                            Icon(Icons.Default.Person, contentDescription = "Profile")
                        }
                        IconButton(onClick = {
                            scope.launch {
                                authRepository.signOut()
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(Screen.AdminDashboard.route) { inclusive = true }
                                }
                            }
                        }) {
                            Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                        }
                    }
                )
                AnimatedVisibility(
                    visible = showSearchBar,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    SearchBar(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        onSearch = { /* Handle search */ },
                        active = true,
                        onActiveChange = { /* Handle active change */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        placeholder = { Text("Search...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
                    ) {
                        // Search suggestions can be added here
                    }
                }
            }
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
                    EnhancedSummaryCard(
                        icon = Icons.Default.Group,
                        title = "Total Users",
                        value = "0",
                        unit = "users",
                        trend = "+5%",
                        isPositive = true
                    )
                    EnhancedSummaryCard(
                        icon = Icons.Default.EventAvailable,
                        title = "Total Hours",
                        value = "0:00",
                        unit = "hours",
                        trend = "+2%",
                        isPositive = true
                    )
                    EnhancedSummaryCard(
                        icon = Icons.Default.PersonOff,
                        title = "Absences",
                        value = "0",
                        unit = "days",
                        trend = "-10%",
                        isPositive = true
                    )
                    EnhancedSummaryCard(
                        icon = Icons.Default.Money,
                        title = "Total Expenses",
                        value = "KES ${financialStats.value.totalExpenses}",
                        unit = "",
                        trend = "+15%",
                        isPositive = false
                    )
                }

                // Tab Row with enhanced styling
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            height = 3.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Overview") },
                        icon = { Icon(Icons.Default.Dashboard, contentDescription = null) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Projects") },
                        icon = { Icon(Icons.Default.Assignment, contentDescription = null) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Financials") },
                        icon = { Icon(Icons.Default.AccountBalance, contentDescription = null) }
                    )
                    Tab(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        text = { Text("Reports") },
                        icon = { Icon(Icons.Default.Assessment, contentDescription = null) }
                    )
                }

                // Content based on selected tab with animations
                AnimatedContent(
                    targetState = selectedTab,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(300)) + slideInHorizontally(
                            animationSpec = tween(300)
                        ) with fadeOut(animationSpec = tween(300)) + slideOutHorizontally(
                            animationSpec = tween(300)
                        )
                    }
                ) { tab ->
                    when (tab) {
                        0 -> OverviewTab(selectedDay, currentWeekStart, weekOffset, navController)
                        1 -> ProjectsTab(projects.value, projectStats.value, navController)
                        2 -> FinancialsTab(financialStats.value, invoices.value, expenses.value, navController)
                        3 -> ReportsTab(navController)
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.EnhancedSummaryCard(
    icon: ImageVector,
    title: String,
    value: String,
    unit: String,
    trend: String,
    isPositive: Boolean
) {
    Card(
        modifier = Modifier
            .weight(1f)
            .padding(4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))
            Text(
                title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            if (unit.isNotEmpty()) {
                Text(
                    unit,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    if (isPositive) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = if (isPositive) Color.Green else Color.Red
                )
                Text(
                    trend,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isPositive) Color.Green else Color.Red
                )
            }
        }
    }
}

@Composable
private fun OverviewTab(
    selectedDay: LocalDate,
    currentWeekStart: LocalDate,
    weekOffset: Int,
    navController: NavController
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Weekly Date Selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = { /* Handle previous week */ }) {
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
                        onClick = { /* Handle date selection */ },
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

            TextButton(onClick = { /* Handle next week */ }) {
                Text("Next Week")
                Icon(Icons.Default.ArrowForward, contentDescription = "Next Week")
            }
        }

        // Quick Actions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            QuickActionButton(
                icon = Icons.Default.People,
                text = "Manage Users",
                onClick = { navController.navigate(Screen.UserManagement.route) }
            )
            QuickActionButton(
                icon = Icons.Default.Approval,
                text = "Approve Timesheets",
                onClick = { navController.navigate(Screen.TimesheetApproval.route) }
            )
            QuickActionButton(
                icon = Icons.Default.Assessment,
                text = "View Reports",
                onClick = { navController.navigate(Screen.Reports.route) }
            )
        }

        // Recent Activity
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Recent Activity",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                // Placeholder for activity list
                Text("No recent activity to display")
            }
        }
    }
}

@Composable
private fun ProjectsTab(
    projects: List<Project>,
    projectStats: ProjectStatistics,
    navController: NavController
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Project Statistics
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            EnhancedSummaryCard(
                icon = Icons.Default.Assignment,
                title = "Active Projects",
                value = projectStats.activeProjects.toString(),
                unit = "projects",
                trend = "+2",
                isPositive = true
            )
            EnhancedSummaryCard(
                icon = Icons.Default.Timeline,
                title = "In Progress",
                value = projectStats.completedProjects.toString(),
                unit = "projects",
                trend = "+1",
                isPositive = true
            )
            EnhancedSummaryCard(
                icon = Icons.Default.CheckCircle,
                title = "Total Budget",
                value = "KES ${projectStats.totalBudget}",
                unit = "",
                trend = "+5%",
                isPositive = true
            )
        }

        // Project List
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Active Projects",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                if (projects.isEmpty()) {
                    Text("No active projects to display")
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(projects) { project ->
                            ProjectCard(project)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProjectCard(project: Project) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = project.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = project.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                ProjectStatusChip(project.status)
            }
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = project.progress / 100f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Budget",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "KES ${project.budget}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
                Column {
                    Text(
                        text = "Spent",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "KES ${project.spentAmount}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
                Column {
                    Text(
                        text = "Progress",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${project.progress}%",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun ProjectStatusChip(status: ProjectStatus) {
    val (color, text) = when (status) {
        ProjectStatus.PLANNING -> MaterialTheme.colorScheme.tertiary to "Planning"
        ProjectStatus.IN_PROGRESS -> MaterialTheme.colorScheme.primary to "In Progress"
        ProjectStatus.ON_HOLD -> MaterialTheme.colorScheme.error to "On Hold"
        ProjectStatus.COMPLETED -> MaterialTheme.colorScheme.secondary to "Completed"
        ProjectStatus.CANCELLED -> MaterialTheme.colorScheme.error to "Cancelled"
    }

    Surface(
        color = color.copy(alpha = 0.1f),
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

@Composable
private fun FinancialsTab(
    financialStats: FinancialStatistics,
    invoices: List<Invoice>,
    expenses: List<Expense>,
    navController: NavController
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Financial Overview
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            EnhancedSummaryCard(
                icon = Icons.Default.AccountBalance,
                title = "Revenue",
                value = "KES ${financialStats.totalRevenue}",
                unit = "",
                trend = "+10%",
                isPositive = true
            )
            EnhancedSummaryCard(
                icon = Icons.Default.Money,
                title = "Expenses",
                value = "KES ${financialStats.totalExpenses}",
                unit = "",
                trend = "+5%",
                isPositive = false
            )
            EnhancedSummaryCard(
                icon = Icons.Default.ShowChart,
                title = "Profit",
                value = "KES ${financialStats.profit}",
                unit = "",
                trend = "+15%",
                isPositive = true
            )
        }

        // Financial Actions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            QuickActionButton(
                icon = Icons.Default.Receipt,
                text = "Invoices",
                onClick = { /* Handle invoices */ }
            )
            QuickActionButton(
                icon = Icons.Default.Payment,
                text = "Payments",
                onClick = { /* Handle payments */ }
            )
            QuickActionButton(
                icon = Icons.Default.Assessment,
                text = "Financial Reports",
                onClick = { /* Handle financial reports */ }
            )
        }

        // Recent Invoices
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Recent Invoices",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                if (invoices.isEmpty()) {
                    Text("No recent invoices")
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(invoices.take(5)) { invoice ->
                            InvoiceCard(invoice)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InvoiceCard(invoice: Invoice) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Invoice #${invoice.number}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Amount: KES ${invoice.amount}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Due: ${invoice.dueDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            InvoiceStatusChip(invoice.status)
        }
    }
}

@Composable
private fun InvoiceStatusChip(status: InvoiceStatus) {
    val (color, text) = when (status) {
        InvoiceStatus.DRAFT -> MaterialTheme.colorScheme.tertiary to "Draft"
        InvoiceStatus.SENT -> MaterialTheme.colorScheme.primary to "Sent"
        InvoiceStatus.PAID -> MaterialTheme.colorScheme.secondary to "Paid"
        InvoiceStatus.OVERDUE -> MaterialTheme.colorScheme.error to "Overdue"
        InvoiceStatus.CANCELLED -> MaterialTheme.colorScheme.error to "Cancelled"
    }

    Surface(
        color = color.copy(alpha = 0.1f),
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

@Composable
private fun ReportsTab(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Report Categories
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(reportCategories) { category ->
                ReportCategoryCard(category)
            }
        }
    }
}

@Composable
private fun RowScope.QuickActionButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .weight(1f)
            .padding(horizontal = 4.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
        Text(text)
    }
}

@Composable
private fun ReportCategoryCard(category: ReportCategory) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = category.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = category.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = { /* Handle report generation */ }) {
                Icon(Icons.Default.Download, contentDescription = "Download Report")
            }
        }
    }
}

data class ReportCategory(
    val title: String,
    val description: String
)

private val reportCategories = listOf(
    ReportCategory(
        "Timesheet Report",
        "Detailed view of employee work hours and attendance"
    ),
    ReportCategory(
        "Project Performance",
        "Analysis of project timelines, budgets, and resource utilization"
    ),
    ReportCategory(
        "Financial Summary",
        "Overview of revenue, expenses, and profitability"
    ),
    ReportCategory(
        "Employee Productivity",
        "Individual and team performance metrics"
    ),
    ReportCategory(
        "Customer Analytics",
        "Customer engagement and satisfaction metrics"
    )
) 