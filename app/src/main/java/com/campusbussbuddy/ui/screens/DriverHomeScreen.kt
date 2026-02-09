package com.campusbussbuddy.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
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
import kotlinx.coroutines.launch

@Composable
fun DriverHomeScreen(
    onLogoutClick: () -> Unit = {}
) {
    var driverInfo by remember { mutableStateOf<DriverInfo?>(null) }
    var busInfo by remember { mutableStateOf<BusInfo?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    
    val scope = rememberCoroutineScope()
    
    // Load driver data on screen launch
    LaunchedEffect(Unit) {
        scope.launch {
            Log.d("DriverHomeScreen", "Loading driver info...")
            driverInfo = FirebaseManager.getCurrentDriverInfo()
            Log.d("DriverHomeScreen", "Driver info loaded: ${driverInfo?.name}")
            Log.d("DriverHomeScreen", "Photo URL: '${driverInfo?.photoUrl}'")
            
            if (driverInfo?.assignedBusId?.isNotEmpty() == true) {
                busInfo = FirebaseManager.getBusInfo(driverInfo!!.assignedBusId)
                Log.d("DriverHomeScreen", "Bus info loaded: Bus ${busInfo?.busNumber}")
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
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Logout Button (Top-Left)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    IconButton(
                        onClick = onLogoutClick,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back),
                            contentDescription = "Logout",
                            tint = Color(0xFF888888),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                
                // Main Content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = Modifier.height(40.dp))
                    
                    // Driver Profile Section
                    DriverProfileSection(driverInfo, busInfo)
                    
                    Spacer(modifier = Modifier.height(60.dp))
                    
                    // Bus Login Button
                    BusLoginButton()
                    
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun DriverProfileSection(
    driverInfo: DriverInfo?,
    busInfo: BusInfo?
) {
    val context = LocalContext.current
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Driver Profile Image with proper loading and error states
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color(0xFF2E4A5A))
                .border(3.dp, Color.White.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            val photoUrl = driverInfo?.photoUrl?.trim() ?: ""
            
            Log.d("DriverProfileSection", "Rendering photo section")
            Log.d("DriverProfileSection", "Photo URL: '$photoUrl'")
            Log.d("DriverProfileSection", "Is empty: ${photoUrl.isEmpty()}")
            Log.d("DriverProfileSection", "Is blank: ${photoUrl.isBlank()}")
            
            if (photoUrl.isNotEmpty() && photoUrl.isNotBlank()) {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(photoUrl)
                        .crossfade(true)
                        .memoryCacheKey(photoUrl)
                        .diskCacheKey(photoUrl)
                        .listener(
                            onStart = {
                                Log.d("CoilImage", "Loading started for: $photoUrl")
                            },
                            onSuccess = { _, _ ->
                                Log.d("CoilImage", "Loading SUCCESS")
                            },
                            onError = { _, result ->
                                Log.e("CoilImage", "Loading FAILED: ${result.throwable.message}")
                                result.throwable.printStackTrace()
                            }
                        )
                        .build(),
                    contentDescription = "Driver Profile Photo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    loading = {
                        // Show loading indicator while image loads
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(30.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        }
                    },
                    error = {
                        // Show default icon if image fails to load
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_person),
                                contentDescription = "Default Profile",
                                modifier = Modifier.size(60.dp),
                                tint = Color.White
                            )
                        }
                    }
                )
            } else {
                Log.d("DriverProfileSection", "Showing default icon - no photo URL")
                // Show default icon if no photo URL
                Icon(
                    painter = painterResource(id = R.drawable.ic_person),
                    contentDescription = "Default Profile",
                    modifier = Modifier.size(60.dp),
                    tint = Color.White
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Driver Name
        Text(
            text = driverInfo?.name ?: "Driver",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Bus Number Only (Clean UI - No Employee ID, No Caption)
        if (busInfo != null) {
            Text(
                text = "Bus ${busInfo.busNumber}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF7DD3C0)
            )
        }
    }
}

@Composable
private fun BusLoginButton() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(32.dp),
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.08f)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    color = Color(0xFF7DD3C0).copy(alpha = 0.2f),
                    bounded = true
                )
            ) { /* Handle bus login */ },
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.95f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
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
                        painter = painterResource(id = R.drawable.ic_directions_bus_vector),
                        contentDescription = "Bus",
                        tint = Color(0xFF7DD3C0),
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Text(
                    text = "Bus Login",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
            }
            
            Icon(
                painter = painterResource(id = R.drawable.ic_chevron_right),
                contentDescription = "Arrow",
                tint = Color(0xFF888888),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
