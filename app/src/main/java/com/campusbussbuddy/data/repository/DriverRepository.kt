package com.campusbussbuddy.data.repository

import android.util.Log
import com.campusbussbuddy.firebase.BusInfo
import com.campusbussbuddy.firebase.DriverInfo
import com.campusbussbuddy.firebase.DriverResult
import com.campusbussbuddy.firebase.FirebaseManager

/**
 * Repository for driver-related Firebase operations.
 * Wraps FirebaseManager calls to provide a clean data layer.
 */
class DriverRepository {

    /**
     * Get the currently logged-in driver's info from Firestore.
     * Uses FirebaseAuth UID → drivers/{uid} document.
     */
    suspend fun getCurrentDriverInfo(): DriverInfo? {
        return try {
            val driverInfo = FirebaseManager.getCurrentDriverInfo()
            Log.d("DriverRepository", "Fetched driver info: ${driverInfo?.name}")
            driverInfo
        } catch (e: Exception) {
            Log.e("DriverRepository", "Failed to fetch driver info", e)
            null
        }
    }

    /**
     * Get bus information by bus ID from Firestore.
     */
    suspend fun getBusInfo(busId: String): BusInfo? {
        return try {
            val busInfo = FirebaseManager.getBusInfo(busId)
            Log.d("DriverRepository", "Fetched bus info: Bus #${busInfo?.busNumber}")
            busInfo
        } catch (e: Exception) {
            Log.e("DriverRepository", "Failed to fetch bus info", e)
            null
        }
    }

    /**
     * Update driver name and phone in Firestore.
     * Also syncs to the active bus document for cross-app consistency.
     */
    suspend fun updateDriverProfile(uid: String, name: String, phone: String): DriverResult {
        return FirebaseManager.updateDriverProfile(uid, name, phone)
    }

    /**
     * Change the current driver's password via FirebaseAuth.
     */
    suspend fun changeDriverPassword(newPassword: String): DriverResult {
        return FirebaseManager.changeDriverPassword(newPassword)
    }
}
