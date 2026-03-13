package com.campusbussbuddy.ui.screens

import android.util.Log
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.campusbussbuddy.R
import com.campusbussbuddy.firebase.BusInfo
import com.campusbussbuddy.firebase.DriverInfo
import com.campusbussbuddy.firebase.FirebaseManager
import com.campusbussbuddy.ui.theme.GlassBackground
import kotlinx.coroutines.launch

@Composable
fun DriverHomeScreen(
    onBusLoginClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    var driverInfo by remember { mutableStateOf<DriverInfo?>(null) }
    var busInfo by remember { mutableStateOf<BusInfo?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    
    // Load driver data on screen launch
    LaunchedEffect(Unit) {
        scope.launch {
            Log.d("DriverHomeScreen", "Loading driver info...")
            driverInfo = FirebaseManager.getCurrentDriverInfo()
            Log.d("DriverHomeScreen", "Driver info loaded: ${driverInfo?.name}")
            
            if (driverInfo?.assignedBusId?.isNotEmpty() == true) {
                busInfo = FirebaseManager.getBusInfo(driverInfo!!.assignedBusId)
                Log.d("DriverHomeScreen", "Bus info loaded: Bus ${busInfo?.busNumber}")
            }
            isLoading = false
        }
    }
    
    GlassBackground {
        Box(modifier = Modifier.fillMaxSize()) {
            if (isLoading) {
                // Loading State
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFF6B9A92)
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Top Label Pill - "CAMPUS TRANSPORT SYSTEM"
                    TopLabelPill()
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Profile Photo
                    DriverProfilePhoto(driverInfo)
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // Driver Portal Title
                    Text(
                        text = "Driver Portal",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A),
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // Shift Status
                    Text(
                        text = "SHIFT ACTIVE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4A5F5F),
                        letterSpacing = 1.5.sp,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Assigned Bus Card
                    AssignedBusCard(busInfo)
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Bus Login Button
                    BusLoginButton(onClick = onBusLoginClick)
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Bottom Navigation Bar
                    BottomNavigationBar()
                    
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun TopLabelPill() {
    Row(
        modifier = Modifier
            .shadow(elevation = 0.dp, shape = RoundedCornerShape(50))
            .background(
                Color.White.copy(alpha = 0.55f),
                RoundedCornerShape(50)
            )
            .border(
                1.dp,
                Color.White.copy(alpha = 0.7f),
                RoundedCornerShape(50)
            )
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_directions_bus_vector),
            contentDescription = null,
            tint = Color(0xFF2d6464),
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = "CAMPUS TRANSPORT SYSTEM",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A),
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
private fun DriverProfilePhoto(driverInfo: DriverInfo?) {
    val context = LocalContext.current
    
    Box(
        modifier = Modifier
            .size(140.dp)
            .shadow(
                elevation = 8.dp,
                shape = CircleShape,
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.08f)
            )
            .background(
                Color.White.copy(alpha = 0.22f),
                CircleShape
            )
            .border(
                1.5.dp,
                Color.White.copy(alpha = 0.50f),
                CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        val photoUrl = driverInfo?.photoUrl?.trim() ?: ""
        
        if (photoUrl.isNotEmpty() && photoUrl.isNotBlank()) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(context)
                    .data(photoUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Driver Profile Photo",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                loading = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(30.dp),
                            color = Color(0xFF6B9A92),
                            strokeWidth = 2.dp
                        )
                    }
                },
                error = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_person),
                        contentDescription = "Default Profile",
                        modifier = Modifier.size(70.dp),
                        tint = Color(0xFF6B9A92)
                    )
                }
            )
        } else {
            Icon(
                painter = painterResource(id = R.drawable.ic_person),
                contentDescription = "Default Profile",
                modifier = Modifier.size(70.dp),
                tint = Color(0xFF6B9A92)
            )
        }
    }
}

@Composable
private fun AssignedBusCard(busInfo: BusInfo?) {
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
            containerColor = Color.White.copy(alpha = 0.22f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ASSIGNED BUS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4A5F5F),
                        letterSpacing = 1.sp
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = if (busInfo != null) "Route ${busInfo.busNumber}" else "Route 42-B",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )
                }
                
                // Bus Icon Circle
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            Color.White.copy(alpha = 0.6f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_directions_bus_vector),
                        contentDescription = "Bus",
                        tint = Color(0xFF2A2A2A),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Next Stop
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_pin_drop),
                    contentDescription = "Location",
                    tint = Color(0xFF4A5F5F),
                    modifier = Modifier.size(20.dp)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Column {
                    Text(
                        text = "NEXT STOP",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4A5F5F),
                        letterSpacing = 1.sp
                    )
                    
                    Text(
                        text = "North Campus Terminal",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1A1A1A)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Departure Time
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_history),
                    contentDescription = "Time",
                    tint = Color(0xFF4A5F5F),
                    modifier = Modifier.size(20.dp)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Column {
                    Text(
                        text = "DEPARTURE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4A5F5F),
                        letterSpacing = 1.sp
                    )
                    
                    Text(
                        text = "14:30 PM (In 12 mins)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1A1A1A)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Alert Message Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color.White.copy(alpha = 0.4f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(12.dp)
            ) {
                Text(
                    text = "\"Road closure on East Ave. Please use University Blvd detour for the next 2 hours.\"",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF2A2A2A),
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun BusLoginButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(
                elevation = 4.dp,
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
                painter = painterResource(id = R.drawable.ic_directions_bus_vector),
                contentDescription = "Bus Login",
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Text(
                text = "Bus Login",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun BottomNavigationBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(28.dp),
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.08f)
            )
            .background(
                Color.White.copy(alpha = 0.22f),
                RoundedCornerShape(28.dp)
            )
            .border(
                1.dp,
                Color.White.copy(alpha = 0.30f),
                RoundedCornerShape(28.dp)
            )
            .padding(vertical = 16.dp, horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomNavItem(
            icon = R.drawable.ic_home,
            label = "HOME",
            onClick = { }
        )
        
        BottomNavItem(
            icon = R.drawable.ic_map,
            label = "MAP",
            onClick = { }
        )
        
        BottomNavItem(
            icon = R.drawable.ic_notifications,
            label = "ALERTS",
            onClick = { }
        )
        
        BottomNavItem(
            icon = R.drawable.ic_settings,
            label = "SETTINGS",
            onClick = { }
        )
    }
}

@Composable
private fun BottomNavItem(
    icon: Int,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    bounded = false,
                    color = Color(0xFF6B9A92).copy(alpha = 0.2f)
                )
            ) { onClick() }
            .padding(8.dp)
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = label,
            tint = Color(0xFF2A2A2A),
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2A2A2A),
            letterSpacing = 0.5.sp
        )
    }
}
