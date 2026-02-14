package com.campusbussbuddy.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
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
import com.campusbussbuddy.firebase.FirebaseManager
import com.campusbussbuddy.ui.theme.AppBackgroundContainer
import kotlinx.coroutines.launch

@Composable
fun AdminHomeScreen(
    onManageDriversClick: () -> Unit = {},
    onManageBusesClick: () -> Unit = {},
    onManageStudentsClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    var totalStudents by remember { mutableStateOf(0) }
    var totalDrivers by remember { mutableStateOf(0) }
    var totalBuses by remember { mutableStateOf(0) }
    var activeTrips by remember { mutableStateOf(0) }
    var busesRunning by remember { mutableStateOf(0) }
    var studentsOnboard by remember { mutableStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }
    
    val scope = rememberCoroutineScope()
    
    // Fetch dashboard counts from Firestore
    LaunchedEffect(Unit) {
        scope.launch {
            Log.d("AdminHomeScreen", "Fetching dashboard counts...")
            
            // Fetch students count
            val students = FirebaseManager.getAllStudents()
            totalStudents = students.size
            Log.d("AdminHomeScreen", "Total Students: $totalStudents")
            
            // Fetch drivers count
            val drivers = FirebaseManager.getAllDrivers()
            totalDrivers = drivers.size
            Log.d("AdminHomeScreen", "Total Drivers: $totalDrivers")
            
            // Fetch buses count
            val buses = FirebaseManager.getAllBuses()
            totalBuses = buses.size
            Log.d("AdminHomeScreen", "Total Buses: $totalBuses")
            
            // Fetch live system status
            val activeBuses = buses.filter { it.activeDriverId.isNotEmpty() }
            busesRunning = activeBuses.size
            activeTrips = activeBuses.size // Same as buses running
            
            // Count students whose buses are active
            studentsOnboard = students.count { student ->
                activeBuses.any { bus -> bus.busId == student.busId }
            }
            
            Log.d("AdminHomeScreen", "Active Trips: $activeTrips")
            Log.d("AdminHomeScreen", "Buses Running: $busesRunning")
            Log.d("AdminHomeScreen", "Students Onboard: $studentsOnboard")
            
            isLoading = false
        }
    }
    AppBackgroundContainer {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            item {
                // Admin Profile Card with Logout
                AdminProfileCard(onLogoutClick = onLogoutClick)
            }
            
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            item {
                // Dashboard Stats with real data
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF7DD3C0))
                    }
                } else {
                    DashboardStatsGrid(
                        totalBuses = totalBuses,
                        totalStudents = totalStudents,
                        totalDrivers = totalDrivers
                    )
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            item {
                // Real-Time Overview with live data
                RealTimeOverviewCard(
                    activeTrips = activeTrips,
                    busesRunning = busesRunning,
                    studentsOnboard = studentsOnboard
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            item {
                // Main Action Tiles
                Text(
                    text = "System Management",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }
            
            item {
                MainActionTiles(
                    onManageDriversClick = onManageDriversClick,
                    onManageBusesClick = onManageBusesClick,
                    onManageStudentsClick = onManageStudentsClick
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
    }
}

@Composable
private fun AdminProfileCard(onLogoutClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.08f)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.28f)
        ),
        border = BorderStroke(
            2.dp,
            Color.White.copy(alpha = 0.55f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Logout Button (Left)
            IconButton(
                onClick = onLogoutClick,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_back_ios_new),
                    contentDescription = "Logout",
                    tint = Color(0xFF666666),
                    modifier = Modifier.size(18.dp)
                )
            }
            
            // Center Content: Profile Image + Name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.Center
            ) {
                // Admin Profile Image - Using admin.png
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF0F0F0))
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.admin),
                        contentDescription = "Admin Profile",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        tint = Color.Unspecified
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                // Admin Name - Reduced size
                Text(
                    text = "Pandiharshan",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
            
            // Settings Icon (Right)
            IconButton(
                onClick = { /* Handle settings */ },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_settings),
                    contentDescription = "Settings",
                    tint = Color(0xFF666666),
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
private fun DashboardStatsGrid(
    totalBuses: Int,
    totalStudents: Int,
    totalDrivers: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                value = totalBuses.toString(),
                label = "Total Buses",
                icon = R.drawable.ic_directions_bus_vector,
                modifier = Modifier.weight(1f)
            )
            
            StatCard(
                value = totalStudents.toString(),
                label = "Total Students",
                icon = R.drawable.ic_person,
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            StatCard(
                value = totalDrivers.toString(),
                label = "Total Drivers",
                icon = R.drawable.ic_group,
                modifier = Modifier.width(180.dp)
            )
        }
    }
}

@Composable
private fun StatCard(
    value: String,
    label: String,
    icon: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(100.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black.copy(alpha = 0.06f),
                spotColor = Color.Black.copy(alpha = 0.06f)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.28f)
        ),
        border = BorderStroke(
            2.dp,
            Color.White.copy(alpha = 0.55f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = label,
                tint = Color(0xFF7DD3C0),
                modifier = Modifier.size(24.dp)
            )
            
            Column {
                Text(
                    text = value,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                Text(
                    text = label,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF888888)
                )
            }
        }
    }
}

@Composable
private fun RealTimeOverviewCard(
    activeTrips: Int,
    busesRunning: Int,
    studentsOnboard: Int
) {
    val isActive = busesRunning > 0
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.08f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.28f)
        ),
        border = BorderStroke(
            2.dp,
            Color.White.copy(alpha = 0.55f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Live System Status",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
                
                // Status Indicator - Dynamic based on active buses
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                if (isActive) Color(0xFF4CAF50) else Color(0xFFFF9800),
                                CircleShape
                            )
                    )
                    
                    Spacer(modifier = Modifier.width(6.dp))
                    
                    Text(
                        text = if (isActive) "Active" else "Idle",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isActive) Color(0xFF4CAF50) else Color(0xFFFF9800)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                LiveStatItem(
                    value = activeTrips.toString(),
                    label = "Active Trips"
                )
                
                Divider(
                    modifier = Modifier
                        .width(1.dp)
                        .height(40.dp),
                    color = Color(0xFFE0E0E0)
                )
                
                LiveStatItem(
                    value = busesRunning.toString(),
                    label = "Buses Running"
                )
                
                Divider(
                    modifier = Modifier
                        .width(1.dp)
                        .height(40.dp),
                    color = Color(0xFFE0E0E0)
                )
                
                LiveStatItem(
                    value = studentsOnboard.toString(),
                    label = "Students Onboard"
                )
            }
        }
    }
}

@Composable
private fun LiveStatItem(
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF888888),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun MainActionTiles(
    onManageDriversClick: () -> Unit = {},
    onManageBusesClick: () -> Unit = {},
    onManageStudentsClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ActionTile(
                icon = R.drawable.ic_directions_bus_vector,
                title = "Manage\nBuses",
                modifier = Modifier.weight(1f),
                onClick = onManageBusesClick
            )
            
            ActionTile(
                icon = R.drawable.ic_group,
                title = "Manage\nDrivers",
                modifier = Modifier.weight(1f),
                onClick = onManageDriversClick
            )
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ActionTile(
                icon = R.drawable.ic_person,
                title = "Manage\nStudents",
                modifier = Modifier.weight(1f),
                onClick = onManageStudentsClick
            )
            
            ActionTile(
                icon = R.drawable.ic_calendar_month,
                title = "Attendance\nOverview",
                modifier = Modifier.weight(1f),
                onClick = { /* Handle attendance */ }
            )
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            ActionTile(
                icon = R.drawable.ic_map,
                title = "Live Trips\nMonitor",
                modifier = Modifier.width(180.dp),
                onClick = { /* Handle live trips */ }
            )
        }
    }
}

@Composable
private fun ActionTile(
    icon: Int,
    title: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .height(110.dp)
            .shadow(
                elevation = 4.dp,
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
            ) { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.28f)
        ),
        border = BorderStroke(
            2.dp,
            Color.White.copy(alpha = 0.55f)
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
                        Color(0xFF7DD3C0).copy(alpha = 0.15f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = title,
                    tint = Color(0xFF7DD3C0),
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
