package com.campusbussbuddy.data.repository

import com.campusbussbuddy.domain.model.User
import com.campusbussbuddy.domain.model.UserRole
import com.campusbussbuddy.domain.repository.AuthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

// 🚀 DEV AUTH MODE - ZERO VALIDATION
// Set to true: Instant login, no validation, role selector
// Set to false: Uses real Firebase authentication
const val USE_DEV_AUTH = true

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuthRepository: FirebaseAuthRepository
) : AuthRepository {

    override suspend fun signIn(email: String, password: String): Result<User> {
        return if (USE_DEV_AUTH) {
            devSignIn(email, password)
        } else {
            firebaseAuthRepository.signIn(email, password)
        }
    }

    override suspend fun signUp(
        email: String,
        password: String,
        name: String,
        collegeId: String,
        role: String
    ): Result<User> {
        return if (USE_DEV_AUTH) {
            devSignUp(email, password, name, collegeId, role)
        } else {
            firebaseAuthRepository.signUp(email, password, name, collegeId, role)
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return if (USE_DEV_AUTH) {
            devSignOut()
        } else {
            firebaseAuthRepository.signOut()
        }
    }

    override fun getCurrentUser(): Flow<User?> {
        return if (USE_DEV_AUTH) {
            devGetCurrentUser()
        } else {
            firebaseAuthRepository.getCurrentUser()
        }
    }

    // Dev implementations - NO VALIDATION
    private suspend fun devSignIn(email: String, @Suppress("UNUSED_PARAMETER") password: String): Result<User> {
        delay(200) // Quick dev login
        
        // For dev mode, we'll determine role from the email prefix
        // But we need to make sure this works with the UI role selector
        val role = when {
            email.startsWith("driver") -> UserRole.DRIVER
            email.startsWith("student") -> UserRole.STUDENT
            else -> UserRole.STUDENT // Default to student
        }
        
        val devUser = User(
            id = "dev_user",
            email = email.ifBlank { "dev@test.com" },
            name = "Dev User",
            collegeId = "DEV123",
            role = role
        )
        
        return Result.success(devUser)
    }

    private suspend fun devSignUp(
        email: String,
        @Suppress("UNUSED_PARAMETER") password: String,
        name: String,
        collegeId: String,
        role: String
    ): Result<User> {
        delay(200) // Quick dev signup
        
        val userRole = try {
            UserRole.valueOf(role.uppercase())
        } catch (e: IllegalArgumentException) {
            UserRole.STUDENT
        }
        
        val devUser = User(
            id = "dev_user_${System.currentTimeMillis()}",
            email = email.ifBlank { "dev@test.com" },
            name = name.ifBlank { "Dev User" },
            collegeId = collegeId.ifBlank { "DEV123" },
            role = userRole
        )
        
        return Result.success(devUser)
    }

    private suspend fun devSignOut(): Result<Unit> {
        delay(100) // Instant sign out
        return Result.success(Unit)
    }

    private fun devGetCurrentUser(): Flow<User?> = flow {
        // Always emit null for dev mode (no persistent session)
        emit(null)
    }
}