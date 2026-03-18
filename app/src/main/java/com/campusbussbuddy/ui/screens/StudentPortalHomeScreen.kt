package com.campusbussbuddy.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import com.campusbussbuddy.R
import com.campusbussbuddy.firebase.BusInfo
import com.campusbussbuddy.firebase.StudentInfo
import com.campusbussbuddy.firebase.FirebaseManager
import com.campusbussbuddy.ui.theme.*
import com.campusbussbuddy.ui.neumorphism.cards.NeumorphismCard
import com.campusbussbuddy.ui.neumorphism.buttons.NeumorphismButton
import com.campusbussbuddy.ui.neumorphism.buttons.NeumorphismIconButton
import com.campusbussbuddy.ui.neumorphism.layout.AppLabelPill
import com.campusbussbuddy.ui.neumorphism.layout.NeumorphismScreenContainer
import com.campusbussbuddy.ui.neumorphism.layout.NeumorphismPill
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.launch

@Composable
fun StudentPortalHomeScreen(
    onLogoutClick: () -> Unit = {}
) {
    var studentInfo by remember { mutableStateOf<StudentInfo?>(null) }
    var busInfo by remember { mutableStateOf<BusInfo?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Scanner States
    var showScanner by remember { mutableStateOf(false) }
    var hasScannedSession by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            showScanner = true
        } else {
            Toast.makeText(context, "Camera permission needed to scan QR.", Toast.LENGTH_SHORT).show()
        }
    }

    // Load student data on screen launch
    LaunchedEffect(Unit) {
        scope.launch {
            studentInfo = FirebaseManager.getCurrentStudentInfo()
            if (studentInfo?.busId?.isNotEmpty() == true) {
                busInfo = FirebaseManager.getBusInfo(studentInfo!!.busId)
            }
            isLoading = false
        }
    }

    NeumorphismScreenContainer {
        Box(modifier = Modifier.fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = NeumorphAccentPrimary
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(48.dp))

                    // Top AppLabelPill with back button
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        NeumorphismIconButton(
                            iconRes = R.drawable.ic_chevron_left,
                            onClick = onLogoutClick,
                            size = 44.dp,
                            iconSize = 24.dp,
                            modifier = Modifier.align(Alignment.CenterStart)
                        )

                        AppLabelPill(
                            icon = R.drawable.ic_person,
                            title = "Student Portal"
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Profile Photo
                    Box(contentAlignment = Alignment.BottomEnd) {
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .neumorphicInset(cornerRadius = 55.dp, blur = 12.dp)
                                .background(NeumorphBgPrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_person),
                                contentDescription = "Profile",
                                tint = NeumorphTextSecondary,
                                modifier = Modifier.size(55.dp)
                            )
                        }

                        // Online indicator
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .neumorphic(cornerRadius = 11.dp, elevation = 3.dp, blur = 6.dp)
                                .background(Color(0xFF4CAF50), CircleShape)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Student Name
                    Text(
                        text = studentInfo?.name ?: "Student",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = NeumorphTextPrimary,
                        letterSpacing = (-0.5).sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Bus and Stop Info
                    Text(
                        text = if (busInfo != null)
                            "Bus ${busInfo!!.busNumber} • Morning Express"
                        else
                            "Bus Not Assigned",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = NeumorphTextSecondary
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = studentInfo?.stop ?: "Stop Not Set",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = NeumorphTextSecondary
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Scan QR Button
                    NeumorphismButton(
                        text = "Scan QR",
                        onClick = {
                            if (!hasScannedSession) {
                                val permissionCheckResult = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                                if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                                    showScanner = true
                                } else {
                                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                }
                            } else {
                                Toast.makeText(context, "Already scanned for this session.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // My Bus Status Card
                    StudentBusStatusCard(busInfo)

                    Spacer(modifier = Modifier.height(20.dp))

                    // Action Grid (2x2)
                    StudentActionGrid()

                    Spacer(modifier = Modifier.height(24.dp))

                    // Bottom Links
                    StudentBottomLinks(onLogoutClick = onLogoutClick)

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // QR Scanner Overlay
            if (showScanner) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                        .zIndex(10f)
                ) {
                    QRScannerView(
                        onScan = { qrContent ->
                            showScanner = false
                            if (qrContent.startsWith("busId:")) {
                                val scannedBusId = qrContent.removePrefix("busId:")
                                
                                // Get current user UID
                                val uid = FirebaseAuth.getInstance().currentUser?.uid
                                if (uid != null) {
                                    FirebaseFirestore.getInstance().collection("students").document(uid)
                                        .update(
                                            mapOf(
                                                "status" to "BOARDED",
                                                "busId" to scannedBusId
                                            )
                                        )
                                        .addOnSuccessListener {
                                            Toast.makeText(context, "Boarded Successfully", Toast.LENGTH_SHORT).show()
                                            hasScannedSession = true
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(context, "Failed to update attendance.", Toast.LENGTH_SHORT).show()
                                        }
                                } else {
                                    Toast.makeText(context, "Error: User not logged in", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(context, "Invalid QR Code", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onClose = {
                            showScanner = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun StudentBusStatusCard(busInfo: BusInfo?) {
    NeumorphismCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 24.dp,
        contentPadding = PaddingValues(20.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .neumorphicInset(cornerRadius = 18.dp, blur = 6.dp)
                            .background(NeumorphBgPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_directions_bus_vector),
                            contentDescription = "Bus",
                            tint = NeumorphAccentPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = "My Bus Status",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeumorphTextPrimary
                    )
                }

                NeumorphismPill(
                    label = "LIVE",
                    onClick = {}
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (busInfo != null) "ROUTE ${busInfo.busNumber} - NORTH CAMPUS" else "ROUTE - NOT ASSIGNED",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = NeumorphTextSecondary,
                letterSpacing = 0.8.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "4 mins",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = NeumorphTextPrimary
                    )
                    Text(
                        text = "Arriving at Drop Point",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = NeumorphTextSecondary
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "NEXT BUS",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeumorphTextSecondary,
                        letterSpacing = 0.8.sp
                    )
                    Text(
                        text = "16:45",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeumorphTextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            NeumorphismButton(
                text = "Track on Map",
                onClick = { /* Handle track on map */ },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun StudentActionGrid() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            StudentActionCard(
                icon = R.drawable.ic_qr_code,
                label = "QR SCANNER",
                modifier = Modifier.weight(1f)
            )

            StudentActionCard(
                icon = R.drawable.ic_person,
                label = "PROFILE",
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            StudentActionCard(
                icon = R.drawable.ic_calendar_today,
                label = "ABSENT CALENDAR",
                modifier = Modifier.weight(1f)
            )

            StudentActionCard(
                icon = R.drawable.ic_map,
                label = "LIVE MAP",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StudentActionCard(
    icon: Int,
    label: String,
    modifier: Modifier = Modifier
) {
    NeumorphismCard(
        modifier = modifier
            .height(110.dp)
            .bounceClick { /* Handle Action */ },
        cornerRadius = 20.dp,
        contentPadding = PaddingValues(0.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .neumorphicInset(cornerRadius = 22.dp, blur = 8.dp)
                    .background(NeumorphBgPrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = label,
                    tint = NeumorphAccentPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = NeumorphTextPrimary,
                textAlign = TextAlign.Center,
                letterSpacing = 0.4.sp,
                lineHeight = 12.sp
            )
        }
    }
}

@Composable
private fun StudentBottomLinks(onLogoutClick: () -> Unit = {}) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        NeumorphismPill(label = "HELP", onClick = {})
        NeumorphismPill(label = "SETTINGS", onClick = {})
        NeumorphismPill(label = "LOGOUT", onClick = onLogoutClick)
    }
}

/**
 * Reusable QR Scanner View utilizing CameraX and Google ML Kit.
 */
@androidx.annotation.OptIn(ExperimentalGetImage::class)
@Composable
fun QRScannerView(
    onScan: (String) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var hasScanned by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                val executor = ContextCompat.getMainExecutor(ctx)

                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    val imageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()

                    val scanner = BarcodeScanning.getClient(
                        BarcodeScannerOptions.Builder()
                            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                            .build()
                    )

                    imageAnalysis.setAnalyzer(executor) { imageProxy ->
                        val mediaImage = imageProxy.image
                        if (mediaImage != null && !hasScanned) {
                            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                            scanner.process(image)
                                .addOnSuccessListener { barcodes ->
                                    for (barcode in barcodes) {
                                        barcode.rawValue?.let { value ->
                                            if (!hasScanned) {
                                                hasScanned = true
                                                onScan(value)
                                            }
                                        }
                                    }
                                }
                                .addOnCompleteListener {
                                    imageProxy.close()
                                }
                        } else {
                            imageProxy.close()
                        }
                    }

                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner, cameraSelector, preview, imageAnalysis
                        )
                    } catch (e: Exception) {
                        Log.e("QRScannerView", "Camera binding failed", e)
                    }
                }, executor)
                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        // Close Button
        NeumorphismIconButton(
            iconRes = R.drawable.ic_chevron_left,
            onClick = onClose,
            size = 48.dp,
            iconSize = 24.dp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 48.dp, start = 24.dp)
        )

        // Overlay Guidance
        Box(
            modifier = Modifier
                .size(250.dp)
                .align(Alignment.Center)
                .background(Color.Transparent)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_qr_code),
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.3f),
                modifier = Modifier.fillMaxSize().padding(16.dp)
            )
        }
        
        Text(
            text = "SCAN BUS QR CODE",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 60.dp),
            letterSpacing = 2.sp
        )
    }
}
