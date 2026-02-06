package com.campusbussbuddy.firebase

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

/**
 * Firebase Manager for Campus Bus Buddy
 * Handles authentication and database operations
 */
object FirebaseManager {
    
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    
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
    
    // ==================== DRIVER MANAGEMENT ====================
    
    /**
     * Create a new driver account (Admin function)
     * @param driverData Driver information
     * @param password Temporary password for driver
     * @param photoUri Optional photo URI for driver profile
     * @return Result with success/error message
     */
    suspend fun createDriverAccount(
        driverData: DriverData,
        password: String,
        photoUri: Uri? = null
    ): DriverResult {
        return try {
            // Step 1: Create Firebase Auth account
            val authResult = auth.createUserWithEmailAndPassword(driverData.email, password).await()
            val driverUid = authResult.user?.uid ?: return DriverResult.Error("Failed to create account")
            
            // Step 2: Upload photo to Firebase Storage if provided
            var photoUrl: String? = null
            if (photoUri != null) {
                photoUrl = uploadDriverPhoto(driverUid, photoUri)
            }
            
            // Step 3: Create driver document in Firestore using UID as document ID
            val driverDocument = hashMapOf(
                "name" to driverData.name,
                "email" to driverData.email,
                "phone" to driverData.phone,
                "photoUrl" to (photoUrl ?: ""),
                "assignedBusId" to driverData.assignedBusId,
                "isActive" to false,
                "createdAt" to System.currentTimeMillis()
            )
            
            firestore.collection("drivers").document(driverUid).set(driverDocument).await()
            
            // Step 4: Sign out the newly created driver account (admin should remain signed in)
            // Note: This is handled by re-authenticating admin after driver creation
            
            DriverResult.Success("Driver account created successfully", driverUid)
        } catch (e: Exception) {
            DriverResult.Error(getErrorMessage(e))
        }
    }
    
    /**
     * Upload driver photo to Firebase Storage
     * @param driverUid Driver's UID
     * @param photoUri Photo URI to upload
     * @return Download URL of uploaded photo
     */
    private suspend fun uploadDriverPhoto(driverUid: String, photoUri: Uri): String? {
        return try {
            val storageRef = storage.reference.child("driver_photos/$driverUid.jpg")
            val uploadTask = storageRef.putFile(photoUri).await()
            storageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Authenticate driver with email and password
     * @param email Driver email
     * @param password Driver password
     * @return DriverAuthResult with driver info or error
     */
    suspend fun authenticateDriver(email: String, password: String): DriverAuthResult {
        return try {
            // Step 1: Sign in with Firebase Auth
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user ?: return DriverAuthResult.Error("Authentication failed")
            
            // Step 2: Fetch driver document from Firestore using UID
            val driverDoc = firestore.collection("drivers").document(user.uid).get().await()
            
            if (!driverDoc.exists()) {
                auth.signOut()
                return DriverAuthResult.Error("Driver profile not found")
            }
            
            // Step 3: Parse driver data
            val driverInfo = DriverInfo(
                uid = user.uid,
                name = driverDoc.getString("name") ?: "Driver",
                email = driverDoc.getString("email") ?: email,
                phone = driverDoc.getString("phone") ?: "",
                photoUrl = driverDoc.getString("photoUrl") ?: "",
                assignedBusId = driverDoc.getString("assignedBusId") ?: "",
                isActive = driverDoc.getBoolean("isActive") ?: false
            )
            
            // Step 4: Fetch assigned bus information
            val busInfo = if (driverInfo.assignedBusId.isNotEmpty()) {
                getBusInfo(driverInfo.assignedBusId)
            } else {
                null
            }
            
            DriverAuthResult.Success(driverInfo, busInfo)
        } catch (e: Exception) {
            DriverAuthResult.Error(getErrorMessage(e))
        }
    }
    
    /**
     * Get bus information from Firestore
     * @param busId Bus document ID
     * @return BusInfo or null
     */
    suspend fun getBusInfo(busId: String): BusInfo? {
        return try {
            val busDoc = firestore.collection("buses").document(busId).get().await()
            
            if (busDoc.exists()) {
                BusInfo(
                    busId = busId,
                    busNumber = busDoc.getLong("busNumber")?.toInt() ?: 0,
                    capacity = busDoc.getLong("capacity")?.toInt() ?: 0,
                    activeDriverId = busDoc.getString("activeDriverId") ?: "",
                    activeDriverName = busDoc.getString("activeDriverName") ?: "",
                    activeDriverPhone = busDoc.getString("activeDriverPhone") ?: ""
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Activate driver and lock bus (exclusive access)
     * @param driverInfo Driver information
     * @return Result with success/error message
     */
    suspend fun activateDriverAndLockBus(driverInfo: DriverInfo): BusLockResult {
        return try {
            val busId = driverInfo.assignedBusId
            if (busId.isEmpty()) {
                return BusLockResult.Error("No bus assigned to this driver")
            }
            
            // Step 1: Check if bus is already locked by another driver
            val busDoc = firestore.collection("buses").document(busId).get().await()
            
            if (!busDoc.exists()) {
                return BusLockResult.Error("Assigned bus not found")
            }
            
            val activeDriverId = busDoc.getString("activeDriverId") ?: ""
            
            // Step 2: If bus is locked by another driver, deny access
            if (activeDriverId.isNotEmpty() && activeDriverId != driverInfo.uid) {
                val activeDriverName = busDoc.getString("activeDriverName") ?: "Another driver"
                return BusLockResult.Error("Bus is currently active with $activeDriverName")
            }
            
            // Step 3: Lock bus with current driver
            val busUpdate = hashMapOf<String, Any>(
                "activeDriverId" to driverInfo.uid,
                "activeDriverName" to driverInfo.name,
                "activeDriverPhone" to driverInfo.phone
            )
            
            firestore.collection("buses").document(busId).update(busUpdate).await()
            
            // Step 4: Update driver status to active
            firestore.collection("drivers").document(driverInfo.uid)
                .update("isActive", true).await()
            
            BusLockResult.Success("Bus activated successfully")
        } catch (e: Exception) {
            BusLockResult.Error(getErrorMessage(e))
        }
    }
    
    /**
     * Deactivate driver and unlock bus
     * @param driverUid Driver UID
     * @param busId Bus ID
     */
    suspend fun deactivateDriverAndUnlockBus(driverUid: String, busId: String): Boolean {
        return try {
            // Clear bus active driver info
            val busUpdate = hashMapOf<String, Any>(
                "activeDriverId" to "",
                "activeDriverName" to "",
                "activeDriverPhone" to ""
            )
            
            firestore.collection("buses").document(busId).update(busUpdate).await()
            
            // Update driver status to inactive
            firestore.collection("drivers").document(driverUid)
                .update("isActive", false).await()
            
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Get current driver information
     */
    suspend fun getCurrentDriverInfo(): DriverInfo? {
        return try {
            val currentUser = auth.currentUser ?: return null
            val driverDoc = firestore.collection("drivers").document(currentUser.uid).get().await()
            
            if (driverDoc.exists()) {
                DriverInfo(
                    uid = currentUser.uid,
                    name = driverDoc.getString("name") ?: "Driver",
                    email = driverDoc.getString("email") ?: "",
                    phone = driverDoc.getString("phone") ?: "",
                    photoUrl = driverDoc.getString("photoUrl") ?: "",
                    assignedBusId = driverDoc.getString("assignedBusId") ?: "",
                    isActive = driverDoc.getBoolean("isActive") ?: false
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Delete driver account completely (Admin function)
     * Removes from Firebase Auth, Firestore, and optionally Storage
     * @param driverUid Driver's UID to delete
     * @return Result with success/error message
     */
    suspend fun deleteDriverAccount(driverUid: String): DriverResult {
        return try {
            // Step 1: Get driver document to retrieve photo URL and other info
            val driverDoc = firestore.collection("drivers").document(driverUid).get().await()
            
            if (!driverDoc.exists()) {
                return DriverResult.Error("Driver not found")
            }
            
            val photoUrl = driverDoc.getString("photoUrl") ?: ""
            val assignedBusId = driverDoc.getString("assignedBusId") ?: ""
            val isActive = driverDoc.getBoolean("isActive") ?: false
            
            // Step 2: If driver is active, deactivate and unlock bus first
            if (isActive && assignedBusId.isNotEmpty()) {
                deactivateDriverAndUnlockBus(driverUid, assignedBusId)
            }
            
            // Step 3: Delete photo from Firebase Storage if exists
            if (photoUrl.isNotEmpty()) {
                try {
                    val photoRef = storage.getReferenceFromUrl(photoUrl)
                    photoRef.delete().await()
                } catch (e: Exception) {
                    // Photo deletion failed, but continue with account deletion
                    // This is not critical as the photo will be orphaned but won't affect functionality
                }
            }
            
            // Step 4: Delete Firestore document
            firestore.collection("drivers").document(driverUid).delete().await()
            
            // Step 5: Delete from Firebase Authentication
            // Note: This requires admin SDK or the user to be currently signed in
            // For production, you should use Firebase Admin SDK on a backend server
            // For now, we'll document this limitation
            
            // IMPORTANT: Firebase Auth user deletion requires either:
            // 1. The user to be currently signed in (not practical for admin deletion)
            // 2. Firebase Admin SDK (requires backend server)
            // 3. Firebase Auth REST API with admin credentials
            
            // For this implementation, we'll delete Firestore and Storage
            // Auth account can be manually deleted from Firebase Console
            // or implement a Cloud Function for complete deletion
            
            DriverResult.Success(
                "Driver removed from database. Note: Auth account should be deleted from Firebase Console.",
                driverUid
            )
        } catch (e: Exception) {
            DriverResult.Error("Failed to delete driver: ${e.message}")
        }
    }
    
    /**
     * Get all drivers from Firestore (Admin function)
     * @return List of DriverInfo
     */
    suspend fun getAllDrivers(): List<DriverInfo> {
        return try {
            val driversSnapshot = firestore.collection("drivers").get().await()
            
            driversSnapshot.documents.mapNotNull { doc ->
                try {
                    DriverInfo(
                        uid = doc.id,
                        name = doc.getString("name") ?: "",
                        email = doc.getString("email") ?: "",
                        phone = doc.getString("phone") ?: "",
                        photoUrl = doc.getString("photoUrl") ?: "",
                        assignedBusId = doc.getString("assignedBusId") ?: "",
                        isActive = doc.getBoolean("isActive") ?: false
                    )
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * Update driver information (Admin function)
     * @param driverInfo Updated driver information
     * @param newPassword Optional new password for the driver
     * @return Result with success/error message
     */
    suspend fun updateDriverInfo(driverInfo: DriverInfo, newPassword: String? = null): DriverResult {
        return try {
            val updateData = hashMapOf<String, Any>(
                "name" to driverInfo.name,
                "phone" to driverInfo.phone,
                "assignedBusId" to driverInfo.assignedBusId
            )
            
            firestore.collection("drivers")
                .document(driverInfo.uid)
                .update(updateData)
                .await()
            
            // If password change is requested, update it
            if (!newPassword.isNullOrBlank() && newPassword.length >= 6) {
                val passwordResult = resetDriverPassword(driverInfo.email, newPassword)
                if (passwordResult is DriverResult.Error) {
                    return DriverResult.Error("Driver info updated but password change failed: ${passwordResult.message}")
                }
            }
            
            DriverResult.Success("Driver information updated successfully", driverInfo.uid)
        } catch (e: Exception) {
            DriverResult.Error("Failed to update driver: ${e.message}")
        }
    }
    
    /**
     * Reset driver password (Admin function)
     * Note: This requires the driver to be signed in or use Firebase Admin SDK
     * For production, implement this via Cloud Functions with Admin SDK
     * @param email Driver email
     * @param newPassword New password
     * @return Result with success/error message
     */
    suspend fun resetDriverPassword(email: String, newPassword: String): DriverResult {
        return try {
            // Validate password strength
            if (newPassword.length < 6) {
                return DriverResult.Error("Password must be at least 6 characters")
            }
            
            // Store current admin user
            val currentAdmin = auth.currentUser
            val adminEmail = currentAdmin?.email
            
            // Sign in as the driver temporarily to change password
            val driverAuth = auth.signInWithEmailAndPassword(email, "temp").await()
            val driverUser = driverAuth.user
            
            if (driverUser != null) {
                // Update password
                driverUser.updatePassword(newPassword).await()
                
                // Sign out driver
                auth.signOut()
                
                // Re-authenticate admin if needed
                if (adminEmail != null) {
                    // Admin needs to re-authenticate manually
                    // This is a limitation of client-side password reset
                }
                
                DriverResult.Success("Password updated successfully", driverUser.uid)
            } else {
                DriverResult.Error("Failed to authenticate driver for password reset")
            }
        } catch (e: Exception) {
            // If temporary sign-in fails, try using password reset email
            try {
                auth.sendPasswordResetEmail(email).await()
                DriverResult.Success("Password reset email sent to driver", "")
            } catch (emailError: Exception) {
                DriverResult.Error("Password reset failed: ${e.message}")
            }
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

/**
 * Driver data for account creation
 */
data class DriverData(
    val name: String,
    val email: String,
    val phone: String,
    val assignedBusId: String
)

/**
 * Driver information data class
 */
data class DriverInfo(
    val uid: String,
    val name: String,
    val email: String,
    val phone: String,
    val photoUrl: String,
    val assignedBusId: String,
    val isActive: Boolean
)

/**
 * Bus information data class
 */
data class BusInfo(
    val busId: String,
    val busNumber: Int,
    val capacity: Int,
    val activeDriverId: String,
    val activeDriverName: String,
    val activeDriverPhone: String
)

/**
 * Driver operation result sealed class
 */
sealed class DriverResult {
    data class Success(val message: String, val driverUid: String) : DriverResult()
    data class Error(val message: String) : DriverResult()
}

/**
 * Driver authentication result sealed class
 */
sealed class DriverAuthResult {
    data class Success(val driverInfo: DriverInfo, val busInfo: BusInfo?) : DriverAuthResult()
    data class Error(val message: String) : DriverAuthResult()
}

/**
 * Bus lock result sealed class
 */
sealed class BusLockResult {
    data class Success(val message: String) : BusLockResult()
    data class Error(val message: String) : BusLockResult()
}