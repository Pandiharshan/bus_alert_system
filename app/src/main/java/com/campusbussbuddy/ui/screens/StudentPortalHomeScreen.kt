package com.campusbussbuddy.ui.screens

import android.util.Log
import androidx.compose.foundation.BorderStroke
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
import com.campusbussbuddy.firebase.StudentInfo
import com.campusbussbuddy.firebase.FirebaseManager
import com.campusbussbuddy.ui.theme.GlassBackground
import kotlinx.coroutines.launch

@Composable
fun StudentPortalHomeScreen(
    onLogoutClick: () -> Unit = {}
) {
    var studentInfo by remember { mutableStateOf<StudentInfo?>(null) }
    var busInfo by remember { mutableStateOf<BusInfo?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    
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
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Top Bar with Back Button and "HOME" title
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onLogoutClick,
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
                            text = "HOME",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A1A),
                            letterSpacing = 2.sp
                        )
                        
                        Spacer(modifier = Modifier.weight(1f))
                        
                        // Placeholder for symmetry
                        Spacer(modifier = Modifier.size(40.dp))
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Profile Photo with green dot indicator
                    Box(
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFB8D4D1))
                                .border(4.dp, Color.White.copy(alpha = 0.5f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_person),
                                contentDescription = "Profile",
                                tint = Color(0xFF6B9090),
                                modifier = Modifier.size(55.dp)
                            )
                        }
                        
                        // Green online indicator
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .background(Color(0xFF4CAF50), CircleShape)
                                .border(3.dp, Color.White, CircleShape)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Student Name
                    Text(
                        text = studentInfo?.name ?: "Alex Harrison",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // Bus and Stop Info
                    Text(
                        text = if (busInfo != null) 
                            "Bus ${busInfo!!.busNumber} • Morning Express"
                        else 
                            "Bus 42 • Morning Express",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF4A5F5F)
                    )
                    
                    Spacer(modifier = Modifier.height(2.dp))
                    
                    // Stop Name
                    Text(
                        text = studentInfo?.stop ?: "Central Hub",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF4A5F5F)
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // My Bus Status Card
                    BusStatusCard(busInfo)
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // Action Grid (2x2)
                    ActionGrid()
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Bottom Links
                    BottomLinks()
                    
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
private fun BusStatusCard(busInfo: BusInfo?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 0.dp,
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFD9E8E6)
        ),
        border = BorderStroke(0.dp, Color.Transparent)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_directions_bus_vector),
                        contentDescription = "Bus",
                        tint = Color(0xFF6B9090),
                        modifier = Modifier.size(22.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(10.dp))
                    
                    Text(
                        text = "My Bus Status",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )
                }
                
                // LIVE Badge
                Box(
                    modifier = Modifier
                        .background(
                            Color(0xFF4CAF50),
                            RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "LIVE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Route Info
            Text(
                text = if (busInfo != null) "ROUTE ${busInfo.busNumber} - NORTH CAMPUS" else "ROUTE 42 - NORTH CAMPUS",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6B9090),
                letterSpacing = 0.8.sp
            )
            
            Spacer(modifier = Modifier.height(6.dp))
            
            // Time and Next Bus Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "4 mins",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )
                    
                    Text(
                        text = "Arriving at Central Hub",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF4A5F5F)
                    )
                }
                
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "NEXT BUS",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6B9090),
                        letterSpacing = 0.8.sp
                    )
                    
                    Text(
                        text = "16:45",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Track on Map Button
            Button(
                onClick = { /* Handle track on map */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(22.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8AAFA8),
                    contentColor = Color.White
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_map),
                    contentDescription = "Map",
                    modifier = Modifier.size(18.dp)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "Track on Map",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun ActionGrid() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            ActionCard(
                icon = R.drawable.ic_qr_code,
                label = "QR SCANNER",
                modifier = Modifier.weight(1f)
            )
            
            ActionCard(
                icon = R.drawable.ic_person,
                label = "PROFILE",
                modifier = Modifier.weight(1f)
            )
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            ActionCard(
                icon = R.drawable.ic_calendar_today,
                label = "ABSENT CALENDAR",
                modifier = Modifier.weight(1f)
            )
            
            ActionCard(
                icon = R.drawable.ic_map,
                label = "LIVE MAP",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ActionCard(
    icon: Int,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(110.dp)
            .shadow(
                elevation = 0.dp,
                shape = RoundedCornerShape(18.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    color = Color(0xFF6B9A92).copy(alpha = 0.2f),
                    bounded = true
                )
            ) { /* Handle action */ },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFD9E8E6)
        ),
        border = BorderStroke(0.dp, Color.Transparent)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = label,
                tint = Color(0xFF2A2A2A),
                modifier = Modifier.size(30.dp)
            )
            
            Spacer(modifier = Modifier.height(10.dp))
            
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2A2A2A),
                textAlign = TextAlign.Center,
                letterSpacing = 0.4.sp,
                lineHeight = 12.sp
            )
        }
    }
}

@Composable
private fun BottomLinks() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        BottomLink("HELP")
        BottomLink("SETTINGS")
        BottomLink("LOGOUT", color = Color(0xFFE53935))
    }
}

@Composable
private fun BottomLink(
    text: String,
    color: Color = Color(0xFF6B9090)
) {
    Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = color,
        letterSpacing = 1.sp,
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    color = color.copy(alpha = 0.2f),
                    bounded = true
                )
            ) { /* Handle click */ }
            .padding(8.dp)
    )
}
