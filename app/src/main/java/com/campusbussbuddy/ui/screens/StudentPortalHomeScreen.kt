package com.campusbussbuddy.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.launch

import androidx.lifecycle.viewmodel.compose.viewModel
import com.campusbussbuddy.viewmodel.StudentPortalViewModel
import com.campusbussbuddy.firebase.DriverInfo

// ─── Student Portal Home Screen ───────────────────────────────────────────────
@Composable
fun StudentPortalHomeScreen(
    studentUid: String,
    onLogoutClick: () -> Unit = {},
    onAbsentCalendarClick: () -> Unit = {},
    viewModel: StudentPortalViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val studentInfo = uiState.studentInfo
    val busInfo = uiState.busInfo
    val activeDriver = uiState.activeDriver
    val isLoading = uiState.isLoading
    val upcomingAbsenceCount = uiState.upcomingAbsenceCount

    var hasScannedSession by remember { mutableStateOf(false) }

    val scope   = rememberCoroutineScope()
    val context = LocalContext.current

    // Camera permission launcher — opens scanner on grant
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.setScannerVisible(true)
        } else {
            Toast.makeText(context, "Camera permission needed to scan QR.", Toast.LENGTH_SHORT).show()
        }
    }

    // Helper: open QR scanner with permission gate
    fun openQrScanner() {
        if (hasScannedSession) {
            Toast.makeText(context, "Already scanned for this session.", Toast.LENGTH_SHORT).show()
            return
        }
        val granted = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        if (granted) viewModel.setScannerVisible(true)
        else cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    // Load student + bus data on launch
    LaunchedEffect(Unit) {
        viewModel.fetchPortalData()
    }

    NeumorphismScreenContainer {
        Box(modifier = Modifier.fillMaxSize()) {

            // ── Scrollable content ────────────────────────────────────────────
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
                        // Extra bottom padding so content doesn't hide behind fixed footer
                        .padding(bottom = 120.dp)
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(48.dp))

                    // Header bar
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        NeumorphismIconButton(
                            iconRes   = R.drawable.ic_chevron_left,
                            onClick   = onLogoutClick,
                            size      = 44.dp,
                            iconSize  = 24.dp,
                            modifier  = Modifier.align(Alignment.CenterStart)
                        )
                        AppLabelPill(
                            icon  = R.drawable.ic_person,
                            title = "Student Portal"
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Profile Avatar
                    Box(contentAlignment = Alignment.BottomEnd) {
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .neumorphicInset(cornerRadius = 55.dp, blur = 12.dp)
                                .background(NeumorphBgPrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter           = painterResource(id = R.drawable.ic_person),
                                contentDescription = "Profile",
                                tint              = NeumorphTextSecondary,
                                modifier          = Modifier.size(55.dp)
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
                        text          = studentInfo?.name ?: "Student",
                        fontSize      = 24.sp,
                        fontWeight    = FontWeight.ExtraBold,
                        color         = NeumorphTextPrimary,
                        letterSpacing = (-0.5).sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text       = if (busInfo != null) "Bus ${busInfo!!.busNumber}" else "Bus Not Assigned",
                        fontSize   = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color      = NeumorphTextSecondary
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text       = studentInfo?.stop ?: "Stop Not Set",
                        fontSize   = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color      = NeumorphTextSecondary
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // My Bus Status Card
                    StudentBusStatusCard(busInfo)

                    Spacer(modifier = Modifier.height(20.dp))

                    // Your Absences Summary Card
                    if (upcomingAbsenceCount > 0) {
                        StudentAbsenceSummaryCard(
                            count = upcomingAbsenceCount,
                            onClick = onAbsentCalendarClick
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    // Action Grid — ONLY cards trigger navigation
                    StudentActionGrid(
                        onQrScanClick  = { openQrScanner() },
                        onProfileClick = { viewModel.setProfileVisible(true) },
                        onAbsentClick  = onAbsentCalendarClick
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // ── Fixed Admin-style Footer ──────────────────────────────────────
            StudentFooterBar(
                studentInfo   = studentInfo,
                onLogoutClick = onLogoutClick,
                modifier      = Modifier.align(Alignment.BottomCenter)
            )

            // ── QR Scanner Overlay ────────────────────────────────────────────
            if (uiState.showScanner) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                        .zIndex(10f)
                ) {
                    QRScannerView(
                        onScan = { qrContent ->
                            viewModel.setScannerVisible(false)
                            val todayStr = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
                            if (qrContent.startsWith("busId:") && qrContent.contains("|date:$todayStr")) {
                                val parts = qrContent.split("|")
                                val scannedBusId = parts.firstOrNull { it.startsWith("busId:") }?.removePrefix("busId:")
                                
                                val uid = FirebaseAuth.getInstance().currentUser?.uid
                                if (uid != null && scannedBusId != null) {
                                    FirebaseFirestore.getInstance()
                                        .collection("students").document(uid)
                                        .update(mapOf("status" to "BOARDED", "busId" to scannedBusId))
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
                        onClose = { viewModel.setScannerVisible(false) }
                    )
                }
            }

            // ── Profile Dialog Overlay ────────────────────────────────────────
            if (uiState.showProfile) {
                StudentProfileDialog(
                    studentInfo = studentInfo,
                    busInfo     = busInfo,
                    activeDriver = activeDriver,
                    onDismiss   = { viewModel.setProfileVisible(false) }
                )
            }
        }
    }
}

// ─── My Bus Status Card ───────────────────────────────────────────────────────
@Composable
private fun StudentBusStatusCard(busInfo: BusInfo?) {
    NeumorphismCard(
        modifier       = Modifier.fillMaxWidth(),
        cornerRadius   = 24.dp,
        contentPadding = PaddingValues(20.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
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
                            painter           = painterResource(id = R.drawable.ic_directions_bus_vector),
                            contentDescription = "Bus",
                            tint              = NeumorphAccentPrimary,
                            modifier          = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text       = "My Bus Status",
                        fontSize   = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color      = NeumorphTextPrimary
                    )
                }
                // LIVE pill
                Box(
                    modifier = Modifier
                        .neumorphic(cornerRadius = 20.dp, elevation = 3.dp, blur = 6.dp)
                        .background(NeumorphSurface, RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text          = "LIVE",
                        fontSize      = 10.sp,
                        fontWeight    = FontWeight.Bold,
                        color         = NeumorphAccentPrimary,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text          = if (busInfo != null) "ROUTE ${busInfo.busNumber} - NORTH CAMPUS" else "ROUTE - NOT ASSIGNED",
                fontSize      = 10.sp,
                fontWeight    = FontWeight.Bold,
                color         = NeumorphTextSecondary,
                letterSpacing = 0.8.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text       = "4 mins",
                        fontSize   = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color      = NeumorphTextPrimary
                    )
                    Text(
                        text       = "Arriving at Drop Point",
                        fontSize   = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color      = NeumorphTextSecondary
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text          = "NEXT BUS",
                        fontSize      = 9.sp,
                        fontWeight    = FontWeight.Bold,
                        color         = NeumorphTextSecondary,
                        letterSpacing = 0.8.sp
                    )
                    Text(
                        text       = "16:45",
                        fontSize   = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color      = NeumorphTextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            NeumorphismButton(
                text     = "Track on Map",
                onClick  = { /* TODO: map navigation */ },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// ─── Upcoming Absences Summary Card ───────────────────────────────────────────
@Composable
private fun StudentAbsenceSummaryCard(count: Int, onClick: () -> Unit) {
    NeumorphismCard(
        modifier = Modifier
            .fillMaxWidth()
            .bounceClick { onClick() },
        cornerRadius = 24.dp,
        contentPadding = PaddingValues(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_calendar_today),
                        contentDescription = "Absences",
                        tint = Color(0xFFE53935),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Your Absences",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeumorphTextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$count upcoming absence(s) planned",
                    fontSize = 13.sp,
                    color = NeumorphTextSecondary
                )
            }
            Icon(
                painter = painterResource(id = R.drawable.ic_chevron_left), // chevron pointing right if rotated or use a proper icon. For now just standard chevron.
                contentDescription = "View",
                tint = NeumorphTextSecondary,
                modifier = Modifier.size(24.dp).background(Color.Transparent) // rotate in styling if needed 
            )
        }
    }
}

// ─── Action Grid — only cards trigger actions ─────────────────────────────────
@Composable
private fun StudentActionGrid(
    onQrScanClick: () -> Unit,
    onProfileClick: () -> Unit,
    onAbsentClick: () -> Unit
) {
    Column(
        modifier            = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            StudentActionCard(
                icon     = R.drawable.ic_qr_code,
                label    = "QR SCANNER",
                onClick  = onQrScanClick,
                modifier = Modifier.weight(1f)
            )
            StudentActionCard(
                icon     = R.drawable.ic_person,
                label    = "PROFILE",
                onClick  = onProfileClick,
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            StudentActionCard(
                icon     = R.drawable.ic_calendar_today,
                label    = "ABSENT CALENDAR",
                onClick  = onAbsentClick,
                modifier = Modifier.weight(1f)
            )
            StudentActionCard(
                icon     = R.drawable.ic_map,
                label    = "LIVE MAP",
                onClick  = { /* TODO */ },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

// ─── Individual Action Card ───────────────────────────────────────────────────
@Composable
private fun StudentActionCard(
    icon: Int,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    NeumorphismCard(
        modifier          = modifier
            .height(110.dp)
            .bounceClick { onClick() },
        cornerRadius      = 20.dp,
        contentPadding    = PaddingValues(0.dp),
        contentAlignment  = Alignment.Center
    ) {
        Column(
            modifier             = Modifier.fillMaxSize().padding(14.dp),
            horizontalAlignment  = Alignment.CenterHorizontally,
            verticalArrangement  = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .neumorphicInset(cornerRadius = 22.dp, blur = 8.dp)
                    .background(NeumorphBgPrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter           = painterResource(id = icon),
                    contentDescription = label,
                    tint              = NeumorphAccentPrimary,
                    modifier          = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text          = label,
                fontSize      = 10.sp,
                fontWeight    = FontWeight.Bold,
                color         = NeumorphTextPrimary,
                textAlign     = TextAlign.Center,
                letterSpacing = 0.4.sp,
                lineHeight    = 12.sp
            )
        }
    }
}

// ─── Fixed Admin-style Footer (Help + Logout) ─────────────────────────────────
@Composable
private fun StudentFooterBar(
    studentInfo: StudentInfo?,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(NeumorphBgPrimary)
            .padding(horizontal = 32.dp, vertical = 18.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .neumorphic(cornerRadius = 28.dp, elevation = 6.dp, blur = 12.dp)
                .background(NeumorphSurface, RoundedCornerShape(28.dp))
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            // Help
            StudentFooterItem(
                icon       = R.drawable.ic_help,
                label      = "Help",
                isSelected = false,
                onClick    = {
                    val name = studentInfo?.name ?: "Student"
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:pandiharshanofficial@gmail.com")
                        putExtra(Intent.EXTRA_SUBJECT, "Support Request")
                        putExtra(Intent.EXTRA_TEXT, "Student Name: $name")
                    }
                    if (intent.resolveActivity(context.packageManager) != null) {
                        context.startActivity(intent)
                    } else {
                        Toast.makeText(context, "No email app found.", Toast.LENGTH_SHORT).show()
                    }
                }
            )

            // Logout
            StudentFooterItem(
                icon       = R.drawable.ic_chevron_left,
                label      = "Logout",
                isSelected = false,
                onClick    = onLogoutClick
            )
        }
    }
}

// ─── Footer Nav Item ──────────────────────────────────────────────────────────
@Composable
private fun StudentFooterItem(
    icon: Int,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier            = Modifier.bounceClick { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .then(
                    if (isSelected)
                        Modifier.neumorphicInset(cornerRadius = 22.dp, elevation = 4.dp, blur = 8.dp)
                    else
                        Modifier.neumorphic(cornerRadius = 22.dp, elevation = 4.dp, blur = 8.dp)
                )
                .background(
                    if (isSelected) Color(0xFFDCDCDC) else NeumorphSurface,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter           = painterResource(id = icon),
                contentDescription = label,
                tint              = if (isSelected) NeumorphAccentPrimary else NeumorphTextSecondary,
                modifier          = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text       = label,
            fontSize   = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color      = if (isSelected) NeumorphAccentPrimary else NeumorphTextSecondary
        )
    }
}

// ─── Profile Dialog Overlay ───────────────────────────────────────────────────
@Composable
private fun StudentProfileDialog(
    studentInfo: StudentInfo?,
    busInfo: BusInfo?,
    activeDriver: DriverInfo?,
    onDismiss: () -> Unit
) {
    val context     = LocalContext.current
    val driverName  = activeDriver?.name?.takeIf { it.isNotBlank() } ?: "Driver not active"
    val driverPhone = activeDriver?.phone?.takeIf { it.isNotBlank() } ?: ""

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.55f))
            .zIndex(20f),
        contentAlignment = Alignment.Center
    ) {
        NeumorphismCard(
            modifier       = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            cornerRadius   = 28.dp,
            contentPadding = PaddingValues(24.dp)
        ) {
            Column(
                modifier            = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Header row
                Row(
                    modifier          = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text       = "My Profile",
                        fontSize   = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color      = NeumorphTextPrimary
                    )
                    NeumorphismIconButton(
                        iconRes  = R.drawable.ic_chevron_left,
                        onClick  = onDismiss,
                        size     = 36.dp,
                        iconSize = 18.dp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Avatar
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .neumorphicInset(cornerRadius = 40.dp, blur = 10.dp)
                        .background(NeumorphBgPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter           = painterResource(id = R.drawable.ic_person),
                        contentDescription = "Avatar",
                        tint              = NeumorphAccentPrimary,
                        modifier          = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Student details
                ProfileInfoRow(label = "Name",     value = studentInfo?.name ?: "—")
                ProfileInfoRow(label = "Stop",     value = studentInfo?.stop ?: "—")
                ProfileInfoRow(label = "Bus No.",  value = if (busInfo != null) "${busInfo.busNumber}" else "Not Assigned")
                ProfileInfoRow(label = "Driver",   value = driverName)

                Spacer(modifier = Modifier.height(8.dp))

                // Driver phone + call button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .neumorphicInset(cornerRadius = 16.dp, blur = 6.dp)
                        .background(NeumorphBgPrimary, RoundedCornerShape(16.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier          = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text          = "DRIVER PHONE",
                                fontSize      = 9.sp,
                                fontWeight    = FontWeight.Bold,
                                color         = NeumorphTextSecondary,
                                letterSpacing = 0.8.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text       = driverPhone.ifBlank { "Not Available" },
                                fontSize   = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color      = NeumorphTextPrimary
                            )
                        }

                        if (driverPhone.isNotBlank()) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .neumorphic(cornerRadius = 22.dp, elevation = 5.dp, blur = 10.dp)
                                    .background(NeumorphSurface, CircleShape)
                                    .bounceClick {
                                        val callIntent = Intent(Intent.ACTION_DIAL,
                                            Uri.parse("tel:$driverPhone"))
                                        context.startActivity(callIntent)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter           = painterResource(id = R.drawable.ic_call),
                                    contentDescription = "Call Driver",
                                    tint              = Color(0xFF4CAF50),
                                    modifier          = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                NeumorphismButton(
                    text     = "Close",
                    onClick  = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

// ─── Info Row helper ──────────────────────────────────────────────────────────
@Composable
private fun ProfileInfoRow(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier          = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text          = label.uppercase(),
                fontSize      = 9.sp,
                fontWeight    = FontWeight.Bold,
                color         = NeumorphTextSecondary,
                letterSpacing = 0.8.sp
            )
            Text(
                text       = value,
                fontSize   = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color      = NeumorphTextPrimary
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        HorizontalDivider(
            color     = NeumorphTextSecondary.copy(alpha = 0.15f),
            thickness = 0.8.dp
        )
        Spacer(modifier = Modifier.height(10.dp))
    }
}

// ─── QR Scanner View (unchanged) ─────────────────────────────────────────────
@androidx.annotation.OptIn(ExperimentalGetImage::class)
@Composable
fun QRScannerView(
    onScan: (String) -> Unit,
    onClose: () -> Unit
) {
    val context         = LocalContext.current
    val lifecycleOwner  = LocalLifecycleOwner.current
    var hasScanned      by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                val previewView          = PreviewView(ctx)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                val executor             = ContextCompat.getMainExecutor(ctx)

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
                                .addOnCompleteListener { imageProxy.close() }
                        } else {
                            imageProxy.close()
                        }
                    }
                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageAnalysis)
                    } catch (e: Exception) {
                        Log.e("QRScannerView", "Camera binding failed", e)
                    }
                }, executor)
                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        // Close button
        NeumorphismIconButton(
            iconRes  = R.drawable.ic_chevron_left,
            onClick  = onClose,
            size     = 48.dp,
            iconSize = 24.dp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 48.dp, start = 24.dp)
        )

        // Overlay guide frame
        Box(
            modifier = Modifier
                .size(250.dp)
                .align(Alignment.Center)
                .background(Color.Transparent)
        ) {
            Icon(
                painter           = painterResource(id = R.drawable.ic_qr_code),
                contentDescription = null,
                tint              = Color.White.copy(alpha = 0.3f),
                modifier          = Modifier.fillMaxSize().padding(16.dp)
            )
        }

        Text(
            text       = "SCAN BUS QR CODE",
            color      = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize   = 16.sp,
            modifier   = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 60.dp),
            letterSpacing = 2.sp
        )
    }
}
