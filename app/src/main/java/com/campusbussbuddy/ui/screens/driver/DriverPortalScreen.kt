package com.campusbussbuddy.ui.screens.driver

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Build
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.campusbussbuddy.ui.components.*
import com.campusbussbuddy.ui.navigation.Destinations

@Composable
fun DriverPortalScreen(
    navController: NavHostController
) {
    AnimatedGradientBackground(
        colors = listOf(
            Color(0xFF667eea),
            Color(0xFF764ba2),
            Color(0xFF667eea)
        )
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                SectionHeader(
                    title = "🚌 Driver Portal",
                    subtitle = "Welcome back, Mike!"
                )
            }
            
            item {
                // Action Cards
                AppCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    androidx.compose.material3.Text(
                        text = "Portal Actions",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D3748),
                        modifier = Modifier.padding(bottom = 20.dp)
                    )
                    
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ActionItem(
                            title = "My Profile",
                            subtitle = "View and edit your information",
                            icon = Icons.Default.Person,
                            gradient = listOf(Color(0xFF667eea), Color(0xFF764ba2)),
                            onClick = { /* TODO: Navigate to driver profile */ }
                        )
                        
                        ActionItem(
                            title = "Bus Login",
                            subtitle = "Access your assigned bus",
                            icon = Icons.Default.Build,
                            gradient = listOf(Color(0xFF4facfe), Color(0xFF00f2fe)),
                            onClick = { navController.navigate(Destinations.BUS_LOGIN) }
                        )
                    }
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(32.dp))
                
                // Sign Out Button
                androidx.compose.material3.OutlinedButton(
                    onClick = { navController.navigate(Destinations.AUTH) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(16.dp)),
                    colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    )
                ) {
                    androidx.compose.material3.Text(
                        text = "← Sign Out",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}