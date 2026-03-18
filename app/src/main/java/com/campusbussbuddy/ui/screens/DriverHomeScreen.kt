package com.campusbussbuddy.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.campusbussbuddy.R
import com.campusbussbuddy.firebase.BusInfo
import com.campusbussbuddy.firebase.DriverInfo
import com.campusbussbuddy.ui.neumorphism.cards.NeumorphismCard
import com.campusbussbuddy.ui.neumorphism.buttons.NeumorphismButton
import com.campusbussbuddy.ui.neumorphism.layout.AppLabelPill
import com.campusbussbuddy.ui.neumorphism.layout.NeumorphismPill
import com.campusbussbuddy.ui.neumorphism.layout.NeumorphismScreenContainer
import com.campusbussbuddy.ui.theme.*
import com.campusbussbuddy.ui.viewmodel.DriverViewModel
import com.campusbussbuddy.ui.viewmodel.DriverPortalUiState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.FieldValue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.campusbussbuddy.ui.neumorphism.buttons.NeumorphismIconButton
import com.campusbussbuddy.ui.neumorphism.inputs.NeumorphismTextField
import com.campusbussbuddy.ui.viewmodel.SettingsViewModel

data class DriverLog(
    val id: String,
    val busId: String,
    val timestampMs: Long
)

@Composable
fun DriverHomeScreen(
    onBusLoginClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    driverViewModel: DriverViewModel = viewModel()
) {
    val uiState by driverViewModel.uiState.collectAsState()
    var currentTab by remember { mutableStateOf("HOME") }
    val context = LocalContext.current
    val firestore = FirebaseFirestore.getInstance()
    var isLoggingIn by remember { mutableStateOf(false) }

    // History state
    var historyLogs by remember { mutableStateOf<List<DriverLog>>(emptyList()) }
    var historyLoading by remember { mutableStateOf(true) }

    // Fetch history
    DisposableEffect(uiState.driverInfo?.uid) {
        val driverId = uiState.driverInfo?.uid
        if (driverId == null) {
            historyLoading = false
            return@DisposableEffect onDispose {}
        }
        val listener = firestore.collection("driver_logs")
            .whereEqualTo("driverId", driverId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(5)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    historyLoading = false
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    historyLogs = snapshot.documents.mapNotNull { doc ->
                        val busId = doc.getString("busId") ?: "Unknown"
                        val timestamp = doc.getTimestamp("timestamp")?.toDate()?.time ?: System.currentTimeMillis()
                        DriverLog(doc.id, busId, timestamp)
                    }
                    historyLoading = false
                }
            }
        onDispose { listener.remove() }
    }

    NeumorphismScreenContainer {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = NeumorphAccentPrimary
                )
            }
            uiState.errorMessage != null -> {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .neumorphicInset(cornerRadius = 40.dp, blur = 12.dp)
                            .background(NeumorphBgPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_notifications),
                            contentDescription = "Error",
                            tint = NeumorphTextSecondary,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = uiState.errorMessage ?: "Something went wrong",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = NeumorphTextPrimary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    NeumorphismButton(
                        text = "Retry",
                        onClick = { driverViewModel.refresh() },
                        modifier = Modifier.fillMaxWidth(0.6f)
                    )
                }
            }
            else -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(bottom = if (currentTab != "HOME") 100.dp else 0.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(48.dp))

                        // Crossfade entire tab content
                        AnimatedContent(
                            targetState = currentTab,
                            transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(200)) },
                            label = "tab_transition"
                        ) { tab ->
                            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                                when (tab) {
                                    "HOME" -> {
                                        HomeTabContent(
                                            uiState = uiState,
                                            isLoggingIn = isLoggingIn,
                                            onBusLoginClick = {
                                                val driverId = uiState.driverInfo?.uid
                                                val busId = uiState.busInfo?.busId ?: uiState.driverInfo?.assignedBusId
                                                if (driverId != null && busId != null) {
                                                    isLoggingIn = true
                                                    val logData = hashMapOf(
                                                        "driverId" to driverId,
                                                        "busId" to busId,
                                                        "timestamp" to FieldValue.serverTimestamp()
                                                    )
                                                    firestore.collection("driver_logs").add(logData)
                                                        .addOnSuccessListener {
                                                            isLoggingIn = false
                                                            Toast.makeText(context, "Bus Login Recorded successfully", Toast.LENGTH_SHORT).show()
                                                            onBusLoginClick()
                                                        }
                                                        .addOnFailureListener {
                                                            isLoggingIn = false
                                                            Toast.makeText(context, "Failed to log in", Toast.LENGTH_SHORT).show()
                                                            onBusLoginClick()
                                                        }
                                                } else {
                                                    onBusLoginClick()
                                                }
                                            }
                                        )
                                    }
                                    "HISTORY" -> HistoryTabContent(historyLogs, historyLoading)
                                    "ROUTES" -> PlaceholderTab("Routes")
                                    "SETTINGS" -> SettingsTabContent(onHomeClick = { currentTab = "HOME" })
                                }
                            }
                        }
                    }

                    // Bottom Navigation Bar matching Admin
                    DriverBottomNavigationBar(
                        currentTab = currentTab,
                        onTabSelect = { currentTab = it },
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }
            }
        }
    }
}

// ─── HOME TAB CONTENT ────────────────────────────────────────────────────────
@Composable
private fun HomeTabContent(
    uiState: DriverPortalUiState,
    isLoggingIn: Boolean,
    onBusLoginClick: () -> Unit
) {
    AppLabelPill(
        icon = R.drawable.ic_directions_bus_vector,
        title = "Campus Transport System"
    )

    Spacer(modifier = Modifier.height(48.dp))

    DriverProfilePhoto(uiState.driverInfo)

    Spacer(modifier = Modifier.height(24.dp))

    Text(
        text = "Driver Portal",
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        color = NeumorphTextPrimary
    )

    Spacer(modifier = Modifier.height(8.dp))

    val shiftText = if (uiState.driverInfo?.shift?.isNotEmpty() == true) {
        uiState.driverInfo.shift.uppercase()
    } else {
        "SHIFT ACTIVE"
    }

    NeumorphismPill(
        label = shiftText,
        onClick = {},
        modifier = Modifier.padding(vertical = 4.dp)
    )

    Spacer(modifier = Modifier.height(32.dp))

    ImprovedAssignedBusCard(
        driverInfo = uiState.driverInfo,
        busInfo = uiState.busInfo
    )

    Spacer(modifier = Modifier.height(32.dp))

    Box(modifier = Modifier.padding(horizontal = 24.dp)) {
        NeumorphismButton(
            text = "Bus Login",
            isLoading = isLoggingIn,
            onClick = onBusLoginClick,
            modifier = Modifier.fillMaxWidth()
        )
    }

    Spacer(modifier = Modifier.height(140.dp))
}

// ─── ASSIGNED BUS CARD ────────────────────────────────────────────────────────
@Composable
private fun ImprovedAssignedBusCard(driverInfo: DriverInfo?, busInfo: BusInfo?) {
    val isActive = driverInfo?.assignedBusId?.isNotEmpty() == true
    val busLabel = when {
        busInfo != null -> "Route ${busInfo.busNumber}"
        isActive -> "Bus Assigned"
        else -> "No Bus Assigned"
    }
    val routeName = when {
        driverInfo?.routeName?.isNotEmpty() == true -> driverInfo.routeName
        else -> "North Campus Terminal"
    }

    val statusText = if (isActive) "ACTIVE" else "UNASSIGNED"
    val statusColor = if (isActive) Color(0xFF4CAF50) else NeumorphTextSecondary

    NeumorphismCard(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
        cornerRadius = 24.dp,
        contentPadding = PaddingValues(24.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .neumorphicInset(cornerRadius = 12.dp, blur = 4.dp)
                        .background(NeumorphBgPrimary, RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Box(modifier = Modifier.size(8.dp).background(statusColor, CircleShape))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(statusText, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = statusColor)
                }

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .neumorphic(cornerRadius = 24.dp, elevation = 4.dp, blur = 8.dp)
                        .background(NeumorphBgPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_directions_bus_vector),
                        contentDescription = "Bus",
                        tint = NeumorphAccentPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "ASSIGNED BUS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = NeumorphTextSecondary,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = busLabel,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = NeumorphTextPrimary
            )

            Spacer(modifier = Modifier.height(24.dp))

            InfoRow(iconRes = R.drawable.ic_pin_drop, label = "NEXT STOP", value = routeName)
            Spacer(modifier = Modifier.height(16.dp))
            InfoRow(iconRes = R.drawable.ic_history, label = "DEPARTURE", value = "14:30 PM (In 12 mins)")
        }
    }
}

// ─── HISTORY TAB CONTENT ─────────────────────────────────────────────────────
@Composable
private fun HistoryTabContent(logs: List<DriverLog>, isLoading: Boolean) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
        Text("Login History", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = NeumorphTextPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Last 5 bus logins", fontSize = 14.sp, color = NeumorphTextSecondary)
        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally), color = NeumorphAccentPrimary)
        } else if (logs.isEmpty()) {
            Text("No login history found", color = NeumorphTextSecondary, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        } else {
            logs.forEach { log ->
                val timeString = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault()).format(Date(log.timestampMs))
                
                NeumorphismCard(
                    modifier = Modifier.fillMaxWidth().bounceClick { },
                    cornerRadius = 16.dp,
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .neumorphicInset(cornerRadius = 20.dp, blur = 6.dp)
                                .background(NeumorphBgPrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(painterResource(R.drawable.ic_history), "History", modifier = Modifier.size(20.dp), tint = NeumorphAccentPrimary)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Bus #${log.busId}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NeumorphTextPrimary)
                            Text(timeString, fontSize = 12.sp, color = NeumorphTextSecondary)
                        }
                        Text("Logged", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

// ─── PLACEHOLDER TAB ─────────────────────────────────────────────────────────
@Composable
private fun PlaceholderTab(title: String) {
    Box(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .neumorphicInset(cornerRadius = 40.dp, blur = 12.dp)
                    .background(NeumorphBgPrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(painterResource(R.drawable.ic_route), contentDescription = null, modifier = Modifier.size(36.dp), tint = NeumorphTextSecondary)
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(title, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = NeumorphTextPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            Text("This section is under development", fontSize = 14.sp, color = NeumorphTextSecondary)
        }
    }
}

// ─── HELPER COMPONENTS ────────────────────────────────────────────────────────
@Composable
private fun DriverProfilePhoto(driverInfo: DriverInfo?) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .size(140.dp)
            .neumorphicInset(cornerRadius = 70.dp, blur = 16.dp)
            .background(NeumorphBgPrimary, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        val photoUrl = driverInfo?.photoUrl?.trim() ?: ""
        if (photoUrl.isNotEmpty()) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(context).data(photoUrl).crossfade(true).build(),
                contentDescription = "Driver Profile Photo",
                modifier = Modifier.fillMaxSize().padding(8.dp).clip(CircleShape),
                contentScale = ContentScale.Crop,
                loading = {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.size(30.dp), color = NeumorphAccentPrimary, strokeWidth = 2.dp)
                    }
                },
                error = {
                    Icon(painterResource(id = R.drawable.ic_person), "Default", modifier = Modifier.size(70.dp), tint = NeumorphTextSecondary)
                }
            )
        } else {
            Icon(painterResource(id = R.drawable.ic_person), "Default Profile", modifier = Modifier.size(70.dp), tint = NeumorphTextSecondary)
        }
    }
}

@Composable
private fun InfoRow(iconRes: Int, label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .neumorphic(cornerRadius = 20.dp, elevation = 4.dp, blur = 8.dp)
                .background(NeumorphBgPrimary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(painterResource(id = iconRes), contentDescription = label, tint = NeumorphTextPrimary, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(label, color = NeumorphTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
            Text(value, color = NeumorphTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

// ─── BOTTOM NAVIGATION BAR ────────────────────────────────────────────────────
@Composable
private fun DriverBottomNavigationBar(
    currentTab: String,
    onTabSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            DriverNavItem(
                icon = R.drawable.ic_home,
                label = "HOME",
                isSelected = currentTab == "HOME",
                onClick = { onTabSelect("HOME") }
            )
            DriverNavItem(
                icon = R.drawable.ic_history,
                label = "HISTORY",
                isSelected = currentTab == "HISTORY",
                onClick = { onTabSelect("HISTORY") }
            )
            DriverNavItem(
                icon = R.drawable.ic_pin_drop,
                label = "ROUTES",
                isSelected = currentTab == "ROUTES",
                onClick = { onTabSelect("ROUTES") }
            )
            DriverNavItem(
                icon = R.drawable.ic_settings,
                label = "SETTINGS",
                isSelected = currentTab == "SETTINGS",
                onClick = { onTabSelect("SETTINGS") }
            )
        }
    }
}

// ─── SETTINGS TAB CONTENT ────────────────────────────────────────────────────────
@Composable
private fun SettingsTabContent(
    onHomeClick: () -> Unit,
    settingsViewModel: SettingsViewModel = viewModel()
) {
    val uiState by settingsViewModel.uiState.collectAsState()

    when {
        uiState.isLoading -> {
            Box(modifier = Modifier.fillMaxWidth().height(300.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = NeumorphAccentPrimary)
            }
        }
        else -> {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // TopBar with Back Button (redirects to Home tab)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    NeumorphismIconButton(
                        iconRes = R.drawable.ic_chevron_left,
                        onClick = onHomeClick,
                        size = 44.dp,
                        iconSize = 24.dp,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                    
                    AppLabelPill(
                        icon = R.drawable.ic_settings,
                        title = "Settings"
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(32.dp))

                    // Success / Error Messages
                    if (uiState.successMessage != null) {
                        NeumorphismCard(
                            modifier = Modifier.fillMaxWidth(),
                            cornerRadius = 16.dp,
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_check_circle),
                                    contentDescription = "Success",
                                    tint = Color(0xFF4CAF50),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = uiState.successMessage!!,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = NeumorphTextPrimary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    if (uiState.errorMessage != null) {
                        NeumorphismCard(
                            modifier = Modifier.fillMaxWidth(),
                            cornerRadius = 16.dp,
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_notifications),
                                    contentDescription = "Error",
                                    tint = Color(0xFFE53935),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = uiState.errorMessage!!,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFFE53935)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Profile Information Section
                    Text(
                        text = "Profile Information",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeumorphTextPrimary,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    NeumorphismCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 24.dp,
                        contentPadding = PaddingValues(24.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "DRIVER NAME",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeumorphTextSecondary,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            NeumorphismTextField(
                                value = uiState.name,
                                onValueChange = { settingsViewModel.onNameChange(it) },
                                placeholder = "Enter your name",
                                leadingIcon = {
                                    Icon(painterResource(R.drawable.ic_person), null, tint = NeumorphTextSecondary, modifier = Modifier.size(20.dp))
                                }
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                text = "PHONE NUMBER",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeumorphTextSecondary,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            NeumorphismTextField(
                                value = uiState.phone,
                                onValueChange = { settingsViewModel.onPhoneChange(it) },
                                placeholder = "Enter phone number",
                                leadingIcon = {
                                    Icon(painterResource(R.drawable.ic_call), null, tint = NeumorphTextSecondary, modifier = Modifier.size(20.dp))
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                            )

                            Spacer(modifier = Modifier.height(32.dp))

                            NeumorphismButton(
                                text = if (uiState.isUpdatingProfile) "Updating..." else "Update Profile",
                                onClick = { if (!uiState.isUpdatingProfile) settingsViewModel.updateProfile() },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Change Password Section
                    Text(
                        text = "Change Password",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeumorphTextPrimary,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    NeumorphismCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 24.dp,
                        contentPadding = PaddingValues(24.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "NEW PASSWORD",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeumorphTextSecondary,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            NeumorphismTextField(
                                value = uiState.newPassword,
                                onValueChange = { settingsViewModel.onNewPasswordChange(it) },
                                placeholder = "Enter new password",
                                leadingIcon = {
                                    Icon(painterResource(R.drawable.ic_visibility_off), null, tint = NeumorphTextSecondary, modifier = Modifier.size(20.dp))
                                },
                                visualTransformation = PasswordVisualTransformation()
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                text = "CONFIRM PASSWORD",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeumorphTextSecondary,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            NeumorphismTextField(
                                value = uiState.confirmPassword,
                                onValueChange = { settingsViewModel.onConfirmPasswordChange(it) },
                                placeholder = "Confirm new password",
                                leadingIcon = {
                                    Icon(painterResource(R.drawable.ic_visibility_off), null, tint = NeumorphTextSecondary, modifier = Modifier.size(20.dp))
                                },
                                visualTransformation = PasswordVisualTransformation()
                            )

                            Spacer(modifier = Modifier.height(32.dp))

                            NeumorphismButton(
                                text = if (uiState.isChangingPassword) "Changing..." else "Change Password",
                                onClick = { if (!uiState.isChangingPassword) settingsViewModel.changePassword() },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

// ─── NAV ITEM ─────────────────────────────────────────────────────────────────
@Composable
private fun DriverNavItem(icon: Int, label: String, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.bounceClick { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .then(
                    if (isSelected) Modifier.neumorphicInset(cornerRadius = 22.dp, elevation = 4.dp, blur = 8.dp)
                    else Modifier.neumorphic(cornerRadius = 22.dp, elevation = 4.dp, blur = 8.dp)
                )
                .background(if (isSelected) Color(0xFFDCDCDC) else NeumorphSurface, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = label,
                tint = if (isSelected) NeumorphAccentPrimary else NeumorphTextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) NeumorphAccentPrimary else NeumorphTextSecondary
        )
    }
}
