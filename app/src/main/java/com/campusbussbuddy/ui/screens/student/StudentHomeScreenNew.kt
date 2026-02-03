@file:OptIn(ExperimentalMaterial3Api::class)

package com.campusbussbuddy.ui.screens.student

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.painterResource
import com.campusbussbuddy.R
import com.campusbussbuddy.ui.navigation.Destinations
import com.campusbussbuddy.ui.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentHomeScreenNew(navController: NavHostController) {
    // Animations
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAnimation by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "pulse"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.BackgroundDark)
    ) {
        // Top Navigation Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Avatar
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                AppColors.Primary.copy(alpha = 0.3f),
                                AppColors.Primary.copy(alpha = 0.1f)
                            )
                        )
                    )
                    .border(2.dp, AppColors.Primary, CircleShape)
                    .clickable { navController.navigate(Destinations.STUDENT_PROFILE) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_person),
                    contentDescription = "Profile",
                    modifier = Modifier.size(20.dp),
                    tint = AppColors.Primary
                )
            }
            
            Text(
                text = "Good Morning, Alex",
                color = AppColors.OnBackground,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            
            IconButton(
                onClick = { /* Notifications */ },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_notifications),
                    contentDescription = "Notifications",
                    modifier = Modifier.size(20.dp),
                    tint = AppColors.OnBackground
                )
            }
        }
        
        // Student ID
        Text(
            text = "Student ID: #8829",
            color = AppColors.OnSurfaceVariant,
            fontSize = 14.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Live Status Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = AppColors.Surface
            ),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(AppColors.StatusOnline, CircleShape)
                                .scale(pulseAnimation)
                        )
                        Text(
                            text = "LIVE STATUS",
                            color = AppColors.Primary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Next Bus: 10 mins away",
                        color = AppColors.OnSurface,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = "Bus 42B - South High School Route",
                        color = AppColors.OnSurfaceVariant,
                        fontSize = 14.sp
                    )
                }
                
                // Bus Icon
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            AppColors.Primary.copy(alpha = 0.1f),
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_directions_bus_vector),
                        contentDescription = "Bus",
                        modifier = Modifier.size(40.dp),
                        tint = AppColors.Primary
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Quick Access Cards
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // My Profile Card
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { navController.navigate(Destinations.STUDENT_PROFILE) },
                colors = CardDefaults.cardColors(
                    containerColor = AppColors.Surface
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_person),
                        contentDescription = "Profile",
                        modifier = Modifier.size(32.dp),
                        tint = AppColors.Primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "My Profile",
                        color = AppColors.OnSurface,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Alex Johnson",
                        color = AppColors.OnSurfaceVariant,
                        fontSize = 12.sp
                    )
                }
            }
            
            // Absent Calendar Card
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { navController.navigate(Destinations.ABSENT_CALENDAR) },
                colors = CardDefaults.cardColors(
                    containerColor = AppColors.Surface
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_calendar_month),
                        contentDescription = "Calendar",
                        modifier = Modifier.size(32.dp),
                        tint = AppColors.Primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Absent Calendar",
                        color = AppColors.OnSurface,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Manage leave",
                        color = AppColors.OnSurfaceVariant,
                        fontSize = 12.sp
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Live Map Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clickable { navController.navigate(Destinations.LIVE_TRACKING) },
            colors = CardDefaults.cardColors(
                containerColor = AppColors.Surface
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_map),
                            contentDescription = "Map",
                            modifier = Modifier.size(20.dp),
                            tint = AppColors.Primary
                        )
                        Text(
                            text = "Live Map",
                            color = AppColors.OnSurface,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "3 ACTIVE BUSES",
                            color = AppColors.Primary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "Track current bus location in real-time",
                        color = AppColors.OnSurfaceVariant,
                        fontSize = 14.sp
                    )
                }
                
                // Map Preview
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(
                            AppColors.Primary.copy(alpha = 0.1f),
                            RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_map),
                        contentDescription = "Map Preview",
                        modifier = Modifier.size(24.dp),
                        tint = AppColors.Primary
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Attendance Summary
        Text(
            text = "Attendance Summary",
            color = AppColors.OnBackground,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Attendance Cards
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Today's Attendance
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = AppColors.Surface
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_pending),
                        contentDescription = "Pending",
                        modifier = Modifier.size(24.dp),
                        tint = AppColors.Warning
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Attendance Marked",
                            color = AppColors.OnSurface,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Status: Pending scan",
                            color = AppColors.OnSurfaceVariant,
                            fontSize = 14.sp
                        )
                    }
                    
                    Text(
                        text = "Today",
                        color = AppColors.OnSurfaceVariant,
                        fontSize = 12.sp
                    )
                }
            }
            
            // Yesterday's Trip
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = AppColors.Surface
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_check_circle),
                        contentDescription = "Complete",
                        modifier = Modifier.size(24.dp),
                        tint = AppColors.Success
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Morning Trip",
                            color = AppColors.OnSurface,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Yesterday • Arrived 8:15 AM",
                            color = AppColors.OnSurfaceVariant,
                            fontSize = 14.sp
                        )
                    }
                    
                    Text(
                        text = "May 24",
                        color = AppColors.OnSurfaceVariant,
                        fontSize = 12.sp
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Scan QR Button
        Button(
            onClick = { navController.navigate(Destinations.QR_SCANNER) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AppColors.Primary,
                contentColor = AppColors.OnPrimary
            ),
            shape = RoundedCornerShape(28.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 8.dp,
                pressedElevation = 4.dp
            )
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_qr_code_scanner),
                    contentDescription = "QR Scanner",
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Scan QR",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Bottom Navigation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Home
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_home),
                    contentDescription = "Home",
                    modifier = Modifier.size(24.dp),
                    tint = AppColors.Primary
                )
                Text(
                    text = "Home",
                    color = AppColors.Primary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            
            // Track
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { navController.navigate(Destinations.LIVE_TRACKING) }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_map),
                    contentDescription = "Track",
                    modifier = Modifier.size(24.dp),
                    tint = AppColors.OnSurfaceVariant
                )
                Text(
                    text = "Track",
                    color = AppColors.OnSurfaceVariant,
                    fontSize = 12.sp
                )
            }
            
            // History
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_history),
                    contentDescription = "History",
                    modifier = Modifier.size(24.dp),
                    tint = AppColors.OnSurfaceVariant
                )
                Text(
                    text = "History",
                    color = AppColors.OnSurfaceVariant,
                    fontSize = 12.sp
                )
            }
            
            // Settings
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_settings),
                    contentDescription = "Settings",
                    modifier = Modifier.size(24.dp),
                    tint = AppColors.OnSurfaceVariant
                )
                Text(
                    text = "Settings",
                    color = AppColors.OnSurfaceVariant,
                    fontSize = 12.sp
                )
            }
        }
    }
}