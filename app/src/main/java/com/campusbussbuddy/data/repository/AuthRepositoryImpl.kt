package com.campusbussbuddy.data.repository

import com.campusbussbuddy.data.remote.FirebaseService
import com.campusbussbuddy.domain.model.User
import com.campusbussbuddy.domain.model.UserRole
import com.campusbussbuddy.domain.repository.AuthRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseService: FirebaseService
) : AuthRepository {

    override suspend fun signIn(email: String, password: String): Result<User> {
        return try {
            val authResult = firebaseService.auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: return Result.failure(Exception("Authentication failed"))
            
            // Fetch user data from Firestore
            val userDoc = firebaseService.firestore
                .collection(FirebaseService.USERS_COLLECTION)
                .document(firebaseUser.uid)
                .get()
                .await()
            
            if (!userDoc.exists()) {
                // Sign out if user document doesn't exist
                firebaseService.auth.signOut()
                return Result.failure(Exception("User profile not found"))
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
            Result.failure(e)
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
            val authResult = firebaseService.auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: return Result.failure(Exception("Registration failed"))
            
            // Create user document in Firestore
            val userData = mapOf(
                "email" to email,
                "name" to name,
                "collegeId" to collegeId,
                "role" to role,
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
                role = UserRole.valueOf(role)
            )
            
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return try {
            firebaseService.auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCurrentUser(): Flow<User?> = callbackFlow {
        val listener = firebaseService.auth.addAuthStateListener { auth ->
            val firebaseUser = auth.currentUser
            if (firebaseUser != null) {
                // Fetch user data from Firestore
                firebaseService.firestore
                    .collection(FirebaseService.USERS_COLLECTION)
                    .document(firebaseUser.uid)
                    .get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            val userData = document.data!!
                            try {
                                val user = User(
                                    id = firebaseUser.uid,
                                    email = userData["email"] as String,
                                    name = userData["name"] as String,
                                    collegeId = userData["collegeId"] as String,
                                    role = UserRole.valueOf(userData["role"] as String)
                                )
                                trySend(user)
                            } catch (e: Exception) {
                                // Invalid role or missing data - sign out
                                firebaseService.auth.signOut()
                                trySend(null)
                            }
                        } else {
                            // User document doesn't exist - sign out
                            firebaseService.auth.signOut()
                            trySend(null)
                        }
                    }
                    .addOnFailureListener {
                        // Network error or other issue - sign out
                        firebaseService.auth.signOut()
                        trySend(null)
                    }
            } else {
                trySend(null)
            }
        }
        
        awaitClose {
            firebaseService.auth.removeAuthStateListener(listener)
        }
    }
}