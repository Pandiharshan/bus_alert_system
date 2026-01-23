package com.campusbussbuddy.domain.usecase

import com.campusbussbuddy.domain.model.BoardingRecord
import com.campusbussbuddy.domain.model.QrCodeData
import com.campusbussbuddy.domain.model.QrScanResult

interface QrCodeRepository {
    suspend fun generateQrCode(busId: String): Result<String>
    suspend fun scanQrCode(qrContent: String): QrScanResult
    suspend fun recordBoarding(boardingRecord: BoardingRecord): Result<Unit>
}

class GenerateQrCodeUseCase(private val repository: QrCodeRepository) {
    suspend operator fun invoke(busId: String): Result<String> {
        return repository.generateQrCode(busId)
    }
}

class ScanQrCodeUseCase(private val repository: QrCodeRepository) {
    suspend operator fun invoke(qrContent: String): QrScanResult {
        return repository.scanQrCode(qrContent)
    }
}

class RecordBoardingUseCase(private val repository: QrCodeRepository) {
    suspend operator fun invoke(boardingRecord: BoardingRecord): Result<Unit> {
        return repository.recordBoarding(boardingRecord)
    }
}