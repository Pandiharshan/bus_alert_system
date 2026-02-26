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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.campusbussbuddy.R
import com.campusbussbuddy.firebase.FirebaseManager
import com.campusbussbuddy.ui.theme.*
import kotlinx.coroutines.launch

// Using Glassmorphism Design Tokens from theme

@Composable
fun AdminHomeScreen(
    onManageDriversClick: () -> Unit = {},
    onManageBusesClick: () -> Unit = {},
    onManageStudentsClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
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
    
    GlassBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar with Back Button and Title
            AdminTopBar(onBackClick = onBackClick)
            
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
                    Spacer(modifier = Modifier.height(40.dp))
                }
                
                item {
                    // Main Action Tiles
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
private fun AdminTopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(Color.Transparent)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Back/Logout button on the left
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_chevron_left),
                contentDescription = "Logout",
                tint = Color(0xFF2C3E3E),
                modifier = Modifier.size(32.dp)
            )
        }
        
        // Title centered
        Text(
            text = "ADMIN PORTAL",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A),
            letterSpacing = 1.2.sp
        )
        
        // Empty space for balance (same size as back button)
        Spacer(modifier = Modifier.size(40.dp))
    }
}

@Composable
private fun AdminProfileCard(onLogoutClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = OuterPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Icon Circle - properly sized and centered
        Box(
            modifier = Modifier
                .size(140.dp)  // Larger size for better visibility
                .shadow(
                    elevation = 8.dp,
                    shape = CircleShape,
                    ambientColor = Color.Black.copy(alpha = 0.1f),
                    spotColor = Color.Black.copy(alpha = 0.1f)
                )
                .border(
                    width = 3.dp,
                    color = Color.White.copy(alpha = 0.5f),
                    shape = CircleShape
                )
                .background(Color.White.copy(alpha = 0.25f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_admin_panel),
                contentDescription = "Admin Profile",
                modifier = Modifier.size(70.dp),  // Icon size proportional to circle
                tint = Color(0xFF5A9A8A)
            )
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Admin Name
        Text(
            text = "Pandiharshan",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A),
            letterSpacing = 0.3.sp
        )
    }
}

@Composable
private fun DashboardStatsGrid(
    totalBuses: Int,
    totalStudents: Int,
    totalDrivers: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = OuterPadding),
        horizontalArrangement = Arrangement.spacedBy(InputGap)
    ) {
        StatCard(
            value = totalStudents.toString(),
            label = "STUDENTS",
            icon = R.drawable.ic_person,
            modifier = Modifier.weight(1f)
        )
        
        StatCard(
            value = totalDrivers.toString(),
            label = "DRIVERS",
            icon = R.drawable.ic_group,
            modifier = Modifier.weight(1f)
        )
        
        StatCard(
            value = totalBuses.toString(),
            label = "BUSES",
            icon = R.drawable.ic_directions_bus_vector,
            modifier = Modifier.weight(1f)
        )
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
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(28.dp),  // 🧊 Corner Radius: 28dp
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.08f)
            ),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = GlassCardFill  // 🧊 Card Fill: #FFFFFF @ 22%
        ),
        border = BorderStroke(
            1.dp,  // 🧊 Border Width: 1dp
            GlassCardBorder  // 🧊 Border: #FFFFFF @ 25%
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),  // 📏 Card Inner Padding
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = label,
                tint = BrandTeal,  // 👤 Icon Tint
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))  // 📏 Spacing: 8dp
            
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary  // 🔤 Primary Titles: #111111
            )
            
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary.copy(alpha = 0.70f),  // 🔤 Secondary Text: #444444
                letterSpacing = 0.5.sp
            )
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
                elevation = 4.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color.Black.copy(alpha = 0.06f),
                spotColor = Color.Black.copy(alpha = 0.06f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.22f)
        ),
        border = BorderStroke(
            1.5.dp,
            Color.White.copy(alpha = 0.15f)
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
            .padding(horizontal = OuterPadding),
        verticalArrangement = Arrangement.spacedBy(InputGap)
    ) {
        ActionButton(
            icon = R.drawable.ic_person,
            title = "Driver Management",
            onClick = onManageDriversClick
        )
        
        ActionButton(
            icon = R.drawable.ic_group,
            title = "Student Management",
            onClick = onManageStudentsClick
        )
        
        ActionButton(
            icon = R.drawable.ic_directions_bus_vector,
            title = "Bus Management",
            onClick = onManageBusesClick
        )
    }
}

@Composable
private fun ActionButton(
    icon: Int,
    title: String,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)  // 📏 Management Card Height: 80dp
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(28.dp),  // 🧊 Corner Radius: 28dp
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.08f)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    color = BrandTeal.copy(alpha = 0.2f),
                    bounded = true
                )
            ) { onClick() },
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = GlassCardFill  // 🧊 Card Fill: #FFFFFF @ 22%
        ),
        border = BorderStroke(
            1.5.dp,  // 🧊 Border Width: 1.5dp
            GlassCardBorder  // 🧊 Border: #FFFFFF @ 25%
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),  // 📏 Card Inner Padding: 24dp
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)  // 📏 Input Gap: 16dp
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        BrandTeal.copy(alpha = 0.10f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = title,
                    tint = BrandTeal,  // 👤 Icon Tint
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,  // 🔤 Primary Titles: #111111
                letterSpacing = 0.5.sp
            )
        }
    }
}
