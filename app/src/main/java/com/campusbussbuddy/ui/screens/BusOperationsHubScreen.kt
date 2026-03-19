package com.campusbussbuddy.ui.screens

import android.graphics.Bitmap
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.campusbussbuddy.R
import com.campusbussbuddy.firebase.FirebaseManager
import com.campusbussbuddy.ui.theme.*
import com.campusbussbuddy.ui.neumorphism.cards.NeumorphismCard
import com.campusbussbuddy.ui.neumorphism.cards.NeumorphismStatCard
import com.campusbussbuddy.ui.neumorphism.buttons.NeumorphismButton
import com.campusbussbuddy.ui.neumorphism.buttons.NeumorphismIconButton
import com.campusbussbuddy.ui.neumorphism.layout.AppLabelPill
import com.campusbussbuddy.ui.neumorphism.layout.NeumorphismScreenContainer
import com.campusbussbuddy.ui.neumorphism.layout.NeumorphismPill
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

import com.campusbussbuddy.viewmodel.StudentStatus
import com.campusbussbuddy.viewmodel.StudentMember
import com.campusbussbuddy.viewmodel.BusOperationsViewModel

@Composable
fun BusOperationsHubScreen(
    busNumber: String = "42-B",
    busId: String = "",
    onLogoutClick: () -> Unit = {},
    onStartTrip: () -> Unit = {}
) {
    // SECURITY: CRITICAL INITIALIZATION CHECK
    if (busId.isBlank()) {
        NeumorphismScreenContainer {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = NeumorphAccentPrimary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Loading Bus Operations...", color = NeumorphTextSecondary)
                }
            }
        }
        return
    }

    val factory = object : androidx.lifecycle.ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            return BusOperationsViewModel(busId) as T
        }
    }
    val viewModel: BusOperationsViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = factory)
    
    val uiState by viewModel.uiState.collectAsState()
    
    var currentTab by rememberSaveable { mutableStateOf("HOME") }

    val driverName = uiState.driverInfo?.name ?: "Loading..."
    val shiftInfo = uiState.driverInfo?.shift?.takeIf { it.isNotBlank() } ?: "Morning Shift"
    val routeInfo = uiState.driverInfo?.routeName?.takeIf { it.isNotBlank() } ?: "Route: Main Campus"
    val driverEmail = uiState.driverInfo?.email ?: ""
    val driverPhone = uiState.driverInfo?.phone ?: ""

    val studentsList = uiState.students
    
    val activeMembers = studentsList.filter { !it.isAbsent }
    val totalMembers = activeMembers.size
    val presentToday = activeMembers.count { it.status == StudentStatus.BOARDED }

    val endTripAction: () -> Unit = {
        uiState.driverInfo?.uid?.let { driverId ->
            viewModel.endTrip(driverId)
        }
    }
    
    val isEndingTrip = uiState.endTripStatus == "PROCESSING"

    LaunchedEffect(uiState.endTripStatus) {
        if (uiState.endTripStatus == "SUCCESS") {
            currentTab = "HOME"
        }
    }

    NeumorphismScreenContainer {
        Box(modifier = Modifier.fillMaxSize()) {
            
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(200)) },
                label = "hub_tab_transition",
                modifier = Modifier.fillMaxSize()
            ) { tab ->
                when (tab) {
                    "HOME" -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(modifier = Modifier.height(48.dp))

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
                                    icon = R.drawable.ic_directions_bus_vector,
                                    title = "Bus Operations"
                                )
                            }

                            Spacer(modifier = Modifier.height(32.dp))

                            BusOpsDriverProfile(
                                driverName = driverName,
                                busNumber = busNumber,
                                shiftInfo = shiftInfo
                            )

                            Spacer(modifier = Modifier.height(24.dp))
                            
                            BusOpsRouteInfo(routeInfo)

                            Spacer(modifier = Modifier.height(24.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                NeumorphismStatCard(
                                    count = presentToday.toString(),
                                    label = "Boarded",
                                    iconRes = R.drawable.ic_person,
                                    modifier = Modifier.weight(1f)
                                )

                                NeumorphismStatCard(
                                    count = totalMembers.toString(),
                                    label = "Total Members",
                                    iconRes = R.drawable.ic_group,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            TripAttendancePortalCard(busId = busId)

                            Spacer(modifier = Modifier.height(32.dp))

                            NeumorphismButton(
                                text = "End Trip",
                                onClick = endTripAction,
                                isLoading = isEndingTrip,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(120.dp))
                        }
                    }
                    "MEMBERS" -> {
                        MembersTabContent(
                            students = studentsList,
                            todayAbsences = studentsList.filter { it.isAbsent }.map { it.id },
                            totalStudents = totalMembers,
                            onBackClick = { currentTab = "HOME" }
                        )
                    }
                    "ROUTES" -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(modifier = Modifier.height(48.dp))

                            AppLabelPill(
                                icon = R.drawable.ic_route,
                                title = "Route Details"
                            )

                            Spacer(modifier = Modifier.height(32.dp))

                            NeumorphismCard(
                                modifier = Modifier.fillMaxWidth(),
                                cornerRadius = 24.dp,
                                contentPadding = PaddingValues(24.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .size(60.dp)
                                            .neumorphicInset(cornerRadius = 30.dp, blur = 8.dp)
                                            .background(NeumorphBgPrimary, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_directions_bus_vector),
                                            contentDescription = "Bus",
                                            tint = NeumorphAccentPrimary,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = routeInfo,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NeumorphTextPrimary,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Bus $busNumber ($shiftInfo)",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = NeumorphTextSecondary
                                    )
                                    Spacer(modifier = Modifier.height(24.dp))
                                    Text(
                                        text = "Additional stops and live traffic data will populate here seamlessly during dynamic route transitions.",
                                        fontSize = 13.sp,
                                        color = NeumorphTextSecondary,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 20.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(120.dp))
                        }
                    }
                    "SETTINGS" -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(modifier = Modifier.height(48.dp))

                            AppLabelPill(
                                icon = R.drawable.ic_settings,
                                title = "Driver Profile"
                            )

                            Spacer(modifier = Modifier.height(32.dp))

                            NeumorphismCard(
                                modifier = Modifier.fillMaxWidth(),
                                cornerRadius = 24.dp,
                                contentPadding = PaddingValues(24.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .size(80.dp)
                                            .neumorphicInset(cornerRadius = 40.dp, blur = 12.dp)
                                            .background(NeumorphBgPrimary, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_person),
                                            contentDescription = "Profile",
                                            tint = NeumorphAccentPrimary,
                                            modifier = Modifier.size(40.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(24.dp))
                                    Text(
                                        text = driverName,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NeumorphTextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = driverEmail,
                                        fontSize = 14.sp,
                                        color = NeumorphTextSecondary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = driverPhone,
                                        fontSize = 14.sp,
                                        color = NeumorphTextSecondary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            NeumorphismButton(
                                text = "Logout",
                                onClick = onLogoutClick,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(120.dp))
                        }
                    }
                }
            }

            // Bottom Navigation Bar matching Admin/Driver exactly
            HubBottomNavigationBar(
                currentTab = currentTab,
                onTabSelect = { currentTab = it },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

// ─── INTERNAL COMPOSABLES ───────────────────────────────────────────────────

@Composable
private fun TripAttendancePortalCard(busId: String) {
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }

    NeumorphismCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 24.dp,
        contentPadding = PaddingValues(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (qrBitmap == null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .neumorphicInset(cornerRadius = 30.dp, blur = 8.dp)
                            .background(NeumorphBgPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_qr_code),
                            contentDescription = "QR Code",
                            tint = NeumorphAccentPrimary,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Attendance Portal",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeumorphTextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Generate QR for students boarding at this stop.",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = NeumorphTextSecondary,
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                NeumorphismButton(
                    text = "Generate QR",
                    onClick = {
                        val todayStr = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
                        qrBitmap = generateQRCode(content = "busId:$busId|date:$todayStr")
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text(
                    text = "Student Scan Code",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeumorphTextPrimary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Bus ID: $busId",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeumorphAccentPrimary,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .size(220.dp)
                        .neumorphicInset(cornerRadius = 24.dp, elevation = 6.dp, blur = 12.dp)
                        .background(NeumorphBgPrimary, RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(180.dp)
                            .background(Color.White, RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            bitmap = qrBitmap!!.asImageBitmap(),
                            contentDescription = "Generated QR Code",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Students scan this code to mark attendance before boarding.",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = NeumorphTextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                NeumorphismPill(
                    label = "LIVE",
                    onClick = {}
                )
            }
        }
    }
}

private fun generateQRCode(content: String): Bitmap? {
    if (content.isBlank()) return null
    return try {
        val size = 512
        val bitMatrix = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, size, size)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(
                    x,
                    y,
                    if (bitMatrix.get(x, y)) android.graphics.Color.BLACK else android.graphics.Color.WHITE
                )
            }
        }
        bitmap
    } catch (e: Exception) {
        null
    }
}

@Composable
private fun MembersTabContent(
    students: List<StudentMember>,
    todayAbsences: List<String>,
    totalStudents: Int,
    onBackClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(48.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            NeumorphismIconButton(
                iconRes = R.drawable.ic_chevron_left,
                onClick = onBackClick,
                size = 44.dp,
                iconSize = 24.dp,
                modifier = Modifier.align(Alignment.CenterStart)
            )

            AppLabelPill(
                icon = R.drawable.ic_pin_drop,
                title = "Bus Stop Details"
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Members",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = NeumorphTextPrimary
            )

            NeumorphismPill(
                label = "$totalStudents EXPECTED",
                onClick = {}
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val waiting = students.filter { it.status == StudentStatus.WAITING_TO_BOARD && !todayAbsences.contains(it.id) }
            val boarded = students.filter { it.status == StudentStatus.BOARDED && !todayAbsences.contains(it.id) }
            val absencesList = students.filter { todayAbsences.contains(it.id) }

            if (waiting.isNotEmpty()) {
                item {
                    Text(
                        text = "Present (Waiting)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
                items(waiting) { student ->
                    BusStopStudentCard(student = student, onCallClick = {})
                }
            }

            if (boarded.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Boarded",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeumorphTextSecondary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                items(boarded) { student ->
                    BusStopStudentCard(student = student, onCallClick = {})
                }
            }
            
            if (absencesList.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Absent Today",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE53935),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                items(absencesList) { student ->
                    BusStopStudentCard(student = student, isAbsentView = true, onCallClick = {})
                }
            }
        }
    }
}

@Composable
private fun BusStopStudentCard(
    student: StudentMember,
    isAbsentView: Boolean = false,
    onCallClick: () -> Unit
) {
    NeumorphismCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 20.dp,
        contentPadding = PaddingValues(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .neumorphicInset(cornerRadius = 24.dp, blur = 8.dp)
                    .background(NeumorphBgPrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = student.initials,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeumorphTextPrimary
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = student.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeumorphTextPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                val statusText: String
                val statusColor: Color
                if (isAbsentView) {
                    statusText = "ABSENT"
                    statusColor = Color(0xFFE53935)
                } else {
                    statusText = when (student.status) {
                        StudentStatus.WAITING_TO_BOARD -> "EXPECTED"
                        StudentStatus.BOARDED -> "BOARDED"
                    }
                    statusColor = when (student.status) {
                        StudentStatus.WAITING_TO_BOARD -> Color(0xFF4CAF50)
                        StudentStatus.BOARDED -> NeumorphTextSecondary
                    }
                }

                Box(
                    modifier = Modifier
                        .background(statusColor.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = statusText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            NeumorphismIconButton(
                iconRes = R.drawable.ic_call,
                onClick = onCallClick,
                size = 40.dp,
                iconSize = 20.dp,
                unselectedTint = NeumorphTextPrimary
            )
        }
    }
}

@Composable
private fun HubBottomNavigationBar(
    currentTab: String,
    onTabSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        NeumorphismCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp),
            cornerRadius = 24.dp,
            contentPadding = PaddingValues(0.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                HubNavItem(
                    icon = R.drawable.ic_home,
                    label = "HOME",
                    isSelected = currentTab == "HOME",
                    onClick = { onTabSelect("HOME") }
                )
                HubNavItem(
                    icon = R.drawable.ic_group,
                    label = "MEMBERS",
                    isSelected = currentTab == "MEMBERS",
                    onClick = { onTabSelect("MEMBERS") }
                )
                HubNavItem(
                    icon = R.drawable.ic_pin_drop,
                    label = "ROUTES",
                    isSelected = currentTab == "ROUTES",
                    onClick = { onTabSelect("ROUTES") }
                )
                HubNavItem(
                    icon = R.drawable.ic_settings,
                    label = "SETTINGS",
                    isSelected = currentTab == "SETTINGS",
                    onClick = { onTabSelect("SETTINGS") }
                )
            }
        }
    }
}

@Composable
private fun HubNavItem(
    icon: Int,
    label: String,
    isSelected: Boolean = false,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .then(
                    if (isSelected) Modifier.neumorphicInset(cornerRadius = 18.dp, blur = 6.dp).background(NeumorphBgPrimary, CircleShape)
                    else Modifier.background(Color.Transparent, CircleShape)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = label,
                tint = if (isSelected) NeumorphAccentPrimary else NeumorphTextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) NeumorphAccentPrimary else NeumorphTextSecondary,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
private fun BusOpsDriverProfile(driverName: String, busNumber: String, shiftInfo: String) {
    NeumorphismCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 24.dp,
        contentPadding = PaddingValues(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .neumorphicInset(cornerRadius = 28.dp, blur = 8.dp)
                    .background(NeumorphBgPrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_person),
                    contentDescription = "Driver Profile",
                    tint = NeumorphAccentPrimary,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = driverName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeumorphTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Bus #$busNumber ($shiftInfo)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = NeumorphTextSecondary
                )
            }
        }
    }
}

@Composable
private fun BusOpsRouteInfo(routeInfo: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .neumorphic(cornerRadius = 14.dp, elevation = 3.dp, blur = 6.dp)
                .background(NeumorphSurface, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_pin_drop),
                contentDescription = "Route",
                tint = NeumorphTextSecondary,
                modifier = Modifier.size(14.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = routeInfo,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = NeumorphTextSecondary,
            textAlign = TextAlign.Center,
            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
        )
    }
}