package com.campusbussbuddy.ui.screens.driver

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.campusbussbuddy.ui.components.*
import com.campusbussbuddy.ui.navigation.Destinations
import com.campusbussbuddy.ui.theme.*

@Composable
fun DriverPortalScreen(
    navController: NavHostController
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(AppSpacing.M),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.M)
        ) {
            item {
                AppTopBar(
                    title = "Driver Portal",
                    actions = {
                        IconButton(
                            onClick = { navController.navigate(Destinations.AUTH) }
                        ) {
                            Text("⚙️", fontSize = 20.sp)
                        }
                    }
                )
            }
            
            item {
                SectionHeader(
                    title = "Welcome back, Mike!",
                    subtitle = "Ready to start your shift?"
                )
            }
            
            item {
                // Action Cards
                AppCard {
                    SectionHeader(
                        title = "Portal Actions",
                        modifier = Modifier.padding(bottom = AppSpacing.M)
                    )
                    
                    Column(
                        verticalArrangement = Arrangement.spacedBy(AppSpacing.S)
                    ) {
                        ListItem(
                            title = "My Profile",
                            subtitle = "View and edit your information",
                            leadingIcon = Icons.Default.Person,
                            onClick = { /* TODO: Navigate to driver profile */ }
                        )
                        
                        ListItem(
                            title = "Bus Login",
                            subtitle = "Access your assigned bus",
                            leadingIcon = Icons.Default.Build,
                            onClick = { navController.navigate(Destinations.BUS_LOGIN) }
                        )
                    }
                }
            }
        }
    }
}