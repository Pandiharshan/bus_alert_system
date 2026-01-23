package com.campusbussbuddy.domain.usecase

import com.campusbussbuddy.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun signIn(email: String, password: String): Result<User>
    suspend fun signOut(): Result<Unit>
    fun getCurrentUser(): Flow<User?>
}

class SignInUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        return authRepository.signIn(email, password)
    }
}

class SignOutUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(): Result<Unit> {
        return authRepository.signOut()
    }
}

class GetCurrentUserUseCase(private val authRepository: AuthRepository) {
    operator fun invoke(): Flow<User?> {
        return authRepository.getCurrentUser()
    }
}