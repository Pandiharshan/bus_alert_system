package com.harshan.busalertsystem.utils

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import org.json.JSONException
import org.json.JSONObject

/*
 * ADD DEPENDENCY to app/build.gradle.kts:
 * implementation 'com.google.zxing:core:3.5.1'
 */
object QrGenerator {

    /**
     * Generates a QR code bitmap containing a JSON object with the bus ID.
     * As per the requirement, the JSON content will be: `{"busId": "bus_1"}`
     *
     * @return A Bitmap object representing the QR code, or null if generation fails.
     */
    fun generateQrCode(): Bitmap? {
        val busId = "bus_1"
        val width = 512
        val height = 512

        val json = JSONObject()
        try {
            json.put("busId", busId)
        } catch (e: JSONException) {
            e.printStackTrace()
            return null
        }
        val content = json.toString()

        val writer = MultiFormatWriter()
        return try {
            val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, width, height)
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
            bmp
        } catch (e: WriterException) {
            e.printStackTrace()
            null
        }
    }
}
