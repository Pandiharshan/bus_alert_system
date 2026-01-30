package com.campusbussbuddy.ui.screens.student

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrScannerScreen(
    navController: NavHostController
) {
    var flashEnabled by remember { mutableStateOf(false) }
    var isScanning by remember { mutableStateOf(false) }
    
    // Animation for the gradient
    val infiniteTransition = rememberInfiniteTransition(label = "gradient")
    val animatedOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
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
                        Color(0xFF667eea).copy(alpha = 0.9f + animatedOffset * 0.1f),
                        Color(0xFF764ba2).copy(alpha = 0.95f),
                        Color.Black.copy(alpha = 0.8f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .statusBarsPadding(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.2f))
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Text(
                    text = "QR Scanner 📱",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                IconButton(
                    onClick = { flashEnabled = !flashEnabled },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = if (flashEnabled) 0.3f else 0.2f))
                ) {
                    Icon(
                        imageVector = if (flashEnabled) Icons.Default.Star else Icons.Default.Add,
                        contentDescription = "Flash",
                        tint = if (flashEnabled) Color.Yellow else Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Camera Viewfinder Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                // Camera Preview Card
                Card(
                    modifier = Modifier
                        .size(300.dp)
                        .clip(RoundedCornerShape(24.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Black.copy(alpha = 0.8f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(
                                        brush = Brush.linearGradient(
                                            colors = listOf(Color(0xFF667eea), Color(0xFF764ba2))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "QR Scanner",
                                    modifier = Modifier.size(40.dp),
                                    tint = Color.White
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            Text(
                                text = "Camera Preview",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            
                            Text(
                                text = "Point camera at QR code",
                                fontSize = 16.sp,
                                color = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
                
                // Scanning Overlay with animated corners
                Box(
                    modifier = Modifier.size(300.dp)
                ) {
                    // Animated corner indicators
                    val cornerSize = 40.dp
                    val cornerThickness = 4.dp
                    val cornerLength = 24.dp
                    
                    // Top-left corner
                    Box(
                        modifier = Modifier
                            .offset(16.dp, 16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(cornerLength, cornerThickness)
                                .background(Color.White)
                        )
                        Box(
                            modifier = Modifier
                                .size(cornerThickness, cornerLength)
                                .background(Color.White)
                        )
                    }
                    
                    // Top-right corner
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset((-16).dp, 16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(cornerLength, cornerThickness)
                                .offset((-cornerLength + cornerThickness), 0.dp)
                                .background(Color.White)
                        )
                        Box(
                            modifier = Modifier
                                .size(cornerThickness, cornerLength)
                                .offset((-cornerThickness), 0.dp)
                                .background(Color.White)
                        )
                    }
                    
                    // Bottom-left corner
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .offset(16.dp, (-16).dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(cornerLength, cornerThickness)
                                .offset(0.dp, (-cornerThickness))
                                .background(Color.White)
                        )
                        Box(
                            modifier = Modifier
                                .size(cornerThickness, cornerLength)
                                .offset(0.dp, (-cornerLength))
                                .background(Color.White)
                        )
                    }
                    
                    // Bottom-right corner
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset((-16).dp, (-16).dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(cornerLength, cornerThickness)
                                .offset((-cornerLength + cornerThickness), (-cornerThickness))
                                .background(Color.White)
                        )
                        Box(
                            modifier = Modifier
                                .size(cornerThickness, cornerLength)
                                .offset((-cornerThickness), (-cornerLength))
                                .background(Color.White)
                        )
                    }
                }
            }
            
            // Instructions Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.95f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "How to Scan 📋",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D3748),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    Text(
                        text = "1. Position QR code within the frame",
                        fontSize = 16.sp,
                        color = Color(0xFF4A5568),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    Text(
                        text = "2. Hold steady for automatic scanning",
                        fontSize = 16.sp,
                        color = Color(0xFF4A5568),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    if (isScanning) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color(0xFF667eea),
                                strokeWidth = 3.dp
                            )
                            
                            Spacer(modifier = Modifier.width(12.dp))
                            
                            Text(
                                text = "Scanning for QR code...",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF667eea)
                            )
                        }
                    } else {
                        Button(
                            onClick = { isScanning = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent
                            ),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(Color(0xFF667eea), Color(0xFF764ba2))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Start Scanning 🚀",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}