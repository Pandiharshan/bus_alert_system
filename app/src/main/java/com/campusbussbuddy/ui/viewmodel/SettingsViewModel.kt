package com.campusbussbuddy.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.campusbussbuddy.data.repository.DriverRepository
import com.campusbussbuddy.firebase.DriverInfo
import com.campusbussbuddy.firebase.DriverResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * UI state for the Driver Settings screen.
 */
data class SettingsUiState(
    val isLoading: Boolean = true,
    val driverInfo: DriverInfo? = null,
    val name: String = "",
    val phone: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isUpdatingProfile: Boolean = false,
    val isChangingPassword: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)

/**
 * ViewModel for the Driver Settings screen.
 * Handles profile editing and password changes via Firebase.
 */
class SettingsViewModel(
    private val repository: DriverRepository = DriverRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadDriverData()
    }

    private fun loadDriverData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val driverInfo = repository.getCurrentDriverInfo()
                if (driverInfo != null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        driverInfo = driverInfo,
                        name = driverInfo.name,
                        phone = driverInfo.phone
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Could not load driver information"
                    )
                }
            } catch (e: Exception) {
                Log.e("SettingsViewModel", "Error loading driver data", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to load data: ${e.message}"
                )
            }
        }
    }

    fun onNameChange(name: String) {
        _uiState.value = _uiState.value.copy(name = name, successMessage = null, errorMessage = null)
    }

    fun onPhoneChange(phone: String) {
        _uiState.value = _uiState.value.copy(phone = phone, successMessage = null, errorMessage = null)
    }

    fun onNewPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(newPassword = password, successMessage = null, errorMessage = null)
    }

    fun onConfirmPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = password, successMessage = null, errorMessage = null)
    }

    fun updateProfile() {
        val state = _uiState.value
        val uid = state.driverInfo?.uid ?: return

        if (state.name.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Name cannot be empty")
            return
        }

        if (state.phone.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Phone number cannot be empty")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUpdatingProfile = true, successMessage = null, errorMessage = null)
            when (val result = repository.updateDriverProfile(uid, state.name, state.phone)) {
                is DriverResult.Success -> {
                    val updatedInfo = repository.getCurrentDriverInfo()
                    _uiState.value = _uiState.value.copy(
                        isUpdatingProfile = false,
                        driverInfo = updatedInfo,
                        successMessage = "Profile updated successfully"
                    )
                }
                is DriverResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isUpdatingProfile = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }

    fun changePassword() {
        val state = _uiState.value

        if (state.newPassword.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Please enter a new password")
            return
        }
        if (state.newPassword.length < 6) {
            _uiState.value = state.copy(errorMessage = "Password must be at least 6 characters")
            return
        }
        if (state.newPassword != state.confirmPassword) {
            _uiState.value = state.copy(errorMessage = "Passwords do not match")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isChangingPassword = true, successMessage = null, errorMessage = null)
            when (val result = repository.changeDriverPassword(state.newPassword)) {
                is DriverResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isChangingPassword = false,
                        newPassword = "",
                        confirmPassword = "",
                        successMessage = "Password changed successfully"
                    )
                }
                is DriverResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isChangingPassword = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(successMessage = null, errorMessage = null)
    }
}
