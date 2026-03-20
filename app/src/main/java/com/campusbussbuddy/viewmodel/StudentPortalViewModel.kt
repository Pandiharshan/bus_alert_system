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

    private var busListener: com.google.firebase.firestore.ListenerRegistration? = null

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

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    studentInfo = student,
                    upcomingAbsenceCount = upcomingCount
                )

                // Step 3: Listen to assigned bus in real-time
                if (student.busId.isNotEmpty()) {
                    busListener?.remove()
                    busListener = FirebaseManager.firestore.collection("buses").document(student.busId)
                        .addSnapshotListener { snapshot, error ->
                            if (error != null || snapshot == null || !snapshot.exists()) return@addSnapshotListener
                            
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
                                _uiState.value = _uiState.value.copy(
                                    busInfo = liveBus,
                                    activeDriver = null
                                )
                            }
                        }
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
