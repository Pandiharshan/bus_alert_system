package com.campusbussbuddy.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
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
import com.campusbussbuddy.ui.theme.*
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
    
    GlassBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar with Back Button and Title
            DriverTopBar(onBackClick = onLogoutClick)
            
            if (isLoading) {
                // Loading State
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = BrandTeal)
                }
            } else {
                // Main Content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Spacer(modifier = Modifier.height(40.dp))
                    
                    // Driver Profile Section
                    DriverProfileSection(driverInfo, busInfo)
                    
                    Spacer(modifier = Modifier.height(60.dp))
                    
                    // Bus Login Button
                    BusLoginButton(onClick = onBusLoginClick)
                    
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun DriverTopBar(onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color.Transparent)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        // Back button
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_back),
                contentDescription = "Back",
                tint = TextPrimary,
                modifier = Modifier.size(24.dp)
            )
        }
        
        // Title centered
        Text(
            text = "DRIVER PORTAL",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            letterSpacing = 1.5.sp,
            modifier = Modifier.align(Alignment.Center)
        )
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
        // Driver Profile Image with glass effect - Match AdminHomeScreen style
        Box(
            modifier = Modifier
                .size(AvatarSize)  // 120dp
                .shadow(
                    elevation = 8.dp,
                    shape = CircleShape,
                    ambientColor = Color.Black.copy(alpha = 0.1f),
                    spotColor = Color.Black.copy(alpha = 0.1f)
                )
                .border(
                    width = 2.dp,
                    color = AvatarBorder,  // White with 30% alpha
                    shape = CircleShape
                )
                .background(GlassCardFill, CircleShape),  // Glass card fill
            contentAlignment = Alignment.Center
        ) {
            val photoUrl = driverInfo?.photoUrl?.trim() ?: ""
            
            Log.d("DriverProfileSection", "Rendering photo section")
            Log.d("DriverProfileSection", "Photo URL: '$photoUrl'")
            
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
                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    loading = {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(30.dp),
                                color = BrandTeal,
                                strokeWidth = 2.dp
                            )
                        }
                    },
                    error = {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_person),
                                contentDescription = "Default Profile",
                                modifier = Modifier.size(60.dp),
                                tint = BrandTeal  // Match unified login icon color
                            )
                        }
                    }
                )
            } else {
                Log.d("DriverProfileSection", "Showing default icon - no photo URL")
                Icon(
                    painter = painterResource(id = R.drawable.ic_person),
                    contentDescription = "Default Profile",
                    modifier = Modifier.size(60.dp),
                    tint = BrandTeal  // Match unified login icon color
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Driver Name
        Text(
            text = driverInfo?.name ?: "Driver",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,  // Match theme color
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Bus Number with glass card style
        if (busInfo != null) {
            Text(
                text = "Bus ${busInfo.busNumber}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = BrandTeal  // Match theme color
            )
        }
    }
}

@Composable
private fun BusLoginButton(onClick: () -> Unit = {}) {
    GlassButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_directions_bus_vector),
                contentDescription = "Bus",
                tint = TextButton,
                modifier = Modifier.size(20.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Text(
                text = "Bus Login",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = TextButton
            )
        }
    }
}
