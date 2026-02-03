@file:OptIn(ExperimentalMaterial3Api::class)

package com.campusbussbuddy.ui.screens.student

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveTrackingScreen(
    navController: NavHostController
) {
    val primary = Color(0xFF0DF26C)
    val backgroundDark = Color(0xFF102217)
    val cardBackground = Color(0xFF1A2E21)
    
    var isProximityAlertEnabled by remember { mutableStateOf(true) }
    var selectedAlertType by remember { mutableStateOf("Alarm") }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundDark)
    ) {
        // Map Background with Gradient Overlay
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Simulated map background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF1F2937))
            ) {
                // Map pattern simulation
                repeat(20) { i ->
                    repeat(15) { j ->
                        Box(
                            modifier = Modifier
                                .offset(
                                    x = (i * 40).dp,
                                    y = (j * 40).dp
                                )
                                .size(2.dp)
                                .background(
                                    Color.White.copy(alpha = 0.1f),
                                    CircleShape
                                )
                        )
                    }
                }
            }
            
            // Gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                backgroundDark.copy(alpha = 0.8f),
                                Color.Transparent,
                                Color.Transparent,
                                backgroundDark.copy(alpha = 0.9f)
                            )
                        )
                    )
            )
            
            // Animated Bus Marker (Center)
            Box(
                modifier = Modifier.align(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                // Pulsing circle
                val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                val scale by infiniteTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = 1.5f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(2000),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "pulse"
                )
                
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .scale(scale)
                        .background(
                            primary.copy(alpha = 0.1f),
                            CircleShape
                        )
                        .border(
                            1.dp,
                            primary.copy(alpha = 0.2f),
                            CircleShape
                        )
                )
                
                // Bus icon
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(primary, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🚌", fontSize = 24.sp, color = backgroundDark)
                }
            }
            
            // Student Location Marker (Bottom Right)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = (-80).dp, y = (-200).dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(Color.Blue, CircleShape)
                            .border(2.dp, Color.White, CircleShape)
                    )
                    
                    Surface(
                        color = backgroundDark.copy(alpha = 0.8f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "You",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
        
        // Top Navigation Bar
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = backgroundDark.copy(alpha = 0.8f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .size(40.dp)
                        .background(primary.copy(alpha = 0.1f), CircleShape)
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Live Tracking",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Route 42A • Morning Trip",
                        color = Color(0xFF9CA3AF),
                        fontSize = 12.sp
                    )
                }
                
                // Live indicator
                Surface(
                    color = primary.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.border(
                        1.dp,
                        primary.copy(alpha = 0.3f),
                        RoundedCornerShape(20.dp)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Pulsing dot
                        val liveTransition = rememberInfiniteTransition(label = "live")
                        val livePulse by liveTransition.animateFloat(
                            initialValue = 0.5f,
                            targetValue = 1f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1000),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "live"
                        )
                        
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .scale(livePulse)
                                .background(primary, CircleShape)
                        )
                        
                        Text(
                            text = "LIVE",
                            color = primary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }
        
        // Floating Top Card (ETA Info)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(top = 80.dp),
            colors = CardDefaults.cardColors(
                containerColor = backgroundDark.copy(alpha = 0.8f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ESTIMATED ARRIVAL",
                        color = primary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "5 mins",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "| 1.2 km",
                            color = Color(0xFF9CA3AF),
                            fontSize = 16.sp
                        )
                    }
                    
                    Text(
                        text = "Bus #42 • John Doe",
                        color = Color(0xFF9CA3AF),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Gray)
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Driver",
                        tint = Color.White,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                    )
                }
            }
        }
        
        // Map Controls (Right side)
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = backgroundDark.copy(alpha = 0.8f)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column {
                    IconButton(
                        onClick = { },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Zoom in",
                            tint = Color.White
                        )
                    }
                    
                    Divider(
                        color = Color.White.copy(alpha = 0.1f),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    
                    IconButton(
                        onClick = { },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Zoom out",
                            tint = Color.White
                        )
                    }
                }
            }
            
            IconButton(
                onClick = { },
                modifier = Modifier
                    .size(48.dp)
                    .background(primary, RoundedCornerShape(8.dp))
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "My location",
                    tint = backgroundDark
                )
            }
        }
        
        // Bottom Sheet Control Panel
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = backgroundDark.copy(alpha = 0.95f)
            ),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                // Handle bar
                Box(
                    modifier = Modifier
                        .width(48.dp)
                        .height(6.dp)
                        .background(Color(0xFF374151), RoundedCornerShape(3.dp))
                        .align(Alignment.CenterHorizontally)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Smart Proximity Alert
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Smart Proximity Alert",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Notify when within 500m",
                            color = Color(0xFF9CA3AF),
                            fontSize = 14.sp
                        )
                    }
                    
                    Switch(
                        checked = isProximityAlertEnabled,
                        onCheckedChange = { isProximityAlertEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = primary,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color(0xFF374151)
                        )
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Alert Type Selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AlertTypeButton(
                        icon = Icons.Default.Notifications,
                        label = "ALARM",
                        isSelected = selectedAlertType == "Alarm",
                        onClick = { selectedAlertType = "Alarm" },
                        modifier = Modifier.weight(1f)
                    )
                    
                    AlertTypeButton(
                        icon = Icons.Default.Add,
                        label = "VIBRATE",
                        isSelected = selectedAlertType == "Vibrate",
                        onClick = { selectedAlertType = "Vibrate" },
                        modifier = Modifier.weight(1f)
                    )
                    
                    AlertTypeButton(
                        icon = Icons.Default.Add,
                        label = "BOTH",
                        isSelected = selectedAlertType == "Both",
                        onClick = { selectedAlertType = "Both" },
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Bus Stats
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.05f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(primary.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("", fontSize = 20.sp)
                        }
                        
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Current Speed",
                                color = Color(0xFF9CA3AF),
                                fontSize = 12.sp
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "45",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = " km/h",
                                    color = Color(0xFF9CA3AF),
                                    fontSize = 12.sp
                                )
                            }
                        }
                        
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(32.dp)
                                .background(Color.White.copy(alpha = 0.1f))
                        )
                        
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = "Traffic",
                                color = Color(0xFF9CA3AF),
                                fontSize = 12.sp
                            )
                            Text(
                                text = "Moderate",
                                color = Color(0xFFFBBF24),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
        
        // iOS Home Indicator
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp)
                .width(128.dp)
                .height(4.dp)
                .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(2.dp))
        )
    }
}

@Composable
private fun AlertTypeButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primary = Color(0xFF0DF26C)
    
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) primary.copy(alpha = 0.1f) else Color.White.copy(alpha = 0.05f)
        ),
        shape = RoundedCornerShape(12.dp),
        border = if (isSelected) BorderStroke(2.dp, primary) else BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                icon,
                contentDescription = label,
                tint = if (isSelected) primary else Color(0xFF9CA3AF)
            )
            Text(
                text = label,
                color = if (isSelected) primary else Color(0xFF9CA3AF),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}