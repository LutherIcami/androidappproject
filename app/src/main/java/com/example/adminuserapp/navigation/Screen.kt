package com.example.adminuserapp.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object UserDashboard : Screen("user_dashboard")
    object AdminDashboard : Screen("admin_dashboard")
    object Profile : Screen("profile")
    object UserManagement : Screen("user_management")
    object Timesheet : Screen("timesheet")
    object AbsenceRequest : Screen("absence_request")
    object AbsenceList : Screen("absence_list")
    object TimesheetApproval : Screen("timesheet_approval")
    object Reports : Screen("reports")
} 