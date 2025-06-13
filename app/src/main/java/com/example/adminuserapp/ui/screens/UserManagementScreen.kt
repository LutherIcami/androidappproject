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
import com.example.adminuserapp.data.User
import com.example.adminuserapp.repository.AuthRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserManagementScreen(navController: NavController) {
    var users by remember { mutableStateOf<List<User>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showDeleteConfirmation by remember { mutableStateOf<User?>(null) }
    var showRoleUpdateDialog by remember { mutableStateOf<User?>(null) }
    var showEditDialog by remember { mutableStateOf<User?>(null) }
    var editedName by remember { mutableStateOf("") }
    var editedEmail by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val authRepository = remember { AuthRepository() }

    LaunchedEffect(Unit) {
        // Simulate loading users
        delay(1000)
        users = listOf(
            User(
                id = "1",
                email = "admin@example.com",
                name = "Admin User",
                isAdmin = true
            ),
            User(
                id = "2",
                email = "user@example.com",
                name = "Regular User",
                isAdmin = false
            )
        )
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Management") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                items(users) { user ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
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
                                Column {
                                    Text(
                                        text = user.name,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = user.email,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = if (user.isAdmin) "Role: Admin" else "Role: User",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (user.isAdmin) MaterialTheme.colorScheme.primary 
                                               else MaterialTheme.colorScheme.secondary
                                    )
                                }
                                Row {
                                    // Edit button
                                    IconButton(
                                        onClick = {
                                            editedName = user.name
                                            editedEmail = user.email
                                            showEditDialog = user
                                        }
                                    ) {
                                        Icon(
                                            Icons.Default.Edit,
                                            contentDescription = "Edit User"
                                        )
                                    }
                                    // Role update button
                                    IconButton(
                                        onClick = { showRoleUpdateDialog = user }
                                    ) {
                                        Icon(
                                            Icons.Default.Settings,
                                            contentDescription = "Update Role"
                                        )
                                    }
                                    // Delete button
                                    IconButton(
                                        onClick = { showDeleteConfirmation = user }
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Delete User",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    showDeleteConfirmation?.let { user ->
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = null },
            title = { Text("Delete User") },
            text = { Text("Are you sure you want to delete ${user.name}? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            users = users.filter { it.id != user.id }
                            showDeleteConfirmation = null
                        }
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Role Update Dialog
    showRoleUpdateDialog?.let { user ->
        AlertDialog(
            onDismissRequest = { showRoleUpdateDialog = null },
            title = { Text("Update User Role") },
            text = { 
                Column {
                    Text("Current role: ${if (user.isAdmin) "Admin" else "User"}")
                    Text("Do you want to ${if (user.isAdmin) "remove" else "grant"} admin privileges for ${user.name}?")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            users = users.map {
                                if (it.id == user.id) it.copy(isAdmin = !it.isAdmin)
                                else it
                            }
                            showRoleUpdateDialog = null
                        }
                    }
                ) {
                    Text("Update")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRoleUpdateDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Edit User Dialog
    showEditDialog?.let { user ->
        AlertDialog(
            onDismissRequest = { showEditDialog = null },
            title = { Text("Edit User") },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    OutlinedTextField(
                        value = editedName,
                        onValueChange = { editedName = it },
                        label = { Text("Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = editedEmail,
                        onValueChange = { editedEmail = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            users = users.map {
                                if (it.id == user.id) it.copy(
                                    name = editedName,
                                    email = editedEmail
                                )
                                else it
                            }
                            showEditDialog = null
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }
} 