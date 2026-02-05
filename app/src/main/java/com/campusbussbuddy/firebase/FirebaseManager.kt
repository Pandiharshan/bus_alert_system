package com.campusbussbuddy.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Firebase Manager for Campus Bus Buddy
 * Handles authentication and database operations
 */
object FirebaseManager {
    
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    
    /**
     * Authenticate admin user with email and password
     * @param email Admin email
     * @param password Admin password
     * @return AuthResult with success status and message
     */
    suspend fun authenticateAdmin(email: String, password: String): AuthResult {
        return try {
            // Sign in with Firebase Auth
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user
            
            if (user != null) {
                // Verify admin status using email as document ID
                val isAdmin = verifyAdminStatus(email)
                if (isAdmin) {
                    AuthResult.Success("Authentication successful")
                } else {
                    // Not an admin - sign out immediately
                    auth.signOut()
                    AuthResult.Error("Access denied. Admin privileges required.")
                }
            } else {
                AuthResult.Error("Authentication failed")
            }
        } catch (e: Exception) {
            AuthResult.Error(getErrorMessage(e))
        }
    }
    
    /**
     * Verify if user has admin privileges using email as document ID
     * @param email User email to verify
     * @return true if user is admin, false otherwise
     */
    private suspend fun verifyAdminStatus(email: String): Boolean {
        return try {
            // Check admin document using email as document ID
            val adminDoc = firestore.collection("admins").document(email).get().await()
            
            if (adminDoc.exists()) {
                // Check if isadmin field is true
                val isAdmin = adminDoc.getBoolean("isadmin") ?: false
                isAdmin
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Get current authenticated user
     */
    fun getCurrentUser() = auth.currentUser
    
    /**
     * Sign out current user
     */
    fun signOut() {
        auth.signOut()
    }
    
    /**
     * Check if user is currently signed in
     */
    fun isUserSignedIn(): Boolean {
        return auth.currentUser != null
    }
    
    /**
     * Get admin information from Firestore
     */
    suspend fun getAdminInfo(): AdminInfo? {
        return try {
            val currentUser = auth.currentUser ?: return null
            val email = currentUser.email ?: return null
            
            val adminDoc = firestore.collection("admins").document(email).get().await()
            
            if (adminDoc.exists()) {
                AdminInfo(
                    uid = currentUser.uid,
                    email = email,
                    name = adminDoc.getString("name") ?: "System Administrator",
                    role = "admin"
                )
            } else {
                // Fallback admin info
                AdminInfo(
                    uid = currentUser.uid,
                    email = email,
                    name = "System Administrator",
                    role = "admin"
                )
            }
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Convert Firebase exception to user-friendly error message
     */
    private fun getErrorMessage(exception: Exception): String {
        return when {
            exception.message?.contains("password", ignoreCase = true) == true -> 
                "Invalid password. Please check your credentials."
            exception.message?.contains("email", ignoreCase = true) == true -> 
                "Invalid email address format."
            exception.message?.contains("user-not-found", ignoreCase = true) == true -> 
                "No admin account found with this email."
            exception.message?.contains("wrong-password", ignoreCase = true) == true -> 
                "Incorrect password. Please try again."
            exception.message?.contains("invalid-email", ignoreCase = true) == true -> 
                "Invalid email address format."
            exception.message?.contains("user-disabled", ignoreCase = true) == true -> 
                "This admin account has been disabled."
            exception.message?.contains("too-many-requests", ignoreCase = true) == true -> 
                "Too many failed attempts. Please try again later."
            exception.message?.contains("network", ignoreCase = true) == true -> 
                "Network error. Please check your connection."
            else -> "Authentication failed. Please try again."
        }
    }
}

/**
 * Authentication result sealed class
 */
sealed class AuthResult {
    data class Success(val message: String) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

/**
 * Admin information data class
 */
data class AdminInfo(
    val uid: String,
    val email: String,
    val name: String,
    val role: String
)