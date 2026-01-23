package com.campusbussbuddy.data.repository

import com.campusbussbuddy.domain.model.BoardingRecord
import com.campusbussbuddy.domain.model.QrCodeData
import com.campusbussbuddy.domain.model.QrScanResult
import com.campusbussbuddy.domain.usecase.QrCodeRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QrCodeRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : QrCodeRepository {

    override suspend fun generateQrCode(busId: String): Result<String> {
        return try {
            val qrData = QrCodeData(busId = busId)
            val json = JSONObject().apply {
                put("busId", qrData.busId)
                put("timestamp", qrData.timestamp)
            }
            Result.success(json.toString())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun scanQrCode(qrContent: String): QrScanResult {
        return try {
            val json = JSONObject(qrContent)
            val busId = json.getString("busId")
            val timestamp = json.optLong("timestamp", System.currentTimeMillis())
            
            val qrCodeData = QrCodeData(busId = busId, timestamp = timestamp)
            QrScanResult.Success(qrCodeData)
        } catch (e: JSONException) {
            QrScanResult.Error("Invalid QR Code format")
        } catch (e: Exception) {
            QrScanResult.Error("Failed to scan QR Code: ${e.message}")
        }
    }

    override suspend fun recordBoarding(boardingRecord: BoardingRecord): Result<Unit> {
        return try {
            val boardingData = mapOf(
                "studentId" to boardingRecord.studentId,
                "busId" to boardingRecord.busId,
                "timestamp" to boardingRecord.timestamp
            )
            
            firestore.collection("boarding")
                .document(boardingRecord.date)
                .collection(boardingRecord.busId)
                .document(boardingRecord.studentId)
                .set(boardingData)
                .await()
                
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}