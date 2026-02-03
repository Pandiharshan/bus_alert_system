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
fun QrScannerScreen(
    navController: NavHostController
) {
    var flashEnabled by remember { mutableStateOf(false) }
    
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
                    title = "QR Scanner",
                    onNavigationClick = { navController.popBackStack() },
                    actions = {
                        IconButton(
                            onClick = { flashEnabled = !flashEnabled }
                        ) {
                            Icon(
                                imageVector = if (flashEnabled) Icons.Default.Star else Icons.Default.Settings,
                                contentDescription = "Flash",
                                tint = if (flashEnabled) AppColors.Warning else AppColors.OnSurface
                            )
                        }
                    }
                )
            }
            
            item {
                // Scanner Area
                AppCard {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
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
                                imageVector = Icons.Default.Settings,
                                contentDescription = "QR Scanner",
                                modifier = Modifier.size(80.dp),
                                tint = AppColors.OnSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(AppSpacing.M))
                            Text(
                                text = "Point camera at QR code",
                                fontSize = AppTypography.HeadlineSmall,
                                fontWeight = FontWeight.Medium,
                                color = AppColors.OnSurface
                            )
                            Text(
                                text = "Scan to board the bus",
                                fontSize = AppTypography.BodyMedium,
                                color = AppColors.OnSurfaceVariant
                            )
                        }
                    }
                }
            }
            
            item {
                // Instructions
                AppCard {
                    SectionHeader(
                        title = "How to scan",
                        modifier = Modifier.padding(bottom = AppSpacing.M)
                    )
                    
                    Column(
                        verticalArrangement = Arrangement.spacedBy(AppSpacing.M)
                    ) {
                        InstructionStep(
                            step = "1",
                            title = "Find the QR code",
                            description = "Look for the QR code displayed in the bus"
                        )
                        
                        InstructionStep(
                            step = "2",
                            title = "Point your camera",
                            description = "Align the QR code within the scanner frame"
                        )
                        
                        InstructionStep(
                            step = "3",
                            title = "Wait for confirmation",
                            description = "Your attendance will be marked automatically"
                        )
                    }
                }
            }
            
            item {
                // Recent Scans
                AppCard {
                    SectionHeader(
                        title = "Recent Scans",
                        modifier = Modifier.padding(bottom = AppSpacing.M)
                    )
                    
                    Column(
                        verticalArrangement = Arrangement.spacedBy(AppSpacing.S)
                    ) {
                        ScanHistoryItem(
                            busNumber = "247",
                            time = "Today, 8:15 AM",
                            status = "Success"
                        )
                        
                        ScanHistoryItem(
                            busNumber = "156",
                            time = "Yesterday, 5:30 PM",
                            status = "Success"
                        )
                        
                        ScanHistoryItem(
                            busNumber = "089",
                            time = "2 days ago, 9:00 AM",
                            status = "Failed"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InstructionStep(
    step: String,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(
                    color = AppColors.Primary,
                    shape = RoundedCornerShape(AppRadius.M)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = step,
                fontSize = AppTypography.LabelMedium,
                fontWeight = FontWeight.Bold,
                color = AppColors.OnPrimary
            )
        }
        
        Spacer(modifier = Modifier.width(AppSpacing.M))
        
        Column {
            Text(
                text = title,
                fontSize = AppTypography.BodyLarge,
                fontWeight = FontWeight.Medium,
                color = AppColors.OnSurface
            )
            Text(
                text = description,
                fontSize = AppTypography.BodyMedium,
                color = AppColors.OnSurfaceVariant
            )
        }
    }
}

@Composable
private fun ScanHistoryItem(
    busNumber: String,
    time: String,
    status: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = AppSpacing.XS),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (status == "Success") Icons.Default.CheckCircle else Icons.Default.Settings,
            contentDescription = status,
            tint = if (status == "Success") AppColors.Success else AppColors.Error,
            modifier = Modifier.size(20.dp)
        )
        
        Spacer(modifier = Modifier.width(AppSpacing.M))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Bus #$busNumber",
                fontSize = AppTypography.BodyLarge,
                fontWeight = FontWeight.Medium,
                color = AppColors.OnSurface
            )
            Text(
                text = time,
                fontSize = AppTypography.BodySmall,
                color = AppColors.OnSurfaceVariant
            )
        }
        
        Surface(
            color = if (status == "Success") AppColors.SuccessContainer else AppColors.ErrorContainer,
            shape = RoundedCornerShape(AppRadius.S)
        ) {
            Text(
                text = status,
                fontSize = AppTypography.LabelSmall,
                color = if (status == "Success") AppColors.Success else AppColors.Error,
                modifier = Modifier.padding(
                    horizontal = AppSpacing.S,
                    vertical = AppSpacing.XS
                )
            )
        }
    }
}