package com.campusbussbuddy.viewmodel.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.campusbussbuddy.domain.model.BoardingRecord
import com.campusbussbuddy.domain.model.BusLocation
import com.campusbussbuddy.domain.model.QrScanResult
import com.campusbussbuddy.domain.usecase.GetBusLocationUseCase
import com.campusbussbuddy.domain.usecase.RecordBoardingUseCase
import com.campusbussbuddy.domain.usecase.ScanQrCodeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class StudentViewModel @Inject constructor(
    private val getBusLocationUseCase: GetBusLocationUseCase,
    private val scanQrCodeUseCase: ScanQrCodeUseCase,
    private val recordBoardingUseCase: RecordBoardingUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(StudentUiState())
    val uiState: StateFlow<StudentUiState> = _uiState.asStateFlow()
    
    private val _selectedBusId = MutableStateFlow("bus_1") // Default bus
    
    val busLocation: StateFlow<BusLocation?> = _selectedBusId
        .flatMapLatest { busId ->
            getBusLocationUseCase(busId)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
    
    fun scanQrCode(qrContent: String, studentId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isScanning = true, scanError = null)
            
            when (val result = scanQrCodeUseCase(qrContent)) {
                is QrScanResult.Success -> {
                    val boardingRecord = BoardingRecord(
                        studentId = studentId,
                        busId = result.qrCodeData.busId,
                        date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    )
                    
                    recordBoardingUseCase(boardingRecord)
                        .onSuccess {
                            _uiState.value = _uiState.value.copy(
                                isScanning = false,
                                boardingSuccess = true,
                                lastScannedBusId = result.qrCodeData.busId
                            )
                        }
                        .onFailure { exception ->
                            _uiState.value = _uiState.value.copy(
                                isScanning = false,
                                scanError = "Failed to record boarding: ${exception.message}"
                            )
                        }
                }
                is QrScanResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isScanning = false,
                        scanError = result.message
                    )
                }
            }
        }
    }
    
    fun selectBus(busId: String) {
        _selectedBusId.value = busId
    }
    
    fun clearScanResult() {
        _uiState.value = _uiState.value.copy(
            boardingSuccess = false,
            scanError = null,
            lastScannedBusId = null
        )
    }
}

data class StudentUiState(
    val isScanning: Boolean = false,
    val boardingSuccess: Boolean = false,
    val scanError: String? = null,
    val lastScannedBusId: String? = null
)