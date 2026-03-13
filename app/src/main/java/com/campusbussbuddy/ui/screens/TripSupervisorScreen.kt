package com.campusbussbuddy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.campusbussbuddy.R
import com.campusbussbuddy.ui.theme.GlassBackground

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
    
    GlassBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Top Bar
            TopBar(
                onBackClick = onBackClick,
                onLocationClick = { /* TODO: Handle location */ }
            )
            
            // Main Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                
                // Live Route Map Card
                LiveRouteMapCard(
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
                CurrentStopCard(
                    stopName = currentStop,
                    boardedHere = boardedHere,
                    totalOnboard = totalOnboard,
                    attendanceGoal = attendanceGoal,
                    currentProgress = currentProgress
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Attendance Portal Card
                AttendancePortalCard()
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Complete Trip Button
                CompleteTripButton(onClick = onCompleteTrip)
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun TopBar(
    onBackClick: () -> Unit,
    onLocationClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(Color.Transparent)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Back Button
        Box(
            modifier = Modifier
                .size(48.dp)
                .shadow(
                    elevation = 4.dp,
                    shape = CircleShape,
                    ambientColor = Color.Black.copy(alpha = 0.08f),
                    spotColor = Color.Black.copy(alpha = 0.08f)
                )
                .background(
                    Color.White.copy(alpha = 0.25f),
                    CircleShape
                )
                .border(
                    1.dp,
                    Color.White.copy(alpha = 0.30f),
                    CircleShape
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(
                        bounded = true,
                        color = Color.White.copy(0.20f)
                    )
                ) { onBackClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_chevron_left),
                contentDescription = "Back",
                tint = Color(0xFF2A2A2A),
                modifier = Modifier.size(24.dp)
            )
        }
        
        // Title Section
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Trip Supervisor",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )
            
            Text(
                text = "LIVE CONSOLE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4A5F5F),
                letterSpacing = 1.sp
            )
        }
        
        // Location Button
        Box(
            modifier = Modifier
                .size(48.dp)
                .shadow(
                    elevation = 4.dp,
                    shape = CircleShape,
                    ambientColor = Color.Black.copy(alpha = 0.08f),
                    spotColor = Color.Black.copy(alpha = 0.08f)
                )
                .background(
                    Color.White.copy(alpha = 0.25f),
                    CircleShape
                )
                .border(
                    1.dp,
                    Color.White.copy(alpha = 0.30f),
                    CircleShape
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(
                        bounded = true,
                        color = Color.White.copy(0.20f)
                    )
                ) { onLocationClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_my_location),
                contentDescription = "Location",
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun LiveRouteMapCard(
    busNumber: String,
    eta: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.08f)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.25f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Color.White.copy(alpha = 0.30f)
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
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
                                    Color.White.copy(alpha = 0.3f),
                                    CircleShape
                                )
                        )
                    }
                }
                
                // Green route line (simulated)
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(4.dp)
                        .offset(y = 40.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF4CAF50).copy(alpha = 0.6f),
                                    Color(0xFF4CAF50),
                                    Color(0xFF4CAF50).copy(alpha = 0.8f)
                                )
                            ),
                            RoundedCornerShape(2.dp)
                        )
                )
                
                // Bus icon on route
                Box(
                    modifier = Modifier
                        .offset(x = 200.dp, y = 30.dp)
                        .size(24.dp)
                        .background(
                            Color(0xFF4CAF50),
                            CircleShape
                        ),
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
                        Color.White.copy(alpha = 0.4f),
                        RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                Color(0xFF4CAF50),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "ROUTE $busNumber LIVE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
                
                Text(
                    text = "ETA: $eta",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2A2A2A)
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
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.08f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.25f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Color.White.copy(alpha = 0.30f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            tabs.forEach { tab ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .background(
                            if (selectedTab == tab) Color.White.copy(alpha = 0.8f)
                            else Color.Transparent,
                            RoundedCornerShape(16.dp)
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(
                                bounded = true,
                                color = Color(0xFF6B9A92).copy(alpha = 0.2f)
                            )
                        ) { onTabSelected(tab) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab,
                        fontSize = 12.sp,
                        fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedTab == tab) Color(0xFF2A2A2A) else Color(0xFF4A5F5F),
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun CurrentStopCard(
    stopName: String,
    boardedHere: Int,
    totalOnboard: Int,
    attendanceGoal: Int,
    currentProgress: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.08f)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.25f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Color.White.copy(alpha = 0.30f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Active Stop Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                Color(0xFF4CAF50),
                                CircleShape
                            )
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = "ACTIVE STOP",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4A5F5F),
                        letterSpacing = 1.sp
                    )
                }
                
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            Color(0xFF4CAF50),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_pin_drop),
                        contentDescription = "Location",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Stop Name
            Text(
                text = stopName,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A),
                lineHeight = 28.sp
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Boarded Here
                StopStatsCard(
                    value = boardedHere.toString(),
                    label = "BOARDED HERE",
                    valueColor = Color(0xFF4CAF50),
                    modifier = Modifier.weight(1f)
                )
                
                // Total Onboard
                StopStatsCard(
                    value = totalOnboard.toString(),
                    label = "TOTAL ONBOARD",
                    valueColor = Color(0xFF2A2A2A),
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
                        color = Color(0xFF4A5F5F),
                        letterSpacing = 1.sp
                    )
                    
                    Text(
                        text = "$currentProgress% REACHED",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4A5F5F),
                        letterSpacing = 1.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Progress Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .background(
                            Color.White.copy(alpha = 0.3f),
                            RoundedCornerShape(4.dp)
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(currentProgress / 100f)
                            .fillMaxHeight()
                            .background(
                                Color(0xFF4CAF50),
                                RoundedCornerShape(4.dp)
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun StopStatsCard(
    value: String,
    label: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(80.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.08f)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.4f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Color.White.copy(alpha = 0.50f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = valueColor,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = label,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4A5F5F),
                letterSpacing = 0.5.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun AttendancePortalCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.08f)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    bounded = true,
                    color = Color(0xFF6B9A92).copy(alpha = 0.2f)
                )
            ) { /* TODO: Open attendance portal */ },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.25f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Color.White.copy(alpha = 0.30f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // QR Code Icon
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        Color.White.copy(alpha = 0.8f),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_qr_code),
                    contentDescription = "QR Code",
                    tint = Color(0xFF2A2A2A),
                    modifier = Modifier.size(32.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Attendance Portal",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "QR Code active for students boarding at this stop.",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF4A5F5F),
                    lineHeight = 16.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "SCAN ACTIVE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50),
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
private fun CompleteTripButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(28.dp)
            ),
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF4CAF50),
            contentColor = Color.White
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_check_circle),
                contentDescription = "Complete",
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Text(
                text = "Complete Trip",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}