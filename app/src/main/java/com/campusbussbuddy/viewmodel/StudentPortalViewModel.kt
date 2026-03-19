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

                var bus: BusInfo? = null
                var driver: DriverInfo? = null

                // Step 2: Fetch assigned bus
                if (student.busId.isNotEmpty()) {
                    bus = FirebaseManager.getBusInfo(student.busId)

                    // Step 3: Fetch active driver explicitly by their ID (solving the stale string bug)
                    if (bus != null && bus.activeDriverId.isNotEmpty()) {
                        val doc = FirebaseManager.firestore.collection("drivers").document(bus.activeDriverId).get().await()
                        if (doc.exists()) {
                            driver = DriverInfo(
                                uid = doc.id,
                                name = doc.getString("name") ?: "Driver",
                                username = doc.getString("username") ?: "",
                                email = doc.getString("email") ?: "",
                                phone = doc.getString("phone") ?: "",
                                photoUrl = doc.getString("photoUrl") ?: "",
                                assignedBusId = doc.getString("assignedBusId") ?: "",
                                isActive = doc.getBoolean("isActive") ?: false
                            )
                        }
                    }
                }

                // Step 4: Fetch absences
                val absences = FirebaseManager.getStudentAbsences(student.uid)
                val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                val upcomingCount = absences.count { it.date >= todayStr }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    studentInfo = student,
                    busInfo = bus,
                    activeDriver = driver,
                    upcomingAbsenceCount = upcomingCount
                )

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
