package com.campusbussbuddy.firebase

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.MetadataChanges
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Real-time Data Manager for Campus Bus Buddy
 * Provides real-time synchronization using Firestore listeners
 * All data updates are pushed to the app instantly without manual refresh
 */
object RealtimeDataManager {
    
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val TAG = "RealtimeDataManager"
    
    // Store active listeners for cleanup
    private val activeListeners = mutableMapOf<String, ListenerRegistration>()
    
    // ==================== REAL-TIME DRIVERS ====================
    
    /**
     * Get real-time updates for all drivers
     * Emits a new list whenever any driver is added, updated, or deleted
     * @return Flow of driver list that updates in real-time
     */
    fun observeAllDrivers(): Flow<List<DriverInfo>> = callbackFlow {
        Log.d(TAG, "Starting real-time listener for all drivers")
        
        val listener = firestore.collection("drivers")
            .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error listening to drivers", error)
                    // Don't close the flow, just log the error
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val drivers = snapshot.documents.mapNotNull { doc ->
                        try {
                            DriverInfo(
                                uid = doc.id,
                                name = doc.getString("name") ?: "",
                                username = doc.getString("username") ?: "",
                                email = doc.getString("email") ?: "",
                                phone = doc.getString("phone") ?: "",
                                photoUrl = doc.getString("photoUrl") ?: "",
                                assignedBusId = doc.getString("assignedBusId") ?: "",
                                isActive = doc.getBoolean("isActive") ?: false
                            )
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing driver document: ${doc.id}", e)
                            null
                        }
                    }
                    
                    Log.d(TAG, "Drivers updated: ${drivers.size} drivers")
                    trySend(drivers)
                }
            }
        
        // Store listener for cleanup
        activeListeners["all_drivers"] = listener
        
        awaitClose {
            Log.d(TAG, "Closing real-time listener for all drivers")
            listener.remove()
            activeListeners.remove("all_drivers")
        }
    }
    
    /**
     * Get real-time updates for a specific driver
     * @param driverUid Driver's UID
     * @return Flow of driver info that updates in real-time
     */
    fun observeDriver(driverUid: String): Flow<DriverInfo?> = callbackFlow {
        Log.d(TAG, "Starting real-time listener for driver: $driverUid")
        
        val listener = firestore.collection("drivers")
            .document(driverUid)
            .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error listening to driver: $driverUid", error)
                    return@addSnapshotListener
                }
                
                if (snapshot != null && snapshot.exists()) {
                    try {
                        val driver = DriverInfo(
                            uid = snapshot.id,
                            name = snapshot.getString("name") ?: "",
                            username = snapshot.getString("username") ?: "",
                            email = snapshot.getString("email") ?: "",
                            phone = snapshot.getString("phone") ?: "",
                            photoUrl = snapshot.getString("photoUrl") ?: "",
                            assignedBusId = snapshot.getString("assignedBusId") ?: "",
                            isActive = snapshot.getBoolean("isActive") ?: false
                        )
                        Log.d(TAG, "Driver updated: ${driver.name}")
                        trySend(driver)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing driver: $driverUid", e)
                        trySend(null)
                    }
                } else {
                    Log.d(TAG, "Driver not found: $driverUid")
                    trySend(null)
                }
            }
        
        activeListeners["driver_$driverUid"] = listener
        
        awaitClose {
            Log.d(TAG, "Closing real-time listener for driver: $driverUid")
            listener.remove()
            activeListeners.remove("driver_$driverUid")
        }
    }
    
    // ==================== REAL-TIME STUDENTS ====================
    
    /**
     * Get real-time updates for all students
     * Emits a new list whenever any student is added, updated, or deleted
     * @return Flow of student list that updates in real-time
     */
    fun observeAllStudents(): Flow<List<StudentInfo>> = callbackFlow {
        Log.d(TAG, "Starting real-time listener for all students")
        
        val listener = firestore.collection("students")
            .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error listening to students", error)
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val students = snapshot.documents.mapNotNull { doc ->
                        try {
                            StudentInfo(
                                uid = doc.id,
                                name = doc.getString("name") ?: "",
                                username = doc.getString("username") ?: "",
                                email = doc.getString("email") ?: "",
                                busId = doc.getString("busId") ?: "",
                                stop = doc.getString("stop") ?: ""
                            )
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing student document: ${doc.id}", e)
                            null
                        }
                    }
                    
                    Log.d(TAG, "Students updated: ${students.size} students")
                    trySend(students)
                }
            }
        
        activeListeners["all_students"] = listener
        
        awaitClose {
            Log.d(TAG, "Closing real-time listener for all students")
            listener.remove()
            activeListeners.remove("all_students")
        }
    }
    
    /**
     * Get real-time updates for a specific student
     * @param studentUid Student's UID
     * @return Flow of student info that updates in real-time
     */
    fun observeStudent(studentUid: String): Flow<StudentInfo?> = callbackFlow {
        Log.d(TAG, "Starting real-time listener for student: $studentUid")
        
        val listener = firestore.collection("students")
            .document(studentUid)
            .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error listening to student: $studentUid", error)
                    return@addSnapshotListener
                }
                
                if (snapshot != null && snapshot.exists()) {
                    try {
                        val student = StudentInfo(
                            uid = snapshot.id,
                            name = snapshot.getString("name") ?: "",
                            username = snapshot.getString("username") ?: "",
                            email = snapshot.getString("email") ?: "",
                            busId = snapshot.getString("busId") ?: "",
                            stop = snapshot.getString("stop") ?: ""
                        )
                        Log.d(TAG, "Student updated: ${student.name}")
                        trySend(student)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing student: $studentUid", e)
                        trySend(null)
                    }
                } else {
                    Log.d(TAG, "Student not found: $studentUid")
                    trySend(null)
                }
            }
        
        activeListeners["student_$studentUid"] = listener
        
        awaitClose {
            Log.d(TAG, "Closing real-time listener for student: $studentUid")
            listener.remove()
            activeListeners.remove("student_$studentUid")
        }
    }
    
    /**
     * Get real-time updates for students assigned to a specific bus
     * @param busId Bus ID to filter students
     * @return Flow of student list for the bus
     */
    fun observeStudentsByBus(busId: String): Flow<List<StudentInfo>> = callbackFlow {
        Log.d(TAG, "Starting real-time listener for students on bus: $busId")
        
        val listener = firestore.collection("students")
            .whereEqualTo("busId", busId)
            .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error listening to students for bus: $busId", error)
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val students = snapshot.documents.mapNotNull { doc ->
                        try {
                            StudentInfo(
                                uid = doc.id,
                                name = doc.getString("name") ?: "",
                                username = doc.getString("username") ?: "",
                                email = doc.getString("email") ?: "",
                                busId = doc.getString("busId") ?: "",
                                stop = doc.getString("stop") ?: ""
                            )
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing student: ${doc.id}", e)
                            null
                        }
                    }
                    
                    Log.d(TAG, "Students for bus $busId updated: ${students.size} students")
                    trySend(students)
                }
            }
        
        activeListeners["students_bus_$busId"] = listener
        
        awaitClose {
            Log.d(TAG, "Closing real-time listener for students on bus: $busId")
            listener.remove()
            activeListeners.remove("students_bus_$busId")
        }
    }
    
    // ==================== REAL-TIME BUSES ====================
    
    /**
     * Get real-time updates for all buses
     * Emits a new list whenever any bus is added, updated, or deleted
     * @return Flow of bus list that updates in real-time
     */
    fun observeAllBuses(): Flow<List<BusInfo>> = callbackFlow {
        Log.d(TAG, "Starting real-time listener for all buses")
        
        val listener = firestore.collection("buses")
            .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error listening to buses", error)
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val buses = snapshot.documents.mapNotNull { doc ->
                        try {
                            BusInfo(
                                busId = doc.id,
                                busNumber = doc.getLong("busNumber")?.toInt() ?: 0,
                                capacity = doc.getLong("capacity")?.toInt() ?: 0,
                                activeDriverId = doc.getString("activeDriverId") ?: "",
                                activeDriverName = doc.getString("activeDriverName") ?: "",
                                activeDriverPhone = doc.getString("activeDriverPhone") ?: ""
                            )
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing bus document: ${doc.id}", e)
                            null
                        }
                    }
                    
                    Log.d(TAG, "Buses updated: ${buses.size} buses")
                    trySend(buses)
                }
            }
        
        activeListeners["all_buses"] = listener
        
        awaitClose {
            Log.d(TAG, "Closing real-time listener for all buses")
            listener.remove()
            activeListeners.remove("all_buses")
        }
    }
    
    /**
     * Get real-time updates for a specific bus
     * @param busId Bus ID
     * @return Flow of bus info that updates in real-time
     */
    fun observeBus(busId: String): Flow<BusInfo?> = callbackFlow {
        Log.d(TAG, "Starting real-time listener for bus: $busId")
        
        val listener = firestore.collection("buses")
            .document(busId)
            .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error listening to bus: $busId", error)
                    return@addSnapshotListener
                }
                
                if (snapshot != null && snapshot.exists()) {
                    try {
                        val bus = BusInfo(
                            busId = snapshot.id,
                            busNumber = snapshot.getLong("busNumber")?.toInt() ?: 0,
                            capacity = snapshot.getLong("capacity")?.toInt() ?: 0,
                            activeDriverId = snapshot.getString("activeDriverId") ?: "",
                            activeDriverName = snapshot.getString("activeDriverName") ?: "",
                            activeDriverPhone = snapshot.getString("activeDriverPhone") ?: ""
                        )
                        Log.d(TAG, "Bus updated: Bus ${bus.busNumber}")
                        trySend(bus)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing bus: $busId", e)
                        trySend(null)
                    }
                } else {
                    Log.d(TAG, "Bus not found: $busId")
                    trySend(null)
                }
            }
        
        activeListeners["bus_$busId"] = listener
        
        awaitClose {
            Log.d(TAG, "Closing real-time listener for bus: $busId")
            listener.remove()
            activeListeners.remove("bus_$busId")
        }
    }
    
    // ==================== REAL-TIME ADMINS ====================
    
    /**
     * Get real-time updates for all admins
     * @return Flow of admin list that updates in real-time
     */
    fun observeAllAdmins(): Flow<List<AdminInfo>> = callbackFlow {
        Log.d(TAG, "Starting real-time listener for all admins")
        
        val listener = firestore.collection("admins")
            .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error listening to admins", error)
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val admins = snapshot.documents.mapNotNull { doc ->
                        try {
                            AdminInfo(
                                uid = doc.id,
                                email = doc.getString("email") ?: "",
                                username = doc.getString("username") ?: "",
                                name = doc.getString("name") ?: "Administrator",
                                role = "admin"
                            )
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing admin document: ${doc.id}", e)
                            null
                        }
                    }
                    
                    Log.d(TAG, "Admins updated: ${admins.size} admins")
                    trySend(admins)
                }
            }
        
        activeListeners["all_admins"] = listener
        
        awaitClose {
            Log.d(TAG, "Closing real-time listener for all admins")
            listener.remove()
            activeListeners.remove("all_admins")
        }
    }
    
    // ==================== REAL-TIME STATISTICS ====================
    
    /**
     * Get real-time statistics for admin dashboard
     * Combines counts from all collections
     * @return Flow of dashboard statistics
     */
    fun observeDashboardStats(): Flow<DashboardStats> = callbackFlow {
        Log.d(TAG, "Starting real-time listener for dashboard stats")
        
        var studentCount = 0
        var driverCount = 0
        var busCount = 0
        var activeDriverCount = 0
        
        // Listen to students collection
        val studentsListener = firestore.collection("students")
            .addSnapshotListener { snapshot, error ->
                if (error == null && snapshot != null) {
                    studentCount = snapshot.size()
                    trySend(DashboardStats(studentCount, driverCount, busCount, activeDriverCount))
                }
            }
        
        // Listen to drivers collection
        val driversListener = firestore.collection("drivers")
            .addSnapshotListener { snapshot, error ->
                if (error == null && snapshot != null) {
                    driverCount = snapshot.size()
                    activeDriverCount = snapshot.documents.count { 
                        it.getBoolean("isActive") == true 
                    }
                    trySend(DashboardStats(studentCount, driverCount, busCount, activeDriverCount))
                }
            }
        
        // Listen to buses collection
        val busesListener = firestore.collection("buses")
            .addSnapshotListener { snapshot, error ->
                if (error == null && snapshot != null) {
                    busCount = snapshot.size()
                    trySend(DashboardStats(studentCount, driverCount, busCount, activeDriverCount))
                }
            }
        
        activeListeners["stats_students"] = studentsListener
        activeListeners["stats_drivers"] = driversListener
        activeListeners["stats_buses"] = busesListener
        
        awaitClose {
            Log.d(TAG, "Closing real-time listeners for dashboard stats")
            studentsListener.remove()
            driversListener.remove()
            busesListener.remove()
            activeListeners.remove("stats_students")
            activeListeners.remove("stats_drivers")
            activeListeners.remove("stats_buses")
        }
    }
    
    // ==================== CLEANUP ====================
    
    /**
     * Remove a specific listener
     * @param key Listener key
     */
    fun removeListener(key: String) {
        activeListeners[key]?.remove()
        activeListeners.remove(key)
        Log.d(TAG, "Removed listener: $key")
    }
    
    /**
     * Remove all active listeners
     * Call this when user logs out or app is destroyed
     */
    fun removeAllListeners() {
        Log.d(TAG, "Removing all listeners (${activeListeners.size} active)")
        activeListeners.values.forEach { it.remove() }
        activeListeners.clear()
    }
    
    /**
     * Get count of active listeners (for debugging)
     */
    fun getActiveListenerCount(): Int = activeListeners.size
}

/**
 * Dashboard statistics data class
 */
data class DashboardStats(
    val totalStudents: Int = 0,
    val totalDrivers: Int = 0,
    val totalBuses: Int = 0,
    val activeDrivers: Int = 0
)
