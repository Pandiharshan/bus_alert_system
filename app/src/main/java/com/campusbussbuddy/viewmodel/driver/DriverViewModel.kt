package com.campusbussbuddy.viewmodel.driver

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.campusbussbuddy.domain.usecase.GenerateQrCodeUseCase
import com.campusbussbuddy.domain.usecase.StartTripUseCase
import com.campusbussbuddy.domain.usecase.StopTripUseCase
import com.campusbussbuddy.utils.QrGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DriverViewModel @Inject constructor(
    private val generateQrCodeUseCase: GenerateQrCodeUseCase,
    private val startTripUseCase: StartTripUseCase,
    private val stopTripUseCase: StopTripUseCase,
    private val qrGenerator: QrGenerator
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(DriverUiState())
    val uiState: StateFlow<DriverUiState> = _uiState.asStateFlow()
    
    private val _busId = MutableStateFlow("bus_1") // Default bus
    
    fun startTrip(busId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            _busId.value = busId
            
            startTripUseCase(busId)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isTripActive = true,
                        currentBusId = busId
                    )
                    generateQrCode(busId)
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Failed to start trip: ${exception.message}"
                    )
                }
        }
    }
    
    fun stopTrip() {
        viewModelScope.launch {
            val busId = _busId.value
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            stopTripUseCase(busId)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isTripActive = false,
                        currentBusId = null,
                        qrCodeBitmap = null
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Failed to stop trip: ${exception.message}"
                    )
                }
        }
    }
    
    private fun generateQrCode(busId: String) {
        viewModelScope.launch {
            qrGenerator.generateQrCodeBitmap(busId)
                .onSuccess { bitmap ->
                    _uiState.value = _uiState.value.copy(qrCodeBitmap = bitmap)
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        error = "Failed to generate QR code: ${exception.message}"
                    )
                }
        }
    }
    
    fun refreshQrCode() {
        val busId = _busId.value
        if (_uiState.value.isTripActive) {
            generateQrCode(busId)
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class DriverUiState(
    val isLoading: Boolean = false,
    val isTripActive: Boolean = false,
    val currentBusId: String? = null,
    val qrCodeBitmap: Bitmap? = null,
    val error: String? = null
)