package com.campusbussbuddy.ui.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.campusbussbuddy.ui.components.*
import com.campusbussbuddy.ui.navigation.Destinations

@Composable
fun StudentHomeScreen(
    navController: NavHostController
) {
    AnimatedGradientBackground(
        colors = listOf(
            Color(0xFF43e97b),
            Color(0xFF38f9d7),
            Color(0xFF43e97b)
        )
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                AppTopBar(
                    title = "Good morning, Alex! ☀️",
                    onActionClick = { navController.navigate(Destinations.AUTH) },
                    actionIcon = Icons.Default.ArrowBack
                )
            }
            
            item {
                // Quick Actions Card
                AppCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    androidx.compose.material3.Text(
                        text = "Quick Actions 🚀",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D3748),
                        modifier = Modifier.padding(bottom = 20.dp)
                    )
                    
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ActionItem(
                            title = "Bus Map",
                            subtitle = "Track buses in real-time",
                            icon = Icons.Default.LocationOn,
                            gradient = listOf(Color(0xFF667eea), Color(0xFF764ba2)),
                            onClick = { navController.navigate(Destinations.BUS_MAP) }
                        )
                        
                        ActionItem(
                            title = "QR Scanner",
                            subtitle = "Scan to board buses",
                            icon = Icons.Default.Settings,
                            gradient = listOf(Color(0xFF4facfe), Color(0xFF00f2fe)),
                            onClick = { navController.navigate(Destinations.QR_SCANNER) }
                        )
                        
                        ActionItem(
                            title = "Absent Calendar",
                            subtitle = "Mark future absences",
                            icon = Icons.Default.DateRange,
                            gradient = listOf(Color(0xFFfa709a), Color(0xFFfee140)),
                            onClick = { navController.navigate(Destinations.ABSENT_CALENDAR) }
                        )
                        
                        ActionItem(
                            title = "Profile",
                            subtitle = "Manage your account",
                            icon = Icons.Default.Person,
                            gradient = listOf(Color(0xFF43e97b), Color(0xFF38f9d7)),
                            onClick = { navController.navigate(Destinations.STUDENT_PROFILE) }
                        )
                    }
                }
            }
            
            item {
                // Recent Activity Card
                AppCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    androidx.compose.material3.Text(
                        text = "Recent Activity 📊",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D3748),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        androidx.compose.foundation.layout.Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                                .background(
                                    brush = androidx.compose.ui.graphics.Brush.linearGradient(
                                        colors = listOf(Color(0xFF43e97b), Color(0xFF38f9d7))
                                    )
                                ),
                            contentAlignment = androidx.compose.ui.Alignment.Center
                        ) {
                            androidx.compose.material3.Text(
                                text = "🚌",
                                fontSize = 20.sp
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            androidx.compose.material3.Text(
                                text = "No recent trips",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF2D3748)
                            )
                            androidx.compose.material3.Text(
                                text = "Your bus boarding history will appear here",
                                fontSize = 14.sp,
                                color = Color(0xFF718096)
                            )
                        }
                    }
                }
            }
        }
    }
}