package com.campusbussbuddy.ui.screens.driver

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.campusbussbuddy.ui.navigation.Destinations

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverBusHomeScreen(
    navController: NavHostController
) {
    // Animation for the gradient
    val infiniteTransition = rememberInfiniteTransition(label = "gradient")
    val animatedOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset"
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF667eea).copy(alpha = 0.8f + animatedOffset * 0.2f),
                        Color(0xFF764ba2).copy(alpha = 0.9f),
                        Color(0xFF667eea).copy(alpha = 0.7f + animatedOffset * 0.3f)
                    )
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                // Header
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "🚌 Bus #247",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    
                    Text(
                        text = "Dashboard",
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.padding(bottom = 32.dp)
                    )
                }
            }
            
            item {
                // Stats Cards Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatCard(
                        title = "Total Students",
                        value = "42",
                        icon = Icons.Default.Person,
                        gradient = listOf(Color(0xFF4facfe), Color(0xFF00f2fe)),
                        modifier = Modifier.weight(1f)
                    )
                    
                    StatCard(
                        title = "Present Today",
                        value = "38",
                        icon = Icons.Default.CheckCircle,
                        gradient = listOf(Color(0xFF43e97b), Color(0xFF38f9d7)),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            item {
                StatCard(
                    title = "Trip Status",
                    value = "Ready to Start",
                    icon = Icons.Default.Build,
                    gradient = listOf(Color(0xFFfa709a), Color(0xFFfee140)),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            item {
                // Action Buttons
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ActionButton(
                        title = "Start Trip",
                        subtitle = "Begin today's route",
                        icon = Icons.Default.PlayArrow,
                        gradient = listOf(Color(0xFF667eea), Color(0xFF764ba2)),
                        onClick = { navController.navigate(Destinations.TRIP_SCREEN) }
                    )
                    
                    ActionButton(
                        title = "Bus Members",
                        subtitle = "View assigned students",
                        icon = Icons.Default.Person,
                        gradient = listOf(Color(0xFF4facfe), Color(0xFF00f2fe)),
                        onClick = { navController.navigate(Destinations.BUS_MEMBERS) }
                    )
                    
                    ActionButton(
                        title = "Attendance",
                        subtitle = "Check today's attendance",
                        icon = Icons.Default.List,
                        gradient = listOf(Color(0xFF43e97b), Color(0xFF38f9d7)),
                        onClick = { navController.navigate(Destinations.ATTENDANCE) }
                    )
                    
                    ActionButton(
                        title = "Bus Profile",
                        subtitle = "Bus info and QR code",
                        icon = Icons.Default.Info,
                        gradient = listOf(Color(0xFFfa709a), Color(0xFFfee140)),
                        onClick = { navController.navigate(Destinations.BUS_PROFILE) }
                    )
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
                
                // Sign Out Button
                OutlinedButton(
                    onClick = { navController.navigate(Destinations.AUTH) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "← Sign Out",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    gradient: List<Color>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(120.dp)
            .clip(RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        color = Color(0xFF718096),
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = value,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D3748)
                    )
                }
                
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(brush = Brush.linearGradient(gradient)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        modifier = Modifier.size(20.dp),
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ActionButton(
    title: String,
    subtitle: String,
    icon: ImageVector,
    gradient: List<Color>,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clip(RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(brush = Brush.linearGradient(gradient)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    modifier = Modifier.size(24.dp),
                    tint = Color.White
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D3748)
                )
                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    color = Color(0xFF718096)
                )
            }
            
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Navigate",
                tint = Color(0xFF718096)
            )
        }
    }
}