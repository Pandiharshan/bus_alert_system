package com.campusbussbuddy.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.campusbussbuddy.data.repository.DriverRepository
import com.campusbussbuddy.firebase.BusInfo
import com.campusbussbuddy.firebase.DriverInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * UI state for the Driver Portal screen.
 */
data class DriverPortalUiState(
    val isLoading: Boolean = true,
    val driverInfo: DriverInfo? = null,
    val busInfo: BusInfo? = null,
    val errorMessage: String? = null
)

/**
 * ViewModel for the Driver Portal Home screen.
 * Manages Firebase data fetching and exposes UI state via StateFlow.
 *
 * Architecture:
 *   DriverHomeScreen → DriverViewModel → DriverRepository → FirebaseManager → Firestore
 */
class DriverViewModel(
    private val repository: DriverRepository = DriverRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(DriverPortalUiState())
    val uiState: StateFlow<DriverPortalUiState> = _uiState.asStateFlow()

    init {
        loadDriverData()
    }

    /**
     * Fetch the current driver's info and their assigned bus info from Firestore.
     *
     * Flow:
     * 1. FirebaseAuth → current user UID
     * 2. Firestore → drivers/{uid} → DriverInfo
     * 3. If assignedBusId is present → Firestore → buses/{busId} → BusInfo
     * 4. Update StateFlow → UI recomposes
     */
    fun loadDriverData() {
        viewModelScope.launch {
            _uiState.value = DriverPortalUiState(isLoading = true)

            try {
                Log.d("DriverViewModel", "Loading driver data...")

                // Step 1: Fetch driver document using current auth UID
                val driverInfo = repository.getCurrentDriverInfo()

                if (driverInfo == null) {
                    Log.w("DriverViewModel", "Driver info is null")
                    _uiState.value = DriverPortalUiState(
                        isLoading = false,
                        errorMessage = "Could not load driver information. Please try again."
                    )
                    return@launch
                }

                Log.d("DriverViewModel", "Driver loaded: ${driverInfo.name}, Bus ID: ${driverInfo.assignedBusId}")

                // Step 2: Fetch assigned bus info if available
                var busInfo: BusInfo? = null
                if (driverInfo.assignedBusId.isNotEmpty()) {
                    busInfo = repository.getBusInfo(driverInfo.assignedBusId)
                    Log.d("DriverViewModel", "Bus loaded: #${busInfo?.busNumber}")
                }

                // Step 3: Update UI state — triggers recomposition
                _uiState.value = DriverPortalUiState(
                    isLoading = false,
                    driverInfo = driverInfo,
                    busInfo = busInfo
                )

            } catch (e: Exception) {
                Log.e("DriverViewModel", "Error loading driver data", e)
                _uiState.value = DriverPortalUiState(
                    isLoading = false,
                    errorMessage = "Failed to load data: ${e.message}"
                )
            }
        }
    }

    /**
     * Refresh driver data (e.g., pull-to-refresh or retry).
     */
    fun refresh() {
        loadDriverData()
    }
}
