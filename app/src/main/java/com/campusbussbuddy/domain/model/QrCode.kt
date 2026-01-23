package com.campusbussbuddy.domain.model

data class QrCodeData(
    val busId: String,
    val timestamp: Long = System.currentTimeMillis()
)

sealed class QrScanResult {
    data class Success(val qrCodeData: QrCodeData) : QrScanResult()
    data class Error(val message: String) : QrScanResult()
}