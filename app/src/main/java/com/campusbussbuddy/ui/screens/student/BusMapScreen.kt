package com.campusbussbuddy.ui.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.campusbussbuddy.ui.components.*
import com.campusbussbuddy.ui.theme.*

@Composable
fun BusMapScreen(
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
                    title = "Bus Map",
                    onNavigationClick = { navController.popBackStack() }
                )
            }
            
            item {
                // Map Placeholder
                AppCard {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(
                                color = AppColors.SurfaceVariant,
                                shape = RoundedCornerShape(AppRadius.M)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Map",
                                modifier = Modifier.size(48.dp),
                                tint = AppColors.OnSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(AppSpacing.S))
                            Text(
                                text = "Live Campus Map",
                                fontSize = AppTypography.HeadlineSmall,
                                fontWeight = FontWeight.Medium,
                                color = AppColors.OnSurface
                            )
                            Text(
                                text = "Real-time bus tracking",
                                fontSize = AppTypography.BodyMedium,
                                color = AppColors.OnSurfaceVariant
                            )
                        }
                    }
                }
            }
            
            item {
                // Active Buses
                AppCard {
                    SectionHeader(
                        title = "Active Buses",
                        subtitle = "Currently running on campus",
                        modifier = Modifier.padding(bottom = AppSpacing.M)
                    )
                    
                    Column(
                        verticalArrangement = Arrangement.spacedBy(AppSpacing.S)
                    ) {
                        BusItem(
                            busNumber = "247",
                            route = "Main Campus Loop",
                            eta = "5 min",
                            occupancy = 85,
                            isActive = true
                        )
                        
                        BusItem(
                            busNumber = "156",
                            route = "Hostel Route",
                            eta = "12 min",
                            occupancy = 60,
                            isActive = true
                        )
                        
                        BusItem(
                            busNumber = "089",
                            route = "Engineering Block",
                            eta = "18 min",
                            occupancy = 40,
                            isActive = false
                        )
                    }
                }
            }
            
            item {
                // Nearby Stops
                AppCard {
                    SectionHeader(
                        title = "Nearby Stops",
                        modifier = Modifier.padding(bottom = AppSpacing.M)
                    )
                    
                    Column(
                        verticalArrangement = Arrangement.spacedBy(AppSpacing.S)
                    ) {
                        StopItem(
                            name = "Main Gate",
                            distance = "50m",
                            nextBus = "3 min"
                        )
                        
                        StopItem(
                            name = "Library",
                            distance = "200m",
                            nextBus = "8 min"
                        )
                        
                        StopItem(
                            name = "Cafeteria",
                            distance = "350m",
                            nextBus = "15 min"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BusItem(
    busNumber: String,
    route: String,
    eta: String,
    occupancy: Int,
    isActive: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = AppSpacing.XS),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = if (isActive) AppColors.Primary else AppColors.SurfaceVariant,
                    shape = RoundedCornerShape(AppRadius.S)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Build,
                contentDescription = "Bus",
                tint = if (isActive) AppColors.OnPrimary else AppColors.OnSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(AppSpacing.M))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Bus #$busNumber",
                fontSize = AppTypography.BodyLarge,
                fontWeight = FontWeight.Medium,
                color = AppColors.OnSurface
            )
            Text(
                text = route,
                fontSize = AppTypography.BodySmall,
                color = AppColors.OnSurfaceVariant
            )
        }
        
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = eta,
                fontSize = AppTypography.BodyMedium,
                fontWeight = FontWeight.Medium,
                color = if (isActive) AppColors.Primary else AppColors.OnSurfaceVariant
            )
            Text(
                text = "$occupancy% full",
                fontSize = AppTypography.BodySmall,
                color = AppColors.OnSurfaceVariant
            )
        }
    }
}

@Composable
private fun StopItem(
    name: String,
    distance: String,
    nextBus: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = AppSpacing.XS),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = "Stop",
            tint = AppColors.Primary,
            modifier = Modifier.size(20.dp)
        )
        
        Spacer(modifier = Modifier.width(AppSpacing.M))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                fontSize = AppTypography.BodyLarge,
                fontWeight = FontWeight.Medium,
                color = AppColors.OnSurface
            )
            Text(
                text = "$distance away",
                fontSize = AppTypography.BodySmall,
                color = AppColors.OnSurfaceVariant
            )
        }
        
        Text(
            text = nextBus,
            fontSize = AppTypography.BodyMedium,
            fontWeight = FontWeight.Medium,
            color = AppColors.Primary
        )
    }
}