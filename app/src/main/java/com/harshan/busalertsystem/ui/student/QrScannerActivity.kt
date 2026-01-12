package com.harshan.busalertsystem.ui.student

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.common.util.concurrent.ListenableFuture
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.harshan.busalertsystem.R
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/*
 * ADD DEPENDENCIES to app/build.gradle.kts:
 * // CameraX core library
 * implementation "androidx.camera:camera-core:1.3.3"
 * // CameraX Camera2 extensions
 * implementation "androidx.camera:camera-camera2:1.3.3"
 * // CameraX Lifecycle library
 * implementation "androidx.camera:camera-lifecycle:1.3.3"
 * // CameraX View class
 * implementation "androidx.camera:camera-view:1.3.3"
 * // ML Kit Barcode Scanning
 * implementation 'com.google.mlkit:barcode-scanning:17.2.0'
 *
 * ADD PERMISSION to AndroidManifest.xml if not already present:
 * <uses-permission android:name="android.permission.CAMERA" />
 */

class QrScannerActivity : AppCompatActivity() {

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var barcodeScanner: BarcodeScanner
    private lateinit var firestore: FirebaseFirestore

    private var isScanProcessing = false

    companion object {
        private const val TAG = "QrScannerActivity"
    }

    private val cameraPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startCamera()
        } else {
            Toast.makeText(this, "Camera permission is required to scan QR codes.", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_scanner)

        cameraExecutor = Executors.newSingleThreadExecutor()
        firestore = FirebaseFirestore.getInstance()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            cameraPermissionRequest.launch(Manifest.permission.CAMERA)
        }
    }

    private fun startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
        barcodeScanner = BarcodeScanning.getClient(options)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            bindCameraUseCases(cameraProvider)
        }, ContextCompat.getMainExecutor(this))
    }

    private fun bindCameraUseCases(cameraProvider: ProcessCameraProvider) {
        val previewView = findViewById<PreviewView>(R.id.cameraPreviewView)

        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
            if (isScanProcessing) {
                imageProxy.close()
                return@setAnalyzer
            }

            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                barcodeScanner.process(image)
                    .addOnSuccessListener { barcodes ->
                        if (barcodes.isNotEmpty()) {
                            isScanProcessing = true // Prevent multiple scans from one QR
                            processBarcode(barcodes.first())
                        }
                    }
                    .addOnCompleteListener {
                        imageProxy.close() // Always close the proxy
                    }
            }
        }

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)
        } catch (exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
        }
    }

    private fun processBarcode(barcode: Barcode) {
        val rawValue = barcode.rawValue
        Log.d(TAG, "QR Code detected: $rawValue")

        try {
            val json = JSONObject(rawValue)
            val busId = json.getString("busId")
            uploadBoardingInfo(busId)
        } catch (e: JSONException) {
            Log.e(TAG, "Failed to parse QR code JSON", e)
            Toast.makeText(this, "Invalid QR Code format", Toast.LENGTH_SHORT).show()
            isScanProcessing = false // Allow scanning again if format is invalid
        }
    }

    private fun uploadBoardingInfo(busId: String) {
        val studentId = FirebaseAuth.getInstance().currentUser?.uid ?: "anonymous_student"
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val boardingData = hashMapOf(
            "studentId" to studentId,
            "busId" to busId,
            "timestamp" to System.currentTimeMillis()
        )

        firestore.collection("boarding").document(date)
            .collection(busId).document(studentId)
            .set(boardingData)
            .addOnSuccessListener {
                Log.d(TAG, "Boarding successful for student $studentId on bus $busId")
                Toast.makeText(this, "Boarding Successful!", Toast.LENGTH_LONG).show()
                finish()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error recording boarding", e)
                Toast.makeText(this, "Boarding failed. Please try again.", Toast.LENGTH_LONG).show()
                finish()
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
        barcodeScanner.close()
    }
}
