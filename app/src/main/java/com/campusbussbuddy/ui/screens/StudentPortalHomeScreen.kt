package com.campusbussbuddy.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.campusbussbuddy.firebase.BusInfo
import com.campusbussbuddy.firebase.StudentInfo
import com.campusbussbuddy.firebase.FirebaseManager
import kotlinx.coroutines.launch

@Composable
fun StudentPortalHomeScreen() {
    var studentInfo by remember { mutableStateOf<StudentInfo?>(null) }
    var busInfo by remember { mutableStateOf<BusInfo?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    
    val scope = rememberCoroutineScope()
    
    // Load student data on screen launch
    LaunchedEffect(Unit) {
        scope.launch {
            Log.d("StudentPortalHome", "Loading student info...")
            studentInfo = FirebaseManager.getCurrentStudentInfo()
            Log.d("StudentPortalHome", "Student info loaded: ${studentInfo?.name}")
            Log.d("StudentPortalHome", "Bus ID: ${studentInfo?.busId}")
            
            if (studentInfo?.busId?.isNotEmpty() == true) {
                busInfo = FirebaseManager.getBusInfo(studentInfo!!.busId)
                Log.d("StudentPortalHome", "Bus info loaded: Bus ${busInfo?.busNumber}")
            }
            isLoading = false
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        if (isLoading) {
            // Loading State
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color(0xFF7DD3C0)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                item {
                    // Top Header
                    TopHeader(studentInfo)
                }
                
                item {
                    // Welcome Section
                    WelcomeSection(studentInfo, busInfo)
                }
                
                item {
                    // Bus Status Card
                    BusStatusCard(busInfo)
                }
                
                item {
                    // Driver Info Card
                    DriverInfoCard(busInfo)
                }
                
                item {
                    // Action Grid
                    ActionGrid()
                }
                
                item {
                    // Recent Activity
                    RecentActivitySection()
                }
            }
        }
        
        // Bottom Navigation
        BottomNavigationBar(
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun TopHeader(studentInfo: StudentInfo?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Avatar
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        Color(0xFFFFB4A2),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_person),
                    contentDescription = "Profile",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Text(
                text = studentInfo?.name ?: "Student Portal",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
        }
        
        // Notification Bell
        IconButton(
            onClick = { /* Handle notification */ },
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_notifications),
                contentDescription = "Notifications",
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun WelcomeSection(studentInfo: StudentInfo?, busInfo: BusInfo?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Text(
            text = "Welcome, ${studentInfo?.name ?: "Student"}",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_directions_bus_vector),
                contentDescription = "Bus",
                tint = Color(0xFFB8A9D9),
                modifier = Modifier.size(16.dp)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = if (busInfo != null) "Bus ${busInfo.busNumber} • ${studentInfo?.stop ?: "Your Stop"}" 
                       else "No bus assigned",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF888888)
            )
        }
    }
}

@Composable
private fun BusStatusCard(busInfo: BusInfo?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.08f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "BUS STATUS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF888888),
                    letterSpacing = 1.sp
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = if (busInfo?.activeDriverId?.isNotEmpty() == true) "Active" else "Not Active",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (busInfo?.activeDriverId?.isNotEmpty() == true) Color(0xFF4CAF50) else Color(0xFF888888)
                )
                
                if (busInfo != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Bus ${busInfo.busNumber}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF666666)
                    )
                }
            }
            
            // Status Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        Color(0xFFB8A9D9).copy(alpha = 0.2f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_directions_bus_vector),
                    contentDescription = "Bus",
                    tint = Color(0xFFB8A9D9),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun DriverInfoCard(busInfo: BusInfo?) {
    if (busInfo?.activeDriverId?.isNotEmpty() == true) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(20.dp),
                    ambientColor = Color.Black.copy(alpha = 0.08f),
                    spotColor = Color.Black.copy(alpha = 0.08f)
                ),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "ACTIVE DRIVER",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF888888),
                    letterSpacing = 1.sp
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                Color(0xFF7DD3C0).copy(alpha = 0.2f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_person),
                            contentDescription = "Driver",
                            tint = Color(0xFF7DD3C0),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column {
                        Text(
                            text = busInfo.activeDriverName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black
                        )
                        
                        if (busInfo.activeDriverPhone.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_call),
                                    contentDescription = "Phone",
                                    tint = Color(0xFF888888),
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = busInfo.activeDriverPhone,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = Color(0xFF888888)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionGrid() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ActionCard(
                icon = R.drawable.ic_my_location,
                title = "Live\nTracking",
                modifier = Modifier.weight(1f)
            )
            
            ActionCard(
                icon = R.drawable.ic_calendar_today,
                title = "Attendance\nLog",
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ActionCard(
                icon = R.drawable.ic_route,
                title = "Bus\nRoute",
                modifier = Modifier.weight(1f)
            )
            
            ActionCard(
                icon = R.drawable.ic_emergency,
                title = "Support\n& SOS",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ActionCard(
    icon: Int,
    title: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(120.dp)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black.copy(alpha = 0.06f),
                spotColor = Color.Black.copy(alpha = 0.06f)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    color = Color(0xFF7DD3C0).copy(alpha = 0.2f),
                    bounded = true
                )
            ) { /* Handle action */ },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        Color(0xFFF5F5F5),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = title,
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun RecentActivitySection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = "Recent Activity",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Activity Items
        ActivityItem(
            icon = R.drawable.ic_check_circle,
            iconColor = Color(0xFF4CAF50),
            title = "Boarded Bus",
            subtitle = "Yesterday, 4:15 PM"
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        ActivityItem(
            icon = R.drawable.ic_notifications,
            iconColor = Color(0xFF2196F3),
            title = "Schedule Update",
            subtitle = "Oct 12, 09:00 AM"
        )
    }
}

@Composable
private fun ActivityItem(
    icon: Int,
    iconColor: Color,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    iconColor.copy(alpha = 0.1f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = title,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            
            Text(
                text = subtitle,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF888888)
            )
        }
    }
}

@Composable
private fun BottomNavigationBar(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = Color.Black.copy(alpha = 0.1f),
                spotColor = Color.Black.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            BottomNavItem(
                icon = R.drawable.ic_home,
                label = "Home",
                isSelected = true
            )
            
            BottomNavItem(
                icon = R.drawable.ic_map,
                label = "Map",
                isSelected = false
            )
            
            BottomNavItem(
                icon = R.drawable.ic_history,
                label = "Logs",
                isSelected = false
            )
            
            BottomNavItem(
                icon = R.drawable.ic_person,
                label = "Profile",
                isSelected = false
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: Int,
    label: String,
    isSelected: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    color = Color(0xFF7DD3C0).copy(alpha = 0.2f),
                    bounded = true
                )
            ) { /* Handle navigation */ }
            .padding(8.dp)
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = label,
            tint = if (isSelected) Color(0xFF7DD3C0) else Color(0xFF888888),
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) Color(0xFF7DD3C0) else Color(0xFF888888)
        )
    }
}