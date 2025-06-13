package com.example.adminuserapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.adminuserapp.ui.screens.*

sealed class NavGraph(val route: String) {
    object Root : NavGraph("root")
    object Auth : NavGraph("auth")
    object Main : NavGraph("main")
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(navController)
        }
        composable(Screen.Register.route) {
            RegisterScreen(navController)
        }
        composable(Screen.UserDashboard.route) {
            UserDashboardScreen(navController)
        }
        composable(Screen.AdminDashboard.route) {
            AdminDashboardScreen(navController)
        }
        composable(Screen.Profile.route) {
            ProfileScreen(navController)
        }
        composable(Screen.UserManagement.route) {
            UserManagementScreen(navController)
        }
    }
} 