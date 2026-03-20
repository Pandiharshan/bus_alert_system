package com.campusbussbuddy.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.campusbussbuddy.firebase.FirebaseManager
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

enum class StudentStatus {
    WAITING_TO_BOARD,
    BOARDED
}

data class StudentMember(
    val id: String,
    val name: String,
    val initials: String,
    val status: StudentStatus,
    val phoneNumber: String = "",
    val isAbsent: Boolean = false
)

data class BusOperationsUiState(
    val isLoading: Boolean = true,
    val driverInfo: com.campusbussbuddy.firebase.DriverInfo? = null,
    val students: List<StudentMember> = emptyList(),
    val errorMessage: String? = null,
    val isTripStarted: Boolean = false,
    val currentTripId: String = "",
    val endTripStatus: String? = null
)

class BusOperationsViewModel(private val busId: String) : ViewModel() {

    private val _uiState = MutableStateFlow(BusOperationsUiState())
    val uiState: StateFlow<BusOperationsUiState> = _uiState.asStateFlow()

    private val db = FirebaseFirestore.getInstance()

    // Fix 8: Guard flags — prevent duplicate listener registration on re-fetch
    private var studentsListenerRegistered = false
    private var absenceListenerRegistered = false

    // Shared mutable source of truth for today's absent student IDs
    private val _absentIds = MutableStateFlow<List<String>>(emptyList())

    init {
        fetchDriverInfo()  // Load driver name immediately for greeting
        fetchStudents()
        restoreTripState() // Fix 1: Restore tripActive from Firestore after app kill
    }

    /**
     * Load the current logged-in driver's info immediately so the
     * greeting and profile sections show the name on first render.
     */
    private fun fetchDriverInfo() {
        viewModelScope.launch {
            try {
                val driver = FirebaseManager.getCurrentDriverInfo()
                if (driver != null) {
                    _uiState.value = _uiState.value.copy(driverInfo = driver)
                }
            } catch (e: Exception) {
                Log.e("BusOperationsVM", "fetchDriverInfo failed", e)
            }
        }
    }

    /**
     * Fix 1: On app restart, read `tripActive` from the bus document
     * to restore the in-memory `isTripStarted` state.
     */
    private fun restoreTripState() {
        viewModelScope.launch {
            try {
                val busDoc = db.collection("buses").document(busId).get().await()
                val isActive = busDoc.getBoolean("tripActive") ?: false
                val cTripId = busDoc.getString("currentTripId") ?: ""
                _uiState.value = _uiState.value.copy(
                    isTripStarted = isActive,
                    currentTripId = cTripId
                )
                Log.d("BusOperationsVM", "Restored tripActive=$isActive from Firestore")
            } catch (e: Exception) {
                Log.e("BusOperationsVM", "Failed to restore trip state", e)
            }
        }
    }

    private fun fetchStudents() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val driver = FirebaseManager.getCurrentDriverInfo()
                val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

                // Fix 8: Only register absence listener once
                if (!absenceListenerRegistered) {
                    absenceListenerRegistered = true
                    db.collection("absences")
                        .whereEqualTo("busId", busId)
                        .whereEqualTo("date", todayStr)
                        .addSnapshotListener { snapshot, error ->
                            if (error != null || snapshot == null) return@addSnapshotListener
                            val ids = snapshot.documents.mapNotNull { it.getString("studentId") }
                            _absentIds.value = ids
                            val updated = _uiState.value.students.map { s ->
                                s.copy(isAbsent = ids.contains(s.id))
                            }
                            _uiState.value = _uiState.value.copy(students = updated)
                        }
                }

                // Fix 8: Only register student listener once
                if (!studentsListenerRegistered) {
                    studentsListenerRegistered = true
                    db.collection("students")
                        .whereEqualTo("busId", busId)
                        .addSnapshotListener { snapshot, e ->
                            if (e != null) {
                                Log.e("BusOperationsVM", "Student listen failed.", e)
                                _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    errorMessage = "Failed to load students."
                                )
                                return@addSnapshotListener
                            }
                            if (snapshot != null) {
                                val latestAbsents = _absentIds.value
                                val studentList = snapshot.documents.mapNotNull { doc ->
                                    try {
                                        val name = doc.getString("name") ?: "Unknown"
                                        val initials = name.split(" ").take(2)
                                            .joinToString("") { it.take(1) }.uppercase()
                                        val statusStr = doc.getString("status") ?: "WAITING_TO_BOARD"
                                        StudentMember(
                                            id          = doc.id,
                                            name        = name,
                                            initials    = initials,
                                            status      = if (statusStr == "BOARDED") StudentStatus.BOARDED
                                                          else StudentStatus.WAITING_TO_BOARD,
                                            phoneNumber = doc.getString("phone") ?: "",
                                            isAbsent    = latestAbsents.contains(doc.id)
                                        )
                                    } catch (err: Exception) { null }
                                }
                                _uiState.value = _uiState.value.copy(
                                    isLoading  = false,
                                    driverInfo = driver,
                                    students   = studentList
                                )
                            }
                        }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading    = false,
                    errorMessage = e.message ?: "Unknown error"
                )
            }
        }
    }

    fun startTrip() {
        viewModelScope.launch {
            val driverInfo = _uiState.value.driverInfo ?: return@launch
            if (driverInfo.uid.isEmpty()) return@launch
            try {
                val tripId = UUID.randomUUID().toString()
                // Fix 1 & 3.1: Persist tripActive and a unique currentTripId to defeat screenshots
                db.collection("buses").document(busId)
                    .update(mapOf(
                        "tripActive" to true,
                        "currentTripId" to tripId,
                        "tripStartedAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
                    )).await()

                val updatedDriver = driverInfo.copy(assignedBusId = busId, isActive = true)
                FirebaseManager.activateDriverAndLockBus(updatedDriver)
                _uiState.value = _uiState.value.copy(
                    isTripStarted = true,
                    currentTripId = tripId
                )
                Log.d("BusOperationsVM", "Trip started and persisted to Firestore")
            } catch (e: Exception) {
                Log.e("BusOperationsVM", "Failed to start trip", e)
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to start trip. Please check your connection."
                )
            }
        }
    }

    /**
     * Completely safe End Trip execution.
     * 1. First: Mass-reset all students in chunks of 499 with up to 3 retries.
     * 2. Then: Atomic batch reset driver isActive + clear bus activeDriverId + tripActive.
     * 
     * By doing students first, if the network drops halfway through, the bus remains locked
     * and the driver can just tap "Complete Trip" again to retry without corrupting the trip state.
     */
    fun endTrip(driverId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(endTripStatus = "PROCESSING")
            try {
                // --- STEP 1: Reset student statuses with retry ---
                val studentsOnBus = db.collection("students")
                    .whereEqualTo("busId", busId)
                    .get()
                    .await()

                // Fix 7 & 19.4: Retry each chunk up to 3 times on transient network failure
                // Doing this FIRST makes the entire operation idempotent
                studentsOnBus.documents.chunked(499).forEach { chunk ->
                    var attempt = 0
                    var chunkSuccess = false
                    while (!chunkSuccess && attempt < 3) {
                        try {
                            val studentBatch = db.batch()
                            chunk.forEach { doc ->
                                studentBatch.update(doc.reference, "status", "NOT_BOARDED")
                            }
                            studentBatch.commit().await()
                            chunkSuccess = true
                        } catch (retryEx: Exception) {
                            attempt++
                            Log.w("BusOperationsVM", "Student batch attempt $attempt failed: ${retryEx.message}")
                            if (attempt >= 3) throw retryEx
                            delay(1000L * attempt) // exponential back-off: 1s, 2s
                        }
                    }
                }

                // --- STEP 2: Reset driver + bus state atomically ---
                // Only executed if all students were successfully reset!
                val coreBatch = db.batch()
                val driverRef = db.collection("drivers").document(driverId)
                coreBatch.update(driverRef, "isActive", false)
                val busRef = db.collection("buses").document(busId)
                // Fix 1: Also clear tripActive and tripStartedAt on end trip
                coreBatch.update(
                    busRef, mapOf(
                        "activeDriverId" to "",
                        "tripActive"     to false,
                        "currentTripId"  to "",
                        "tripStartedAt"  to null
                    )
                )
                coreBatch.commit().await()

                _uiState.value = _uiState.value.copy(isTripStarted = false, endTripStatus = "SUCCESS")

            } catch (e: Exception) {
                Log.e("BusOperationsVM", "End Trip failed", e)
                _uiState.value = _uiState.value.copy(
                    endTripStatus = "ERROR",
                    errorMessage  = "Trip ended but some student statuses may not have reset. Please check network."
                )
            }
        }
    }

    fun markStudentBoarded(studentId: String) {
        viewModelScope.launch {
            try {
                db.collection("students").document(studentId).update("status", "BOARDED").await()
            } catch (e: Exception) {
                Log.e("BusOperationsVM", "Failed to force board student", e)
            }
        }
    }
}
