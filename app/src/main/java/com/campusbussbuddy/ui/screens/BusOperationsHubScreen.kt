package com.campusbussbuddy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.campusbussbuddy.R
import com.campusbussbuddy.ui.theme.GlassBackground

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
                // Top Bar
                BusOperationsTopBar(onBackClick = onLogoutClick)
                
                // Main Content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 90.dp), // Add bottom padding for fixed footer
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
                    
                    // Stats Cards Row
                    BusOperationsStatsCardsRow(
                        totalMembers = totalMembers,
                        presentToday = presentToday
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Start Trip Button
                    BusOperationsStartTripButton(onClick = onStartTrip)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Route Information
                    BusOperationsRouteInformation(routeInfo)
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Management Cards
                    BusOperationsManagementCards()
                    
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
            
            // Fixed Bottom Navigation
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color.Transparent)
            ) {
                BusOperationsBottomNavigationBar(onMembersClick = onMembersClick)
            }
        }
    }
}

@Composable
private fun BusOperationsTopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(Color.Transparent)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_chevron_left),
                contentDescription = "Back",
                tint = Color(0xFF2C3E3E),
                modifier = Modifier.size(32.dp)
            )
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Text(
            text = "Bus Operations",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A),
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Placeholder for symmetry
        Spacer(modifier = Modifier.size(40.dp))
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
            Text(
                text = driverName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
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
private fun BusOperationsStatsCardsRow(
    totalMembers: Int,
    presentToday: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Total Members Card
        BusOperationsStatsCard(
            title = "TOTAL MEMBERS",
            value = totalMembers.toString(),
            modifier = Modifier.weight(1f)
        )
        
        // Present Today Card
        BusOperationsStatsCard(
            title = "PRESENT TODAY",
            value = presentToday.toString(),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun BusOperationsStatsCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(100.dp)
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
        border = BorderStroke(
            1.dp,
            Color.White.copy(alpha = 0.30f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4A5F5F),
                letterSpacing = 0.5.sp,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = value,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A),
                textAlign = TextAlign.Center
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
private fun BusOperationsManagementCards() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Members List Card
        BusOperationsManagementCard(
            icon = R.drawable.ic_group,
            title = "Members List",
            modifier = Modifier.weight(1f),
            onClick = { /* TODO: Navigate to members list */ }
        )
        
        // Bus Profile Card
        BusOperationsManagementCard(
            icon = R.drawable.ic_directions_bus_vector,
            title = "Bus Profile",
            modifier = Modifier.weight(1f),
            onClick = { /* TODO: Navigate to bus profile */ }
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
    Card(
        modifier = modifier
            .height(120.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.08f)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    color = Color(0xFF6B9A92).copy(alpha = 0.2f),
                    bounded = true
                )
            ) { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.25f)
        ),
        border = BorderStroke(
            1.dp,
            Color.White.copy(alpha = 0.30f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
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

@Composable
private fun BusOperationsBottomNavigationBar(onMembersClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 24.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(35.dp),
                    ambientColor = Color.Black.copy(alpha = 0.08f),
                    spotColor = Color.Black.copy(alpha = 0.08f)
                ),
            shape = RoundedCornerShape(35.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.25f)
            ),
            border = BorderStroke(
                1.dp,
                Color.White.copy(alpha = 0.30f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BusOperationsBottomNavItem(
                    icon = R.drawable.ic_home,
                    label = "HOME",
                    isSelected = true,
                    onClick = { }
                )
                
                BusOperationsBottomNavItem(
                    icon = R.drawable.ic_group,
                    label = "MEMBERS",
                    isSelected = false,
                    onClick = onMembersClick
                )
                
                BusOperationsBottomNavItem(
                    icon = R.drawable.ic_map,
                    label = "ROUTES",
                    isSelected = false,
                    onClick = { }
                )
                
                BusOperationsBottomNavItem(
                    icon = R.drawable.ic_settings,
                    label = "SETTINGS",
                    isSelected = false,
                    onClick = { }
                )
            }
        }
    }
}

@Composable
private fun BusOperationsBottomNavItem(
    icon: Int,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    bounded = false,
                    radius = 24.dp,
                    color = Color(0xFF6B9A92).copy(alpha = 0.2f)
                )
            ) { onClick() }
            .padding(8.dp)
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = label,
            tint = if (isSelected) Color(0xFF2A2A2A) else Color(0xFF4A5F5F),
            modifier = Modifier.size(20.dp)
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color(0xFF2A2A2A) else Color(0xFF4A5F5F),
            letterSpacing = 0.5.sp
        )
    }
}