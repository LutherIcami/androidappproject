package com.example.adminuserapp.repository

import com.example.adminuserapp.data.User
import kotlinx.coroutines.delay

class AuthRepository {
    private var currentUser: User? = null

    suspend fun signIn(email: String, password: String): User {
        // Simulate network delay
        delay(1000)
        
        // Simulate authentication
        return if (email == "admin@example.com" && password == "admin@123") {
            User(
                id = "1",
                email = email,
                name = "Admin User",
                isAdmin = true
            )
        } else if (email == "user@example.com" && password == "user123") {
            User(
                id = "2",
                email = email,
                name = "Regular User",
                isAdmin = false
            )
        } else {
            throw Exception("Invalid email or password")
        }
    }

    suspend fun signUp(email: String, password: String, name: String): Result<User> {
        return try {
            // Simulate network delay
            delay(1000)
            
            // Simulate registration
            if (email.isBlank() || password.isBlank() || name.isBlank()) {
                throw Exception("All fields are required")
            }
            
            // Create new user
            val user = User(
                id = System.currentTimeMillis().toString(),
                email = email,
                name = name,
                isAdmin = false
            )
            
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getCurrentUser(): User? = currentUser

    suspend fun signOut() {
        // Simulate network delay
        delay(500)
        // In a real app, you would clear the auth token/session here
        currentUser = null
    }
} 