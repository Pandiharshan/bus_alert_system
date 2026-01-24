package com.campusbussbuddy.domain.repository

import com.campusbussbuddy.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun signIn(email: String, password: String): Result<User>
    suspend fun signUp(email: String, password: String, name: String, collegeId: String, role: String): Result<User>
    suspend fun signOut(): Result<Unit>
    fun getCurrentUser(): Flow<User?>
}