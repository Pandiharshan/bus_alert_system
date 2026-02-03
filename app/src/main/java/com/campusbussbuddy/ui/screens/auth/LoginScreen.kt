@file:OptIn(ExperimentalMaterial3Api::class)

package com.campusbussbuddy.ui.screens.auth

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.res.painterResource
import com.campusbussbuddy.R
import com.campusbussbuddy.ui.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToStudent: () -> Unit,
    onNavigateToDriver: () -> Unit
) {
    // Animations
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "pulse"
    )
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.BackgroundDark)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))
        
        // Header with Help Icon
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(24.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_directions_bus_vector),
                    contentDescription = "SmartBus",
                    modifier = Modifier.size(24.dp),
                    tint = AppColors.Primary
                )
                Text(
                    text = "SmartBus",
                    color = AppColors.OnBackground,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            IconButton(
                onClick = { /* Help action */ },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_notifications),
                    contentDescription = "Help",
                    modifier = Modifier.size(20.dp),
                    tint = AppColors.OnSurfaceVariant
                )
            }
        }
        
        Spacer(modifier = Modifier.height(80.dp))
        
        // Bus Icon Illustration
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(
                    AppColors.Surface,
                    RoundedCornerShape(16.dp)
                )
                .border(
                    1.dp,
                    AppColors.Outline,
                    RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_directions_bus_vector),
                contentDescription = "Bus",
                modifier = Modifier
                    .size(80.dp)
                    .scale(pulseScale),
                tint = AppColors.Primary
            )
        }
        
        Spacer(modifier = Modifier.height(40.dp))
        
        // Title and Subtitle
        Text(
            text = "Track Your Journey",
            color = AppColors.OnBackground,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = "Seamless bus attendance and real-time\ntracking for a safer commute.",
            color = AppColors.OnSurfaceVariant,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
        
        Spacer(modifier = Modifier.height(60.dp))
        
        // Student Login Button
        Button(
            onClick = onNavigateToStudent,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AppColors.Primary,
                contentColor = AppColors.OnPrimary
            ),
            shape = RoundedCornerShape(12.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp,
                pressedElevation = 2.dp
            )
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_person),
                    contentDescription = "Student",
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Student Login",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_chevron_right),
                    contentDescription = "Arrow",
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Driver Login Button
        OutlinedButton(
            onClick = onNavigateToDriver,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = AppColors.OnBackground
            ),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                brush = Brush.horizontalGradient(
                    colors = listOf(AppColors.Outline, AppColors.Outline)
                )
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_settings),
                    contentDescription = "Driver",
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Driver Login",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_chevron_right),
                    contentDescription = "Arrow",
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Register Link
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "New to the platform?",
                color = AppColors.OnSurfaceVariant,
                fontSize = 14.sp
            )
            
            TextButton(
                onClick = onNavigateToRegister
            ) {
                Text(
                    text = "Register Your Institution",
                    color = AppColors.Primary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_share),
                    contentDescription = "External",
                    modifier = Modifier
                        .size(16.dp)
                        .padding(start = 4.dp),
                    tint = AppColors.Primary
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Footer
        Text(
            text = "POWERED BY SMARTBUS ANALYTICS V2.4",
            color = AppColors.TextTertiary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 1.sp
        )
    }
}