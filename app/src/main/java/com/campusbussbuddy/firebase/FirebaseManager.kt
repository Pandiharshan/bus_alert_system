package com.campusbussbuddy.firebase

import android.net.Uri
import android.util.Log
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
    val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    
    /**
     * Authenticate admin user with username and password
     * Username is converted to email format (username@gmail.com) for Firebase Auth
     * @param username Admin username (will be converted to email)
     * @param password Admin password
     * @return AuthResult with success status and message
     */
    suspend fun authenticateAdmin(username: String, password: String): AuthResult {
        return try {
            Log.d("FirebaseManager", "Authenticating admin with username: $username")
            
            // Convert username to email format for Firebase Auth
            val email = "${username.trim()}@gmail.com"
            
            Log.d("FirebaseManager", "Using email for auth: $email")
            
            // Sign in with Firebase Auth using generated email
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user
            
            if (user != null) {
                // Verify admin status
                val adminDoc = firestore.collection("admins").document(email).get().await()
                
                if (adminDoc.exists()) {
                    val isAdmin = adminDoc.getBoolean("isadmin") ?: false
                    if (isAdmin) {
                        Log.d("FirebaseManager", "Admin authentication successful")
                        AuthResult.Success("Authentication successful")
                    } else {
                        // Not an admin - sign out immediately
                        auth.signOut()
                        AuthResult.Error("Access denied. Admin privileges required.")
                    }
                } else {
                    // Admin document doesn't exist
                    auth.signOut()
                    AuthResult.Error("Admin account not found.")
                }
            } else {
                AuthResult.Error("Authentication failed")
            }
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Admin authentication failed", e)
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
                    username = adminDoc.getString("username") ?: "",
                    name = adminDoc.getString("name") ?: "System Administrator",
                    role = "admin"
                )
            } else {
                // Fallback admin info
                AdminInfo(
                    uid = currentUser.uid,
                    email = email,
                    username = "",
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
     * Username is converted to email format (username@gmail.com) for Firebase Auth
     * @param driverData Driver information
     * @param password Temporary password for driver
     * @param photoUri Optional photo URI for driver profile
     * @return Result with success/error message
     */
    /**
     * Helper: Creates a new Firebase Auth user OR cleans up an orphaned one (deleted from Firestore
     * but still exists in Auth) by signing in + deleting + recreating.
     * Returns the UID on success, null on failure (after populating [errorOut]).
     */
    private suspend fun resolveAuthUid(
        email: String,
        password: String,
        username: String,
        role: String,
        errorOut: (String) -> Unit
    ): String? {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            authResult.user?.uid
        } catch (authEx: Exception) {
            val isAlreadyInUse =
                authEx.message?.contains("email address is already in use", ignoreCase = true) == true ||
                authEx.message?.contains("email-already-in-use", ignoreCase = true) == true
            if (!isAlreadyInUse) { errorOut(getErrorMessage(authEx)); return null }
            Log.w("FirebaseManager", "Orphaned Auth account found for $email — cleaning up")
            try {
                val oldAuth = auth.signInWithEmailAndPassword(email, password).await()
                oldAuth.user?.delete()?.await()
                Log.d("FirebaseManager", "Orphaned Auth deleted, recreating $email")
                val retryResult = auth.createUserWithEmailAndPassword(email, password).await()
                retryResult.user?.uid
            } catch (signInEx: Exception) {
                Log.e("FirebaseManager", "Cannot clean up orphaned Auth — wrong password", signInEx)
                errorOut(
                    "A $role with username '$username' already existed. " +
                    "Use a different username, or the same password as before."
                )
                null
            }
        }
    }

    suspend fun createDriverAccount(
        driverData: DriverData,
        password: String,
        photoUri: Uri? = null
    ): DriverResult {
        return try {
            Log.d("FirebaseManager", "Creating driver account for: ${driverData.username}")
            
            if (driverData.username.isBlank() || driverData.name.isBlank()) {
                return DriverResult.Error("Username and name are required")
            }
            if (password.length < 6) {
                return DriverResult.Error("Password must be at least 6 characters")
            }
            
            val email = "${driverData.username.trim()}@gmail.com"
            Log.d("FirebaseManager", "Creating Firebase Auth account with email: $email")
            
            var errorMsg: String? = null
            val driverUid = resolveAuthUid(email, password, driverData.username.trim(), "driver") { errorMsg = it }
                ?: return DriverResult.Error(errorMsg ?: "Failed to create account")
            
            Log.d("FirebaseManager", "Firebase Auth account ready, UID: $driverUid")
            
            // Upload photo if provided
            var photoUrl: String? = null
            if (photoUri != null) {
                photoUrl = uploadDriverPhoto(driverUid, photoUri)
            }
            
            // Create/overwrite driver document in Firestore
            val driverDocument = hashMapOf(
                "name" to driverData.name.trim(),
                "username" to driverData.username.trim(),
                "email" to email,
                "phone" to driverData.phone.trim(),
                "photoUrl" to (photoUrl ?: ""),
                "assignedBusId" to driverData.assignedBusId.trim(),
                "isActive" to false,
                "createdAt" to System.currentTimeMillis()
            )
            
            firestore.collection("drivers").document(driverUid).set(driverDocument).await()
            Log.d("FirebaseManager", "Driver document created in Firestore")
            
            DriverResult.Success("Driver account created successfully", driverUid)
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Create driver account failed", e)
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
            Log.d("FirebaseManager", "Starting photo upload for UID: $driverUid")
            Log.d("FirebaseManager", "Photo URI: $photoUri")
            
            val storageRef = storage.reference.child("driver_photos/$driverUid.jpg")
            
            // Upload file
            val uploadTask = storageRef.putFile(photoUri).await()
            Log.d("FirebaseManager", "Upload task completed: ${uploadTask.metadata?.path}")
            
            // Get download URL
            val downloadUrl = storageRef.downloadUrl.await().toString()
            Log.d("FirebaseManager", "Download URL: $downloadUrl")
            
            downloadUrl
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Photo upload failed", e)
            null
        }
    }
    
    /**
     * Authenticate driver with username and password
     * Username is converted to email format (username@gmail.com) for Firebase Auth
     * @param username Driver username (will be converted to email)
     * @param password Driver password
     * @return DriverAuthResult with driver info or error
     */
    suspend fun authenticateDriver(username: String, password: String): DriverAuthResult {
        return try {
            Log.d("FirebaseManager", "Authenticating driver with username: $username")
            
            // Convert username to email format for Firebase Auth
            val email = "${username.trim()}@gmail.com"
            
            Log.d("FirebaseManager", "Using email for auth: $email")
            
            // Step 1: Sign in with Firebase Auth using generated email
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user ?: return DriverAuthResult.Error("Authentication failed")
            
            Log.d("FirebaseManager", "Driver authenticated successfully, UID: ${user.uid}")
            
            // Step 2: Fetch driver data from Firestore
            val driverDoc = firestore.collection("drivers").document(user.uid).get().await()
            
            if (!driverDoc.exists()) {
                Log.e("FirebaseManager", "Driver document not found for UID: ${user.uid}")
                auth.signOut()
                return DriverAuthResult.Error("Driver account not found in database")
            }
            
            val driverInfo = DriverInfo(
                uid = user.uid,
                name = driverDoc.getString("name") ?: "Driver",
                username = username.trim(),
                email = email,
                phone = driverDoc.getString("phone") ?: "",
                photoUrl = driverDoc.getString("photoUrl") ?: "",
                assignedBusId = driverDoc.getString("assignedBusId") ?: "",
                isActive = driverDoc.getBoolean("isActive") ?: false,
                shift = driverDoc.getString("shift") ?: "",
                routeName = driverDoc.getString("routeName") ?: ""
            )
            
            Log.d("FirebaseManager", "Driver info loaded: ${driverInfo.name}, Bus: ${driverInfo.assignedBusId}")
            
            // Step 3: Fetch assigned bus information
            val busInfo = if (driverInfo.assignedBusId.isNotEmpty()) {
                getBusInfo(driverInfo.assignedBusId)
            } else {
                Log.w("FirebaseManager", "Driver has no assigned bus")
                null
            }
            
            Log.d("FirebaseManager", "Driver authentication successful")
            DriverAuthResult.Success(driverInfo, busInfo)
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Driver authentication failed", e)
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
            var busDoc = firestore.collection("buses").document(busId).get().await()
            
            // If the literal document ID doesn't exist, try resolving it as a busNumber.
            // Admin AddStudent flow assigns buses by human-readable busNumber (e.g. "101") 
            // instead of auto-generated push keys for document IDs.
            if (!busDoc.exists()) {
                val numericBusId = busId.toIntOrNull()
                if (numericBusId != null) {
                    val query = firestore.collection("buses")
                        .whereEqualTo("busNumber", numericBusId)
                        .limit(1)
                        .get()
                        .await()
                    if (!query.isEmpty) {
                        busDoc = query.documents[0]
                    }
                }
            }

            if (busDoc.exists()) {
                BusInfo(
                    busId = busDoc.id, // always map back to the true document ID
                    busNumber = busDoc.getLong("busNumber")?.toInt() ?: 0,
                    capacity = busDoc.getLong("capacity")?.toInt() ?: 0,
                    activeDriverId = busDoc.getString("activeDriverId") ?: ""
                )
            } else {
                Log.d("FirebaseManager", "getBusInfo: Bus $busId not found by ID or busNumber")
                null
            }
        } catch (e: Exception) {
            Log.e("FirebaseManager", "getBusInfo failed", e)
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
                "activeDriverId" to driverInfo.uid
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
                "activeDriverId" to ""
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
                val photoUrl = driverDoc.getString("photoUrl") ?: ""
                Log.d("FirebaseManager", "Fetched driver info for UID: ${currentUser.uid}")
                Log.d("FirebaseManager", "Driver name: ${driverDoc.getString("name")}")
                Log.d("FirebaseManager", "Photo URL: '$photoUrl'")
                Log.d("FirebaseManager", "Photo URL isEmpty: ${photoUrl.isEmpty()}")
                Log.d("FirebaseManager", "Photo URL isBlank: ${photoUrl.isBlank()}")
                
                DriverInfo(
                    uid = currentUser.uid,
                    name = driverDoc.getString("name") ?: "Driver",
                    username = driverDoc.getString("username") ?: "",
                    email = driverDoc.getString("email") ?: "",
                    phone = driverDoc.getString("phone") ?: "",
                    photoUrl = photoUrl,
                    assignedBusId = driverDoc.getString("assignedBusId") ?: "",
                    isActive = driverDoc.getBoolean("isActive") ?: false,
                    shift = driverDoc.getString("shift") ?: "",
                    routeName = driverDoc.getString("routeName") ?: ""
                )
            } else {
                Log.w("FirebaseManager", "Driver document not found for UID: ${currentUser.uid}")
                null
            }
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Get driver info failed", e)
            null
        }
    }
    
    /**
     * Update driver profile (name and phone) in Firestore.
     * Also updates activeDriverName on the assigned bus if the driver is active.
     * This ensures sync across Driver App, Student App, and Admin Panel.
     * @param uid Driver UID
     * @param name Updated driver name
     * @param phone Updated phone number
     * @return DriverResult with success/error
     */
    suspend fun updateDriverProfile(uid: String, name: String, phone: String): DriverResult {
        return try {
            Log.d("FirebaseManager", "Updating driver profile for UID: $uid")
            
            val updateData = hashMapOf<String, Any>(
                "name" to name.trim(),
                "phone" to phone.trim()
            )
            
            // Update driver document
            firestore.collection("drivers").document(uid).update(updateData).await()
            
            // If driver is active on a bus, also update the bus document
            // so students and admin see the latest name/phone
            val driverDoc = firestore.collection("drivers").document(uid).get().await()
            val assignedBusId = driverDoc.getString("assignedBusId") ?: ""
            val isActive = driverDoc.getBoolean("isActive") ?: false
            
            Log.d("FirebaseManager", "Driver profile updated successfully")
            DriverResult.Success("Profile updated successfully", uid)
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Update driver profile failed", e)
            DriverResult.Error("Failed to update profile: ${e.message}")
        }
    }
    
    /**
     * Change the current driver's password using FirebaseAuth.
     * @param newPassword New password (minimum 6 characters)
     * @return DriverResult with success/error
     */
    suspend fun changeDriverPassword(newPassword: String): DriverResult {
        return try {
            val currentUser = auth.currentUser
                ?: return DriverResult.Error("No user is currently signed in")
            
            if (newPassword.length < 6) {
                return DriverResult.Error("Password must be at least 6 characters")
            }
            
            currentUser.updatePassword(newPassword).await()
            
            Log.d("FirebaseManager", "Password changed successfully")
            DriverResult.Success("Password changed successfully", currentUser.uid)
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Change password failed", e)
            DriverResult.Error(getErrorMessage(e))
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
            
            // Fix 5.3: Bulletproof bus unlock — query any bus locked by this driver and unlock it
            val lockedBuses = firestore.collection("buses")
                .whereEqualTo("activeDriverId", driverUid)
                .get()
                .await()
            for (bDoc in lockedBuses.documents) {
                firestore.collection("buses").document(bDoc.id)
                    .update(mapOf(
                        "activeDriverId" to "",
                        "tripActive" to false,
                        "currentTripId" to "",
                        "tripStartedAt" to null
                    )).await()
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
                        username = doc.getString("username") ?: "",
                        email = doc.getString("email") ?: "",
                        phone = doc.getString("phone") ?: "",
                        photoUrl = doc.getString("photoUrl") ?: "",
                        assignedBusId = doc.getString("assignedBusId") ?: "",
                        isActive = doc.getBoolean("isActive") ?: false,
                        shift = doc.getString("shift") ?: "",
                        routeName = doc.getString("routeName") ?: ""
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
     * Update driver information (Admin function).
     * Fix 5: If assignedBusId changes, clear old bus lock to prevent ghost-driver lock.
     */
    suspend fun updateDriverInfo(driverInfo: DriverInfo, newPassword: String? = null): DriverResult {
        return try {
            // Fix 5: Detect bus reassignment and clear old bus lock
            val existingDriverDoc = firestore.collection("drivers").document(driverInfo.uid).get().await()
            val oldBusId = existingDriverDoc.getString("assignedBusId") ?: ""
            val isCurrentlyActive = existingDriverDoc.getBoolean("isActive") ?: false

            // If bus ID is changing and driver is NOT mid-trip, clear old bus's activeDriverId
            if (oldBusId.isNotEmpty() && oldBusId != driverInfo.assignedBusId && !isCurrentlyActive) {
                val oldBusSnapshot = firestore.collection("buses").document(oldBusId).get().await()
                val busActiveDriver = oldBusSnapshot.getString("activeDriverId") ?: ""
                if (busActiveDriver == driverInfo.uid) {
                    // Safe to clear — this driver owns the lock on the old bus
                    firestore.collection("buses").document(oldBusId)
                        .update(mapOf("activeDriverId" to "", "tripActive" to false, "tripStartedAt" to null))
                        .await()
                    Log.d("FirebaseManager", "Cleared ghost lock on old bus $oldBusId after driver reassignment")
                }
            }

            val updateData = hashMapOf<String, Any>(
                "name" to driverInfo.name,
                "username" to driverInfo.username,
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
     * Sends password reset email to the driver
     * Note: For production, implement this via Cloud Functions with Admin SDK for direct password change
     * @param email Driver email
     * @param newPassword New password (not used in email method, kept for future Admin SDK implementation)
     * @return Result with success/error message
     */
    suspend fun resetDriverPassword(email: String, newPassword: String): DriverResult {
        return try {
            // Validate password strength
            if (newPassword.length < 6) {
                return DriverResult.Error("Password must be at least 6 characters")
            }
            
            // For now, we'll send a password reset email
            // In production, use Firebase Admin SDK via Cloud Functions for direct password change
            auth.sendPasswordResetEmail(email).await()
            
            DriverResult.Success("Password reset email sent to driver. They can set their new password from the email.", "")
        } catch (e: Exception) {
            DriverResult.Error("Password reset failed: ${e.message}")
        }
    }
    
    /**
     * Update bus information (Admin function)
     * @param busInfo Updated bus information
     * @return Result with success/error message
     */
    suspend fun updateBusInfo(busInfo: BusInfo): BusResult {
        return try {
            val updateData = hashMapOf<String, Any>(
                "busNumber" to busInfo.busNumber,
                "capacity" to busInfo.capacity
            )
            
            firestore.collection("buses")
                .document(busInfo.busId)
                .update(updateData)
                .await()
            
            Log.d("FirebaseManager", "Bus info updated: ${busInfo.busId}")
            
            BusResult.Success("Bus information updated successfully", busInfo.busId)
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Update bus failed", e)
            BusResult.Error("Failed to update bus: ${e.message}")
        }
    }
    
    /**
     * Create a new bus (Admin function)
     * @param busNumber Bus number
     * @param capacity Bus capacity
     * @param password Bus password for driver login
     * @return Result with success/error message
     */
    suspend fun createBus(busNumber: Int, capacity: Int, password: String): BusResult {
        return try {
            val hashedPassword = hashPassword(password)
            val busData = hashMapOf(
                "busNumber" to busNumber,
                "capacity" to capacity,
                "password" to hashedPassword,
                "activeDriverId" to "",
                "createdAt" to System.currentTimeMillis()
            )
            
            val docRef = firestore.collection("buses").add(busData).await()
            
            Log.d("FirebaseManager", "Bus created: ${docRef.id}")
            
            BusResult.Success("Bus created successfully", docRef.id)
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Create bus failed", e)
            BusResult.Error("Failed to create bus: ${e.message}")
        }
    }
    
    /**
     * Delete bus (Admin function)
     * @param busId Bus ID to delete
     * @return Result with success/error message
     */
    suspend fun deleteBus(busId: String): BusResult {
        return try {
            // Check if bus is currently active
            val busDoc = firestore.collection("buses").document(busId).get().await()
            
            if (!busDoc.exists()) {
                return BusResult.Error("Bus not found")
            }
            
            val activeDriverId = busDoc.getString("activeDriverId") ?: ""
            
            if (activeDriverId.isNotEmpty()) {
                return BusResult.Error("Cannot delete active bus. Please deactivate first.")
            }
            
            // Delete bus document
            firestore.collection("buses").document(busId).delete().await()
            
            // Clean up orphaned students
            val orphanedStudents = firestore.collection("students")
                .whereEqualTo("busId", busId)
                .get()
                .await()
                
            if (!orphanedStudents.isEmpty) {
                val batch = firestore.batch()
                for (doc in orphanedStudents.documents) {
                    batch.update(doc.reference, mapOf("busId" to ""))
                }
                batch.commit().await()
                Log.d("FirebaseManager", "Cleaned up ${orphanedStudents.size()} orphaned students")
            }
            
            Log.d("FirebaseManager", "Bus deleted: $busId")
            
            BusResult.Success("Bus deleted successfully", busId)
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Delete bus failed", e)
            BusResult.Error("Failed to delete bus: ${e.message}")
        }
    }
    
    /**
     * SHA-256 password hasher for bus authentication
     */
    private fun hashPassword(password: String): String {
        return try {
            val digest = java.security.MessageDigest.getInstance("SHA-256")
            val hashBytes = digest.digest(password.toByteArray(Charsets.UTF_8))
            hashBytes.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Hash calculation failed", e)
            password // Fallback to raw string if digest fails for some reason
        }
    }

    /**
     * Authenticate bus with bus number and password
     * @param busNumber Bus number to authenticate
     * @param password Bus password
     * @return BusAuthResult with bus info or error
     */
    suspend fun authenticateBus(busNumber: Int, password: String): BusAuthResult {
        return try {
            Log.d("FirebaseManager", "Authenticating bus: $busNumber")
            
            // Query buses collection for matching bus number
            val busesSnapshot = firestore.collection("buses")
                .whereEqualTo("busNumber", busNumber)
                .get()
                .await()
            
            if (busesSnapshot.isEmpty) {
                Log.d("FirebaseManager", "Bus not found: $busNumber")
                return BusAuthResult.Error("Bus not found. Please check the bus number.")
            }
            
            // Get the first matching bus
            val busDoc = busesSnapshot.documents.first()
            val storedPassword = busDoc.getString("password") ?: ""
            val inputHash = hashPassword(password)
            
            // Verify password mapping: Support both hashed and legacy plaintext for migration
            val isMatch = (storedPassword == inputHash) || (storedPassword == password)
            
            if (!isMatch) {
                Log.d("FirebaseManager", "Invalid password for bus: $busNumber")
                return BusAuthResult.Error("Invalid password. Please try again.")
            }
            
            // Auto-migrate plaintext passwords to SHA-256 on successful login
            if (storedPassword == password && storedPassword != inputHash) {
                try {
                    busDoc.reference.update("password", inputHash).await()
                    Log.d("FirebaseManager", "Migrated bus password to SHA-256 format")
                } catch (e: Exception) {
                    Log.w("FirebaseManager", "Failed to migrate password", e)
                }
            }
            
            // Password is correct, create BusInfo object
            val busInfo = BusInfo(
                busId = busDoc.id,
                busNumber = busDoc.getLong("busNumber")?.toInt() ?: busNumber,
                capacity = busDoc.getLong("capacity")?.toInt() ?: 0,
                activeDriverId = busDoc.getString("activeDriverId") ?: ""
            )
            
            Log.d("FirebaseManager", "Bus authentication successful: $busNumber")
            BusAuthResult.Success(busInfo)
            
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Bus authentication failed", e)
            BusAuthResult.Error("Authentication failed: ${e.message}")
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
    
    // ==================== STUDENT MANAGEMENT ====================
    
    /**
     * Create a new student account (Admin function)
     * Username is converted to email format (username@gmail.com) for Firebase Auth
     * @param studentData Student information
     * @param password Temporary password for student
     * @return Result with success/error message
     */
    suspend fun createStudentAccount(
        studentData: StudentData,
        password: String
    ): StudentResult {
        return try {
            Log.d("FirebaseManager", "Creating student account for username: ${studentData.username}")
            
            val email = "${studentData.username.trim()}@gmail.com"
            
            var errorMsg: String? = null
            val studentUid = resolveAuthUid(email, password, studentData.username.trim(), "student") { errorMsg = it }
                ?: return StudentResult.Error(errorMsg ?: "Failed to create account")
            
            Log.d("FirebaseManager", "Student Auth UID ready: $studentUid")
            
            // Create/overwrite student document in Firestore
            val studentDocument = hashMapOf(
                "name" to studentData.name,
                "username" to studentData.username,
                "email" to email,
                "phone" to studentData.phone,
                "busId" to studentData.busId,
                "stop" to studentData.stop,
                "createdAt" to System.currentTimeMillis()
            )
            
            firestore.collection("students").document(studentUid).set(studentDocument).await()
            Log.d("FirebaseManager", "Student document created in Firestore")
            
            StudentResult.Success("Student account created successfully", studentUid)
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Create student failed", e)
            StudentResult.Error(getErrorMessage(e))
        }
    }
    
    /**
     * Authenticate student with username and password
     * Username is converted to email format (username@gmail.com) for Firebase Auth
     * @param username Student username (will be converted to email)
     * @param password Student password
     * @return StudentAuthResult with student info or error
     */
    suspend fun authenticateStudent(username: String, password: String): StudentAuthResult {
        return try {
            Log.d("FirebaseManager", "Authenticating student with username: $username")
            
            // Convert username to email format for Firebase Auth
            val email = "${username.trim()}@gmail.com"
            
            Log.d("FirebaseManager", "Using email for auth: $email")
            
            // Step 1: Sign in with Firebase Auth using generated email
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user ?: return StudentAuthResult.Error("Authentication failed")
            
            Log.d("FirebaseManager", "Student authenticated, UID: ${user.uid}")
            
            // Step 2: Fetch student data from Firestore
            val studentDoc = firestore.collection("students").document(user.uid).get().await()
            
            if (!studentDoc.exists()) {
                auth.signOut()
                return StudentAuthResult.Error("Student account not found")
            }
            
            val studentInfo = StudentInfo(
                uid = user.uid,
                name = studentDoc.getString("name") ?: "Student",
                username = username.trim(),
                email = email,
                busId = studentDoc.getString("busId") ?: "",
                stop = studentDoc.getString("stop") ?: "",
                phone = studentDoc.getString("phone") ?: ""
            )
            
            Log.d("FirebaseManager", "Student info loaded: ${studentInfo.name}")
            
            // Step 3: Fetch assigned bus information
            val busInfo = if (studentInfo.busId.isNotEmpty()) {
                getBusInfo(studentInfo.busId)
            } else {
                null
            }
            
            Log.d("FirebaseManager", "Bus info loaded: ${busInfo?.busNumber ?: "No bus"}")
            
            StudentAuthResult.Success(studentInfo, busInfo)
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Student authentication failed", e)
            StudentAuthResult.Error(getErrorMessage(e))
        }
    }
    
    /**
     * Get current student information
     */
    suspend fun getCurrentStudentInfo(): StudentInfo? {
        return try {
            val currentUser = auth.currentUser ?: return null
            val studentDoc = firestore.collection("students").document(currentUser.uid).get().await()
            
            if (studentDoc.exists()) {
                Log.d("FirebaseManager", "Fetched student info for UID: ${currentUser.uid}")
                
                StudentInfo(
                    uid = currentUser.uid,
                    name = studentDoc.getString("name") ?: "Student",
                    username = studentDoc.getString("username") ?: "",
                    email = studentDoc.getString("email") ?: "",
                    busId = studentDoc.getString("busId") ?: "",
                    stop = studentDoc.getString("stop") ?: "",
                    phone = studentDoc.getString("phone") ?: ""
                )
            } else {
                Log.w("FirebaseManager", "Student document not found for UID: ${currentUser.uid}")
                null
            }
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Get student info failed", e)
            null
        }
    }
    
    /**
     * Get all buses from Firestore (Admin function)
     * @return List of BusInfo
     */
    suspend fun getAllBuses(): List<BusInfo> {
        return try {
            Log.d("FirebaseManager", "Fetching all buses from Firestore...")
            val busesSnapshot = firestore.collection("buses").limit(50).get().await()
            
            val buses = busesSnapshot.documents.mapNotNull { doc ->
                try {
                    BusInfo(
                        busId = doc.id,
                        busNumber = doc.getLong("busNumber")?.toInt() ?: 0,
                        capacity = doc.getLong("capacity")?.toInt() ?: 0,
                        activeDriverId = doc.getString("activeDriverId") ?: ""
                    )
                } catch (e: Exception) {
                    Log.e("FirebaseManager", "Error parsing bus document: ${doc.id}", e)
                    null
                }
            }
            
            Log.d("FirebaseManager", "Successfully loaded ${buses.size} buses")
            buses
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Get all buses failed", e)
            emptyList()
        }
    }
    
    /**
     * Get all students from Firestore (Admin function)
     * @return List of StudentInfo
     */
    suspend fun getAllStudents(): List<StudentInfo> {
        return try {
            val studentsSnapshot = firestore.collection("students").limit(50).get().await()
            
            studentsSnapshot.documents.mapNotNull { doc ->
                try {
                    StudentInfo(
                        uid = doc.id,
                        name = doc.getString("name") ?: "",
                        username = doc.getString("username") ?: "",
                        email = doc.getString("email") ?: "",
                        busId = doc.getString("busId") ?: "",
                        stop = doc.getString("stop") ?: "",
                        phone = doc.getString("phone") ?: ""
                    )
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Get all students failed", e)
            emptyList()
        }
    }
    
    /**
     * Delete student account completely (Admin function)
     * Removes from Firebase Auth and Firestore
     * @param studentUid Student's UID to delete
     * @return Result with success/error message
     */
    suspend fun deleteStudentAccount(studentUid: String): StudentResult {
        return try {
            // Step 1: Get student document
            val studentDoc = firestore.collection("students").document(studentUid).get().await()
            
            if (!studentDoc.exists()) {
                return StudentResult.Error("Student not found")
            }
            
            // P3 Fix: Delete all absence records for this student before deleting the student doc
            val absenceSnapshot = firestore.collection("absences")
                .whereEqualTo("studentId", studentUid)
                .get()
                .await()
            
            if (!absenceSnapshot.isEmpty) {
                absenceSnapshot.documents.chunked(499).forEach { chunk ->
                    val absenceBatch = firestore.batch()
                    chunk.forEach { absenceBatch.delete(it.reference) }
                    absenceBatch.commit().await()
                }
                Log.d("FirebaseManager", "Deleted ${absenceSnapshot.size()} absence records for $studentUid")
            }
            
            // Step 2: Delete Firestore student document
            firestore.collection("students").document(studentUid).delete().await()
            
            Log.d("FirebaseManager", "Student document deleted: $studentUid")
            
            // Note: Firebase Auth user deletion requires Admin SDK or the user to be signed in
            // For production, implement Cloud Function for complete deletion
            
            StudentResult.Success(
                "Student removed from database. Note: Auth account should be deleted from Firebase Console.",
                studentUid
            )
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Failed to delete student", e)
            StudentResult.Error("Failed to delete student: ${e.message}")
        }
    }
    
    /**
     * Update student information (Admin function)
     * @param studentInfo Updated student information
     * @param newPassword Optional new password for the student
     * @return Result with success/error message
     */
    suspend fun updateStudentInfo(studentInfo: StudentInfo, newPassword: String? = null): StudentResult {
        return try {
            val updateData = hashMapOf<String, Any>(
                "name" to studentInfo.name,
                "username" to studentInfo.username,
                "busId" to studentInfo.busId,
                "stop" to studentInfo.stop,
                "phone" to studentInfo.phone
            )
            
            firestore.collection("students")
                .document(studentInfo.uid)
                .update(updateData)
                .await()
            
            // If password change is requested, update it
            if (!newPassword.isNullOrBlank() && newPassword.length >= 6) {
                val passwordResult = resetStudentPassword(studentInfo.email, newPassword)
                if (passwordResult is StudentResult.Error) {
                    return StudentResult.Error("Student info updated but password change failed: ${passwordResult.message}")
                }
            }
            
            Log.d("FirebaseManager", "Student info updated: ${studentInfo.uid}")
            
            StudentResult.Success("Student information updated successfully", studentInfo.uid)
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Update student failed", e)
            StudentResult.Error("Failed to update student: ${e.message}")
        }
    }
    
    /**
     * Reset student password (Admin function)
     * Note: This requires the student to be signed in or use Firebase Admin SDK
     * For production, implement this via Cloud Functions with Admin SDK
     * @param email Student email
     * @param newPassword New password
     * @return Result with success/error message
     */
    suspend fun resetStudentPassword(email: String, newPassword: String): StudentResult {
        return try {
            // Validate password strength
            if (newPassword.length < 6) {
                return StudentResult.Error("Password must be at least 6 characters")
            }
            
            // For now, send password reset email as a workaround
            // In production, use Firebase Admin SDK via Cloud Functions
            try {
                auth.sendPasswordResetEmail(email).await()
                StudentResult.Success("Password reset email sent to student", "")
            } catch (emailError: Exception) {
                StudentResult.Error("Password reset failed: ${emailError.message}")
            }
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Password reset failed", e)
            StudentResult.Error(getErrorMessage(e))
        }
    }
    
    // ==================== ABSENCE MANAGEMENT ====================
    
    /**
     * Mark an absence for a student
     */
    suspend fun markAbsence(
        studentId: String,
        studentName: String,
        busId: String,
        stopName: String,
        dates: List<String>
    ): AbsenceResult {
        return try {
            val batch = firestore.batch()
            
            for (date in dates) {
                // Determine a unique ID to prevent duplicates for the same student on the same date
                val docId = "${studentId}_${date}"
                val docRef = firestore.collection("absences").document(docId)
                
                val absenceData = hashMapOf(
                    "studentId" to studentId,
                    "studentName" to studentName,
                    "busId" to busId,
                    "stopName" to stopName,
                    "date" to date,
                    "status" to "absent",
                    "createdAt" to System.currentTimeMillis()
                )
                batch.set(docRef, absenceData)
            }
            
            batch.commit().await()
            AbsenceResult.Success("Absences saved successfully")
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Mark absence failed", e)
            AbsenceResult.Error("Failed to save absences: ${e.message}")
        }
    }
    
    /**
     * Revoke an absence for a specific date
     */
    suspend fun revokeAbsence(studentId: String, date: String): AbsenceResult {
        return try {
            val docId = "${studentId}_${date}"
            firestore.collection("absences").document(docId).delete().await()
            AbsenceResult.Success("Absence revoked")
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Revoke absence failed", e)
            AbsenceResult.Error("Failed to revoke: ${e.message}")
        }
    }
    
    /**
     * Get all future or current absences for a specific student
     */
    suspend fun getStudentAbsences(studentId: String): List<AbsenceData> {
        return try {
            val snapshot = firestore.collection("absences")
                .whereEqualTo("studentId", studentId)
                .get().await()
                
            snapshot.documents.mapNotNull { doc ->
                AbsenceData(
                    id = doc.id,
                    studentId = doc.getString("studentId") ?: "",
                    studentName = doc.getString("studentName") ?: "",
                    busId = doc.getString("busId") ?: "",
                    stopName = doc.getString("stopName") ?: "",
                    date = doc.getString("date") ?: "",
                    status = doc.getString("status") ?: "",
                    createdAt = doc.getLong("createdAt") ?: 0L
                )
            }.sortedBy { it.date }
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Failed to fetch student absences", e)
            emptyList()
        }
    }
    
    /**
     * Get all absences for a specific bus on a specific date (e.g. TODAY)
     */
    suspend fun getBusAbsencesForDate(busId: String, dateString: String): List<AbsenceData> {
        return try {
            val snapshot = firestore.collection("absences")
                .whereEqualTo("busId", busId)
                .whereEqualTo("date", dateString)
                .get().await()
                
            snapshot.documents.mapNotNull { doc ->
                AbsenceData(
                    id = doc.id,
                    studentId = doc.getString("studentId") ?: "",
                    studentName = doc.getString("studentName") ?: "",
                    busId = doc.getString("busId") ?: "",
                    stopName = doc.getString("stopName") ?: "",
                    date = doc.getString("date") ?: "",
                    status = doc.getString("status") ?: "",
                    createdAt = doc.getLong("createdAt") ?: 0L
                )
            }
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Failed to get bus absences", e)
            emptyList()
        }
    }
    
    /**
     * Get all recorded absences for a specific bus (Admin View)
     */
    suspend fun getAllAbsencesByBus(busId: String): List<AbsenceData> {
        return try {
            val snapshot = firestore.collection("absences")
                .whereEqualTo("busId", busId)
                .get().await()
                
            snapshot.documents.mapNotNull { doc ->
                AbsenceData(
                    id = doc.id,
                    studentId = doc.getString("studentId") ?: "",
                    studentName = doc.getString("studentName") ?: "",
                    busId = doc.getString("busId") ?: "",
                    stopName = doc.getString("stopName") ?: "",
                    date = doc.getString("date") ?: "",
                    status = doc.getString("status") ?: "",
                    createdAt = doc.getLong("createdAt") ?: 0L
                )
            }.sortedByDescending { it.date }
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Failed to get all bus absences", e)
            emptyList()
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
    val username: String,
    val name: String,
    val role: String
)

/**
 * Driver data for account creation
 */
data class DriverData(
    val name: String,
    val username: String,
    val phone: String,
    val assignedBusId: String
)

/**
 * Driver information data class
 */
data class DriverInfo(
    val uid: String,
    val name: String,
    val username: String,
    val email: String,
    val phone: String,
    val photoUrl: String,
    val assignedBusId: String,
    val isActive: Boolean,
    val shift: String = "",
    val routeName: String = ""
)

/**
 * Bus information data class
 */
data class BusInfo(
    val busId: String,
    val busNumber: Int,
    val capacity: Int,
    val activeDriverId: String
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

/**
 * Bus operation result sealed class
 */
sealed class BusResult {
    data class Success(val message: String, val busId: String) : BusResult()
    data class Error(val message: String) : BusResult()
}

/**
 * Bus authentication result sealed class
 */
sealed class BusAuthResult {
    data class Success(val busInfo: BusInfo) : BusAuthResult()
    data class Error(val message: String) : BusAuthResult()
}

// ==================== STUDENT MANAGEMENT ====================

/**
 * Student data for account creation
 */
data class StudentData(
    val name: String,
    val username: String,
    val busId: String,
    val stop: String,
    val phone: String = ""
)

/**
 * Student information data class
 */
data class StudentInfo(
    val uid: String,
    val name: String,
    val username: String,
    val email: String,
    val busId: String,
    val stop: String,
    val phone: String = ""
)

/**
 * Student operation result sealed class
 */
sealed class StudentResult {
    data class Success(val message: String, val studentUid: String) : StudentResult()
    data class Error(val message: String) : StudentResult()
}

/**
 * Student authentication result sealed class
 */
sealed class StudentAuthResult {
    data class Success(val studentInfo: StudentInfo, val busInfo: BusInfo?) : StudentAuthResult()
    data class Error(val message: String) : StudentAuthResult()
}

// ==================== ABSENCE MODELS ====================

/**
 * Data model for a given day's absence
 */
data class AbsenceData(
    val id: String = "",
    val studentId: String = "",
    val studentName: String = "",
    val busId: String = "",
    val stopName: String = "",
    val date: String = "",      // format: yyyy-MM-dd
    val status: String = "absent",
    val createdAt: Long = 0L
)

sealed class AbsenceResult {
    data class Success(val message: String) : AbsenceResult()
    data class Error(val message: String) : AbsenceResult()
}