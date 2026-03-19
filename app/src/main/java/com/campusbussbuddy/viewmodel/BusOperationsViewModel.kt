package com.campusbussbuddy.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.campusbussbuddy.firebase.FirebaseManager
import com.campusbussbuddy.firebase.StudentInfo
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.flow.combine
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
    val endTripStatus: String? = null
)

class BusOperationsViewModel(private val busId: String) : ViewModel() {

    private val _uiState = MutableStateFlow(BusOperationsUiState())
    val uiState: StateFlow<BusOperationsUiState> = _uiState.asStateFlow()

    private val db = FirebaseFirestore.getInstance()

    init {
        fetchStudents()
    }

    // Shared mutable source of truth for today's absent student IDs.
    // Both listeners below read/write this — whichever fires second always wins
    // and triggers a full re-computation of the student list.
    private val _absentIds = MutableStateFlow<List<String>>(emptyList())

    private fun fetchStudents() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                // Fetch Driver Info
                val driver = FirebaseManager.getCurrentDriverInfo()

                val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

                // ── Real-time absence listener ────────────────────────────────
                // Writes absent student IDs into _absentIds so the student
                // listener can always use the freshest set.
                db.collection("absences")
                    .whereEqualTo("busId", busId)
                    .whereEqualTo("date", todayStr)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null || snapshot == null) return@addSnapshotListener

                        val ids = snapshot.documents.mapNotNull { it.getString("studentId") }
                        _absentIds.value = ids

                        // Re-stamp every already-loaded student with the new flags
                        val updated = _uiState.value.students.map { s ->
                            s.copy(isAbsent = ids.contains(s.id))
                        }
                        _uiState.value = _uiState.value.copy(students = updated)
                    }

                // ── Real-time student listener ────────────────────────────────
                // Reads from _absentIds at call time; combined with the
                // absence listener above this guarantees consistency.
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
                            // Use the latest absent IDs — may have been set by the
                            // absence listener before or after this callback fires.
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
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading    = false,
                    errorMessage = e.message ?: "Unknown error"
                )
            }
        }
    }

    /**
     * Completely atomic End Trip execution.
     * 1. Resets the driver's isActive state.
     * 2. Frees the bus's activeDriverId.
     * 3. Mass-resets all students assigned to this bus back to "NOT_BOARDED".
     */
    fun endTrip(driverId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(endTripStatus = "PROCESSING")
            try {
                val batch = db.batch()

                // Reset Driver
                val driverRef = db.collection("drivers").document(driverId)
                batch.update(driverRef, "isActive", false)

                // Free Bus (Note: in the new system we just clear activeDriverId)
                val busRef = db.collection("buses").document(busId)
                batch.update(busRef, "activeDriverId", "")

                // Reset Students
                val studentsOnBus = db.collection("students")
                    .whereEqualTo("busId", busId)
                    .get()
                    .await()
                
                for (doc in studentsOnBus) {
                    batch.update(doc.reference, "status", "NOT_BOARDED")
                }

                // Commit all 100% atomically
                batch.commit().await()
                _uiState.value = _uiState.value.copy(endTripStatus = "SUCCESS")

            } catch (e: Exception) {
                Log.e("BusOperationsVM", "Batch End Trip failed", e)
                _uiState.value = _uiState.value.copy(
                    endTripStatus = "ERROR",
                    errorMessage = e.message
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
