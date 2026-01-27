package com.campusbussbuddy.data.repository

import com.campusbussbuddy.data.remote.FirebaseService
import com.campusbussbuddy.domain.model.User
import com.campusbussbuddy.domain.model.UserRole
import com.campusbussbuddy.domain.repository.AuthRepository
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthRepository @Inject constructor(
    private val firebaseService: FirebaseService
) : AuthRepository {

    override suspend fun signIn(email: String, password: String): Result<User> {
        return try {
            val authResult = firebaseService.auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: return Result.failure(Exception("Authentication failed"))
            
            // Fetch user data from Firestore using await()
            val userDoc = firebaseService.firestore
                .collection(FirebaseService.USERS_COLLECTION)
                .document(firebaseUser.uid)
                .get()
                .await()
            
            if (!userDoc.exists()) {
                // Sign out if user document doesn't exist
                firebaseService.auth.signOut()
                return Result.failure(Exception("User profile not found. Please contact support."))
            }
            
            val userData = userDoc.data!!
            val user = User(
                id = firebaseUser.uid,
                email = userData["email"] as String,
                name = userData["name"] as String,
                collegeId = userData["collegeId"] as String,
                role = UserRole.valueOf(userData["role"] as String)
            )
            
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(mapAuthException(e))
        }
    }

    override suspend fun signUp(
        email: String,
        password: String,
        name: String,
        collegeId: String,
        role: String
    ): Result<User> {
        return try {
            // Validate role before creating account
            val userRole = try {
                UserRole.valueOf(role.uppercase())
            } catch (e: IllegalArgumentException) {
                return Result.failure(Exception("Invalid role selected"))
            }
            
            val authResult = firebaseService.auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: return Result.failure(Exception("Registration failed"))
            
            // Create user document in Firestore using await()
            val userData = mapOf(
                "email" to email,
                "name" to name,
                "collegeId" to collegeId,
                "role" to userRole.name,
                "createdAt" to System.currentTimeMillis()
            )
            
            firebaseService.firestore
                .collection(FirebaseService.USERS_COLLECTION)
                .document(firebaseUser.uid)
                .set(userData)
                .await()
            
            val user = User(
                id = firebaseUser.uid,
                email = email,
                name = name,
                collegeId = collegeId,
                role = userRole
            )
            
            Result.success(user)
        } catch (e: Exception) {
            // If Firestore fails but auth succeeded, clean up auth
            try {
                firebaseService.auth.currentUser?.delete()?.await()
            } catch (cleanupException: Exception) {
                // Log cleanup failure but don't override original error
            }
            Result.failure(mapAuthException(e))
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return try {
            firebaseService.auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Sign out failed: ${e.message}"))
        }
    }

    override fun getCurrentUser(): Flow<User?> = flow {
        try {
            val firebaseUser = firebaseService.auth.currentUser
            if (firebaseUser != null) {
                // Fetch user data from Firestore using await()
                val userDoc = firebaseService.firestore
                    .collection(FirebaseService.USERS_COLLECTION)
                    .document(firebaseUser.uid)
                    .get()
                    .await()
                
                if (userDoc.exists()) {
                    val userData = userDoc.data!!
                    try {
                        val user = User(
                            id = firebaseUser.uid,
                            email = userData["email"] as String,
                            name = userData["name"] as String,
                            collegeId = userData["collegeId"] as String,
                            role = UserRole.valueOf(userData["role"] as String)
                        )
                        emit(user)
                    } catch (e: Exception) {
                        // Invalid role or missing data - sign out and emit null
                        firebaseService.auth.signOut()
                        emit(null)
                    }
                } else {
                    // User document doesn't exist - sign out and emit null
                    firebaseService.auth.signOut()
                    emit(null)
                }
            } else {
                emit(null)
            }
        } catch (e: Exception) {
            // Network error or other issue - emit null
            emit(null)
        }
    }
    
    private fun mapAuthException(exception: Exception): Exception {
        return when (exception) {
            is FirebaseAuthInvalidCredentialsException -> Exception("Invalid email or password")
            is FirebaseAuthInvalidUserException -> Exception("No account found with this email")
            is FirebaseAuthUserCollisionException -> Exception("An account with this email already exists")
            is FirebaseAuthWeakPasswordException -> Exception("Password is too weak. Use at least 6 characters")
            is FirebaseNetworkException -> Exception("Network error. Please check your connection")
            is FirebaseFirestoreException -> {
                when (exception.code) {
                    FirebaseFirestoreException.Code.PERMISSION_DENIED -> Exception("Access denied. Please contact support")
                    FirebaseFirestoreException.Code.UNAVAILABLE -> Exception("Service temporarily unavailable. Please try again")
                    else -> Exception("Database error: ${exception.message}")
                }
            }
            else -> Exception(exception.message ?: "An unexpected error occurred")
        }
    }
}