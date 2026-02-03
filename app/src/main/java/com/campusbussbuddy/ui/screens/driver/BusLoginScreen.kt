@file:OptIn(ExperimentalMaterial3Api::class)

package com.campusbussbuddy.ui.screens.driver

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
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
fun BusLoginScreen(
    navController: NavHostController
) {
    val primary = AppColors.Primary
    val backgroundDark = AppColors.BackgroundDark
    val cardBackground = AppColors.CardBackground
    val borderColor = AppColors.Border
    
    var busNumber by remember { mutableStateOf("") }
    var busPassword by remember { mutableStateOf("") }
    
    // Animations
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAnimation by infiniteTransition.animateFloat(
        initialValue = 0.75f,
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
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_back_vector),
                    contentDescription = "Back",
                    modifier = Modifier.size(24.dp),
                    tint = AppColors.OnBackground
                )
            }
            
            Text(
                text = "Bus Login",
                color = AppColors.OnBackground,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.width(48.dp))
        }
        
        // Header Illustration Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF102217).copy(alpha = 0.4f),
                            Color(0xFF102217).copy(alpha = 0.9f)
                        )
                    )
                )
                .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Welcome Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Start Tracking",
                color = AppColors.OnBackground,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Enter vehicle credentials to sync this device with the fleet management system.",
                color = AppColors.OnSurfaceVariant,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Login Form Fields
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Bus Number Field
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "BUS NUMBER",
                    color = AppColors.OnBackground,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.sp
                )
                
                OutlinedTextField(
                    value = busNumber,
                    onValueChange = { busNumber = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "e.g., B-204",
                            color = AppColors.OnSurfaceVariant
                        )
                    },
                    trailingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_directions_bus_vector),
                            contentDescription = "Bus",
                            modifier = Modifier.size(20.dp),
                            tint = Color(0xFF9CBAA8)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primary,
                        unfocusedBorderColor = borderColor,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = primary,
                        focusedContainerColor = cardBackground,
                        unfocusedContainerColor = cardBackground
                    ),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )
            }
            
            // Bus Password Field
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "BUS PASSWORD",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.sp
                )
                
                OutlinedTextField(
                    value = busPassword,
                    onValueChange = { busPassword = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "••••••••",
                            color = Color(0xFF9CBAA8)
                        )
                    },
                    trailingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_settings),
                            contentDescription = "Lock",
                            modifier = Modifier.size(20.dp),
                            tint = Color(0xFF9CBAA8)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primary,
                        unfocusedBorderColor = borderColor,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = primary,
                        focusedContainerColor = cardBackground,
                        unfocusedContainerColor = cardBackground
                    ),
                    shape = RoundedCornerShape(8.dp),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true
                )
            }
        }
        
        Spacer(modifier = Modifier.height(40.dp))
        
        // Login Button
        Button(
            onClick = { navController.navigate(Destinations.DRIVER_BUS_HOME) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = primary,
                contentColor = backgroundDark
            ),
            shape = RoundedCornerShape(12.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 8.dp,
                pressedElevation = 4.dp
            )
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Login to Bus",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_chevron_right),
                    contentDescription = "Login",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Secondary Actions
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextButton(onClick = { }) {
                Text(
                    text = "Forgot Bus Credentials?",
                    color = Color(0xFF9CBAA8),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            
            // GPS Status Indicator
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .background(cardBackground, RoundedCornerShape(20.dp))
                    .border(1.dp, borderColor, RoundedCornerShape(20.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(primary, androidx.compose.foundation.shape.CircleShape)
                        .then(
                            if (pulseAnimation > 0.9f) Modifier.background(
                                primary.copy(alpha = 0.5f),
                                androidx.compose.foundation.shape.CircleShape
                            ) else Modifier
                        )
                )
                Text(
                    text = "GPS Ready • System Version 2.4.0",
                    color = Color(0xFF9CBAA8),
                    fontSize = 12.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Support Footer
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "SmartBus Fleet Management System © 2024",
                color = Color(0xFF5C7A68),
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Need technical support? Contact Fleet Hub",
                color = Color(0xFF5C7A68),
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}