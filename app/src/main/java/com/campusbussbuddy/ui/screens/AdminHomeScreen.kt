package com.campusbussbuddy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.campusbussbuddy.R

@Composable
fun AdminHomeScreen(
    onManageDriversClick: () -> Unit = {},
    onManageBusesClick: () -> Unit = {},
    onManageStudentsClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            item {
                // Admin Profile Card
                AdminProfileCard()
            }
            
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            item {
                // Dashboard Stats
                DashboardStatsGrid()
            }
            
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            item {
                // Real-Time Overview
                RealTimeOverviewCard()
            }
            
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            item {
                // Main Action Tiles
                Text(
                    text = "System Management",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }
            
            item {
                MainActionTiles(
                    onManageDriversClick = onManageDriversClick,
                    onManageBusesClick = onManageBusesClick,
                    onManageStudentsClick = onManageStudentsClick
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun AdminProfileCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.08f)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.95f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Admin Profile Image
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF0F0F0))
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.admin),
                    contentDescription = "Admin Profile",
                    modifier = Modifier.fillMaxSize(),
                    tint = Color.Unspecified
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Admin Name
            Text(
                text = "Pandiharshan",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )
            
            // Settings Icon
            IconButton(
                onClick = { /* Handle settings */ },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_settings),
                    contentDescription = "Settings",
                    tint = Color(0xFF888888),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun DashboardStatsGrid() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                value = "24",
                label = "Total Buses",
                icon = R.drawable.ic_directions_bus_vector,
                modifier = Modifier.weight(1f)
            )
            
            StatCard(
                value = "842",
                label = "Total Students",
                icon = R.drawable.ic_person,
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            StatCard(
                value = "756",
                label = "Present Today",
                icon = R.drawable.ic_check_circle,
                modifier = Modifier.width(180.dp)
            )
        }
    }
}

@Composable
private fun StatCard(
    value: String,
    label: String,
    icon: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(100.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black.copy(alpha = 0.06f),
                spotColor = Color.Black.copy(alpha = 0.06f)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.95f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = label,
                tint = Color(0xFF7DD3C0),
                modifier = Modifier.size(24.dp)
            )
            
            Column {
                Text(
                    text = value,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                Text(
                    text = label,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF888888)
                )
            }
        }
    }
}

@Composable
private fun RealTimeOverviewCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.08f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.95f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Live System Status",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
                
                // Status Indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                Color(0xFF4CAF50),
                                CircleShape
                            )
                    )
                    
                    Spacer(modifier = Modifier.width(6.dp))
                    
                    Text(
                        text = "Active",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF4CAF50)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                LiveStatItem(
                    value = "12",
                    label = "Active Trips"
                )
                
                Divider(
                    modifier = Modifier
                        .width(1.dp)
                        .height(40.dp),
                    color = Color(0xFFE0E0E0)
                )
                
                LiveStatItem(
                    value = "12",
                    label = "Buses Running"
                )
                
                Divider(
                    modifier = Modifier
                        .width(1.dp)
                        .height(40.dp),
                    color = Color(0xFFE0E0E0)
                )
                
                LiveStatItem(
                    value = "756",
                    label = "Students Onboard"
                )
            }
        }
    }
}

@Composable
private fun LiveStatItem(
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF888888),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun MainActionTiles(
    onManageDriversClick: () -> Unit = {},
    onManageBusesClick: () -> Unit = {},
    onManageStudentsClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ActionTile(
                icon = R.drawable.ic_directions_bus_vector,
                title = "Manage\nBuses",
                modifier = Modifier.weight(1f),
                onClick = onManageBusesClick
            )
            
            ActionTile(
                icon = R.drawable.ic_group,
                title = "Manage\nDrivers",
                modifier = Modifier.weight(1f),
                onClick = onManageDriversClick
            )
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ActionTile(
                icon = R.drawable.ic_person,
                title = "Manage\nStudents",
                modifier = Modifier.weight(1f),
                onClick = onManageStudentsClick
            )
            
            ActionTile(
                icon = R.drawable.ic_calendar_month,
                title = "Attendance\nOverview",
                modifier = Modifier.weight(1f),
                onClick = { /* Handle attendance */ }
            )
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            ActionTile(
                icon = R.drawable.ic_map,
                title = "Live Trips\nMonitor",
                modifier = Modifier.width(180.dp),
                onClick = { /* Handle live trips */ }
            )
        }
    }
}

@Composable
private fun ActionTile(
    icon: Int,
    title: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .height(110.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black.copy(alpha = 0.06f),
                spotColor = Color.Black.copy(alpha = 0.06f)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    color = Color(0xFF7DD3C0).copy(alpha = 0.2f),
                    bounded = true
                )
            ) { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.95f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
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
                    painter = painterResource(id = icon),
                    contentDescription = title,
                    tint = Color(0xFF7DD3C0),
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                lineHeight = 18.sp
            )
        }
    }
}