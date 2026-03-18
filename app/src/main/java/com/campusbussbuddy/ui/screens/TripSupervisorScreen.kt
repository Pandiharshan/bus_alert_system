package com.campusbussbuddy.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.campusbussbuddy.R
import com.campusbussbuddy.ui.theme.*
import com.campusbussbuddy.ui.neumorphism.cards.NeumorphismCard
import com.campusbussbuddy.ui.neumorphism.cards.NeumorphismStatCard
import com.campusbussbuddy.ui.neumorphism.buttons.NeumorphismButton
import com.campusbussbuddy.ui.neumorphism.buttons.NeumorphismIconButton
import com.campusbussbuddy.ui.neumorphism.layout.AppLabelPill
import com.campusbussbuddy.ui.neumorphism.layout.NeumorphismScreenContainer
import com.campusbussbuddy.ui.neumorphism.layout.NeumorphismPill
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

@Composable
fun TripSupervisorScreen(
    busNumber: String = "42-B",
    onBackClick: () -> Unit = {},
    onCompleteTrip: () -> Unit = {}
) {
    var currentStop by remember { mutableStateOf("North Campus Terminal") }
    var boardedHere by remember { mutableStateOf(24) }
    var totalOnboard by remember { mutableStateOf(42) }
    var attendanceGoal by remember { mutableStateOf(80) }
    var currentProgress by remember { mutableStateOf(80) }
    var eta by remember { mutableStateOf("4 mins") }
    var selectedTab by remember { mutableStateOf("CURRENT") }

    NeumorphismScreenContainer {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Embedded TopBar utilizing AppLabelPill
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
                    icon = R.drawable.ic_route,
                    title = "Trip Supervisor"
                )

                NeumorphismIconButton(
                    iconRes = R.drawable.ic_my_location,
                    onClick = { /* TODO: Handle location */ },
                    size = 44.dp,
                    iconSize = 22.dp,
                    unselectedTint = NeumorphAccentPrimary,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Main Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Live Route Map Card
                TripLiveRouteMapCard(
                    busNumber = busNumber,
                    eta = eta
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Trip Status Tabs
                TripStatusTabs(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Current Stop Card
                TripCurrentStopCard(
                    stopName = currentStop,
                    boardedHere = boardedHere,
                    totalOnboard = totalOnboard,
                    attendanceGoal = attendanceGoal,
                    currentProgress = currentProgress
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Attendance Portal Card with QR Generation
                TripAttendancePortalCard(busId = busNumber)

                Spacer(modifier = Modifier.height(24.dp))

                // Complete Trip Button
                NeumorphismButton(
                    text = "Complete Trip",
                    onClick = onCompleteTrip,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun TripLiveRouteMapCard(
    busNumber: String,
    eta: String
) {
    NeumorphismCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        cornerRadius = 24.dp,
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Dotted pattern background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Simulate dotted pattern with small circles
                repeat(8) { row ->
                    repeat(15) { col ->
                        Box(
                            modifier = Modifier
                                .offset(
                                    x = (col * 20).dp,
                                    y = (row * 12).dp
                                )
                                .size(2.dp)
                                .background(
                                    NeumorphTextSecondary.copy(alpha = 0.2f),
                                    CircleShape
                                )
                        )
                    }
                }

                // Route line
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(4.dp)
                        .offset(y = 40.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    NeumorphAccentPrimary.copy(alpha = 0.4f),
                                    NeumorphAccentPrimary,
                                    NeumorphAccentPrimary.copy(alpha = 0.6f)
                                )
                            ),
                            RoundedCornerShape(2.dp)
                        )
                )

                // Bus icon on route (neumorphic circular)
                Box(
                    modifier = Modifier
                        .offset(x = 200.dp, y = 30.dp)
                        .size(24.dp)
                        .neumorphic(cornerRadius = 12.dp, elevation = 3.dp, blur = 6.dp)
                        .background(NeumorphAccentPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_directions_bus_vector),
                        contentDescription = "Bus",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            // Bottom info bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        NeumorphBgPrimary.copy(alpha = 0.8f),
                        RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NeumorphismPill(
                    label = "ROUTE $busNumber LIVE",
                    onClick = {}
                )

                Text(
                    text = "ETA: $eta",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = NeumorphTextPrimary
                )
            }
        }
    }
}

@Composable
private fun TripStatusTabs(
    selectedTab: String,
    onTabSelected: (String) -> Unit
) {
    val tabs = listOf("PAST", "CURRENT", "UPCOMING")

    NeumorphismCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 20.dp,
        contentPadding = PaddingValues(4.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            tabs.forEach { tab ->
                val isSelected = selectedTab == tab

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .then(
                            if (isSelected)
                                Modifier
                                    .neumorphicInset(cornerRadius = 16.dp, elevation = 4.dp, blur = 8.dp)
                                    .background(NeumorphBgPrimary, RoundedCornerShape(16.dp))
                            else
                                Modifier
                        )
                        .bounceClick { onTabSelected(tab) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) NeumorphAccentPrimary else NeumorphTextSecondary,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun TripCurrentStopCard(
    stopName: String,
    boardedHere: Int,
    totalOnboard: Int,
    attendanceGoal: Int,
    currentProgress: Int
) {
    NeumorphismCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 24.dp,
        contentPadding = PaddingValues(20.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Active Stop Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status indicator
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Color(0xFF4CAF50), CircleShape)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "ACTIVE STOP",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeumorphTextSecondary,
                        letterSpacing = 1.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .neumorphicInset(cornerRadius = 20.dp, blur = 6.dp)
                        .background(NeumorphBgPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_pin_drop),
                        contentDescription = "Location",
                        tint = NeumorphAccentPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Stop Name
            Text(
                text = stopName,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = NeumorphTextPrimary,
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                NeumorphismStatCard(
                    count = boardedHere.toString(),
                    label = "Boarded Here",
                    modifier = Modifier.weight(1f)
                )

                NeumorphismStatCard(
                    count = totalOnboard.toString(),
                    label = "Total Onboard",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Attendance Goal Section
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ATTENDANCE GOAL",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeumorphTextSecondary,
                        letterSpacing = 1.sp
                    )

                    Text(
                        text = "$currentProgress% REACHED",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeumorphTextSecondary,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Progress Bar (neumorphic inset)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .neumorphicInset(cornerRadius = 4.dp, elevation = 2.dp, blur = 4.dp)
                        .background(NeumorphBgPrimary, RoundedCornerShape(4.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(currentProgress / 100f)
                            .fillMaxHeight()
                            .background(NeumorphAccentPrimary, RoundedCornerShape(4.dp))
                    )
                }
            }
        }
    }
}

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
                // Initial State
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
                        qrBitmap = generateQRCode(content = "busId:$busId")
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                // QR Generated State
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

                // The crisp QR Code on an inset Neumorphism surface
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
                    label = "SCAN ACTIVE",
                    onClick = {}
                )
            }
        }
    }
}

/**
 * Helper function to generate a QR Code Bitmap from raw text using ZXing.
 *
 * @param content The string to embed into the QR code
 * @return Bitmap of the generated QR Code or null on error
 */
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