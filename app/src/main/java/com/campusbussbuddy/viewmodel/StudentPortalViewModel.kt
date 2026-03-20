package com.campusbussbuddy.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.campusbussbuddy.firebase.BusInfo
import com.campusbussbuddy.firebase.DriverInfo
import com.campusbussbuddy.firebase.FirebaseManager
import com.campusbussbuddy.firebase.StudentInfo
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

data class StudentPortalUiState(
    val isLoading: Boolean = true,
    val studentInfo: StudentInfo? = null,
    val busInfo: BusInfo? = null,
    val activeDriver: DriverInfo? = null,
    val upcomingAbsenceCount: Int = 0,
    val showScanner: Boolean = false,
    val showProfile: Boolean = false,
    val isAbsentToday: Boolean = false,
    val errorMessage: String? = null
)

class StudentPortalViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(StudentPortalUiState())
    val uiState: StateFlow<StudentPortalUiState> = _uiState.asStateFlow()

    init {
        fetchPortalData()
    }

    fun setScannerVisible(isVisible: Boolean) {
        _uiState.value = _uiState.value.copy(showScanner = isVisible)
    }

    fun setProfileVisible(isVisible: Boolean) {
        _uiState.value = _uiState.value.copy(showProfile = isVisible)
    }

    private var busListener: com.google.firebase.firestore.ListenerRegistration? = null

    // Fix 6: Cache the last known activeDriverId to avoid re-fetching driver
    // doc on every bus document update when the driver hasn't changed
    private var cachedActiveDriverId: String = ""

    override fun onCleared() {
        super.onCleared()
        busListener?.remove()
    }

    fun fetchPortalData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                // Step 1: Fetch current student document
                val student = FirebaseManager.getCurrentStudentInfo()
                if (student == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Could not fetch student profile."
                    )
                    return@launch
                }

                // Step 2: Fetch absences
                val absences = FirebaseManager.getStudentAbsences(student.uid)
                val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                val upcomingCount = absences.count { it.date >= todayStr }
                val absentToday = absences.any { it.date == todayStr }

                // We do NOT set isLoading = false yet! We wait until we get the bus.
                var currentState = _uiState.value.copy(
                    studentInfo = student,
                    upcomingAbsenceCount = upcomingCount,
                    isAbsentToday = absentToday
                )

                // Step 3: Immediately fetch bus info so portal shows bus number right away
                if (student.busId.isNotEmpty()) {
                    try {
                        val busDoc = FirebaseManager.firestore.collection("buses").document(student.busId).get().await()
                        if (busDoc.exists()) {
                            val immediateBus = BusInfo(
                                busId = busDoc.id,
                                busNumber = busDoc.getLong("busNumber")?.toInt() ?: 0,
                                capacity = busDoc.getLong("capacity")?.toInt() ?: 0,
                                activeDriverId = busDoc.getString("activeDriverId") ?: ""
                            )
                            currentState = currentState.copy(busInfo = immediateBus)
                            Log.d("StudentPortalViewModel", "Immediately loaded bus: ${immediateBus.busNumber}")
                        } else {
                            Log.w("StudentPortalViewModel", "Bus document ${student.busId} does not exist in Firestore")
                        }
                    } catch (e: Exception) {
                        Log.e("StudentPortalViewModel", "Failed to immediate-load bus info", e)
                    }
                }
                
                // Now we have everything we need to show the UI without flickering
                _uiState.value = currentState.copy(isLoading = false)

                // Step 4: Listen to assigned bus in real-time
                if (student.busId.isNotEmpty()) {
                    busListener?.remove()
                    busListener = FirebaseManager.firestore.collection("buses").document(student.busId)
                        .addSnapshotListener { snapshot, error ->
                            if (error != null) {
                                Log.e("StudentPortalViewModel", "Bus snapshot error", error)
                                return@addSnapshotListener
                            }
                            if (snapshot == null || !snapshot.exists()) {
                                Log.w("StudentPortalViewModel", "Bus snapshot null/non-existent for ${student.busId}")
                                return@addSnapshotListener
                            }
                            
                            val busNumber = snapshot.getLong("busNumber")?.toInt() ?: 0
                            val capacity = snapshot.getLong("capacity")?.toInt() ?: 0
                            val activeDriverId = snapshot.getString("activeDriverId") ?: ""
                            
                            val liveBus = BusInfo(
                                busId = snapshot.id,
                                busNumber = busNumber,
                                capacity = capacity,
                                activeDriverId = activeDriverId
                            )

                            if (activeDriverId.isNotEmpty()) {
                                if (activeDriverId != cachedActiveDriverId) {
                                    cachedActiveDriverId = activeDriverId
                                    FirebaseManager.firestore.collection("drivers").document(activeDriverId)
                                        .get()
                                        .addOnSuccessListener { doc ->
                                            if (doc.exists()) {
                                                val liveDriver = DriverInfo(
                                                    uid = doc.id,
                                                    name = doc.getString("name") ?: "Driver",
                                                    username = doc.getString("username") ?: "",
                                                    email = doc.getString("email") ?: "",
                                                    phone = doc.getString("phone") ?: "",
                                                    photoUrl = doc.getString("photoUrl") ?: "",
                                                    assignedBusId = doc.getString("assignedBusId") ?: "",
                                                    isActive = doc.getBoolean("isActive") ?: false,
                                                    shift = doc.getString("shift") ?: "",
                                                    routeName = doc.getString("routeName") ?: ""
                                                )
                                                _uiState.value = _uiState.value.copy(
                                                    busInfo = liveBus,
                                                    activeDriver = liveDriver
                                                )
                                            }
                                        }
                                } else {
                                    _uiState.value = _uiState.value.copy(busInfo = liveBus)
                                }
                            } else {
                                cachedActiveDriverId = ""
                                _uiState.value = _uiState.value.copy(
                                    busInfo = liveBus,
                                    activeDriver = null
                                )
                            }
                        }
                } else {
                    // No bus ID, so just stop loading
                    _uiState.value = currentState.copy(isLoading = false)
                }

            } catch (e: Exception) {
                Log.e("StudentPortalViewModel", "Error fetching data", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Unknown error occurred"
                )
            }
        }
    }
}
