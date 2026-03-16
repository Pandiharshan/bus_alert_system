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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.campusbussbuddy.R
import com.campusbussbuddy.ui.theme.GlassBackground
import com.campusbussbuddy.ui.components.*

@Composable
fun BusOperationsHubScreen(
    busNumber: String = "42-B",
    onLogoutClick: () -> Unit = {},
    onStartTrip: () -> Unit = {},
    onMembersClick: () -> Unit = {}
) {
    var totalMembers by remember { mutableStateOf(48) }
    var presentToday by remember { mutableStateOf(32) }
    var driverName by remember { mutableStateOf("Alex Thompson") }
    var shiftInfo by remember { mutableStateOf("Morning Shift") }
    var routeInfo by remember { mutableStateOf("Route: North Campus → Science Park Terminal") }
    
    GlassBackground {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Top Bar (reusable component)
                TopBarLayout(
                    title = "Bus Operations",
                    onBackClick = onLogoutClick
                )
                
                // Main Content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 90.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Driver Profile Section
                    BusOperationsDriverProfileSection(
                        driverName = driverName,
                        busNumber = busNumber,
                        shiftInfo = shiftInfo
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Stats Cards Row (reusable stat cards)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        StatCard(
                            value = totalMembers.toString(),
                            label = "TOTAL MEMBERS",
                            modifier = Modifier.weight(1f)
                        )
                        
                        StatCard(
                            value = presentToday.toString(),
                            label = "PRESENT TODAY",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Start Trip Button
                    BusOperationsStartTripButton(onClick = onStartTrip)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Route Information
                    BusOperationsRouteInformation(routeInfo)
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Management Cards (reusable glass list cards)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        BusOperationsManagementCard(
                            icon = R.drawable.ic_group,
                            title = "Members List",
                            modifier = Modifier.weight(1f),
                            onClick = { /* TODO: Navigate to members list */ }
                        )
                        
                        BusOperationsManagementCard(
                            icon = R.drawable.ic_directions_bus_vector,
                            title = "Bus Profile",
                            modifier = Modifier.weight(1f),
                            onClick = { /* TODO: Navigate to bus profile */ }
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
            
            // Fixed Bottom Navigation (reusable component)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color.Transparent)
            ) {
                BottomNavBar {
                    BottomNavItem(
                        icon = R.drawable.ic_home,
                        label = "HOME",
                        isSelected = true,
                        onClick = { }
                    )
                    
                    BottomNavItem(
                        icon = R.drawable.ic_group,
                        label = "MEMBERS",
                        onClick = onMembersClick
                    )
                    
                    BottomNavItem(
                        icon = R.drawable.ic_map,
                        label = "ROUTES",
                        onClick = { }
                    )
                    
                    BottomNavItem(
                        icon = R.drawable.ic_settings,
                        label = "SETTINGS",
                        onClick = { }
                    )
                }
            }
        }
    }
}

@Composable
private fun BusOperationsDriverProfileSection(
    driverName: String,
    busNumber: String,
    shiftInfo: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Driver Profile Circle
        Box(
            modifier = Modifier
                .size(56.dp)
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
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_person),
                contentDescription = "Driver Profile",
                tint = Color(0xFF2A2A2A),
                modifier = Modifier.size(28.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            SectionTitle(
                text = driverName,
                fontSize = 18
            )
            
            Text(
                text = "Bus #$busNumber ($shiftInfo)",
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF4A5F5F)
            )
        }
    }
}

@Composable
private fun BusOperationsStartTripButton(onClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .size(200.dp)
            .shadow(
                elevation = 12.dp,
                shape = CircleShape,
                ambientColor = Color(0xFF4CAF50).copy(alpha = 0.3f),
                spotColor = Color(0xFF4CAF50).copy(alpha = 0.3f)
            )
            .background(
                Color(0xFF4CAF50),
                CircleShape
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    color = Color.White.copy(0.3f),
                    bounded = true
                )
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Play Button Icon
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        Color.White,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_chevron_right),
                    contentDescription = "Start Trip",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(28.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "START TRIP",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
private fun BusOperationsRouteInformation(routeInfo: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_route),
            contentDescription = "Route",
            tint = Color(0xFF4A5F5F),
            modifier = Modifier.size(16.dp)
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Text(
            text = routeInfo,
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF4A5F5F),
            textAlign = TextAlign.Center,
            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
        )
    }
}

@Composable
private fun BusOperationsManagementCard(
    icon: Int,
    title: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    GlassListCard(
        modifier = modifier.height(120.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(0.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = title,
                tint = Color(0xFF2A2A2A),
                modifier = Modifier.size(32.dp)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A1A),
                textAlign = TextAlign.Center
            )
        }
    }
}