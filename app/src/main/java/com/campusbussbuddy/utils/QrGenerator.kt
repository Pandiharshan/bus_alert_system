package com.campusbussbuddy.utils

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QrGenerator @Inject constructor() {
    
    fun generateQrCodeBitmap(
        busId: String,
        width: Int = 512,
        height: Int = 512
    ): Result<Bitmap> {
        return try {
            val qrContent = createQrContent(busId)
            val writer = MultiFormatWriter()
            val bitMatrix = writer.encode(qrContent, BarcodeFormat.QR_CODE, width, height)
            
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
            
            Result.success(bitmap)
        } catch (e: WriterException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun generateQrCodeString(busId: String): Result<String> {
        return try {
            val qrContent = createQrContent(busId)
            Result.success(qrContent)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun createQrContent(busId: String): String {
        val json = JSONObject().apply {
            put("busId", busId)
            put("timestamp", System.currentTimeMillis())
        }
        return json.toString()
    }
}