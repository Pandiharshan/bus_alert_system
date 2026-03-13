package com.campusbussbuddy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.campusbussbuddy.R
import com.campusbussbuddy.ui.theme.GlassBackground

data class StudentMember(
    val id: String,
    val name: String,
    val initials: String,
    val status: StudentStatus,
    val phoneNumber: String = ""
)

enum class StudentStatus {
    WAITING_TO_BOARD,
    BOARDED
}

@Composable
fun BusStopDetailsScreen(
    stopName: String = "Main Gate Terminal",
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {}
) {
    // Sample data - replace with actual data from Firebase
    val students = remember {
        listOf(
            StudentMember("1", "Jonathan Smith", "JS", StudentStatus.WAITING_TO_BOARD, "+1234567890"),
            StudentMember("2", "Emily Anderson", "EA", StudentStatus.BOARDED, "+1234567891"),
            StudentMember("3", "Michael Kim", "MK", StudentStatus.WAITING_TO_BOARD, "+1234567892"),
            StudentMember("4", "Sarah Roberts", "SR", StudentStatus.WAITING_TO_BOARD, "+1234567893"),
            StudentMember("5", "David Wilson", "DW", StudentStatus.BOARDED, "+1234567894"),
            StudentMember("6", "Lisa Chen", "LC", StudentStatus.WAITING_TO_BOARD, "+1234567895"),
            StudentMember("7", "James Brown", "JB", StudentStatus.BOARDED, "+1234567896"),
            StudentMember("8", "Anna Martinez", "AM", StudentStatus.WAITING_TO_BOARD, "+1234567897")
        )
    }
    
    val totalStudents = students.size
    
    GlassBackground {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Top Bar
                BusStopDetailsTopBar(onBackClick = onBackClick)
                
                // Main Content with LazyColumn
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f), // Take remaining space above footer
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(
                        start = 20.dp,
                        end = 20.dp,
                        top = 16.dp,
                        bottom = 90.dp // Add bottom padding to prevent overlap with footer
                    )
                ) {
                    item {
                        // Students Header
                        BusStopDetailsStudentsHeader(totalCount = totalStudents)
                    }
                    
                    items(students) { student ->
                        BusStopDetailsStudentMemberCard(
                            student = student,
                            onCallClick = { /* TODO: Handle call */ }
                        )
                    }
                }
            }
            
            // Fixed Bottom Navigation - Always visible at bottom
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color.Transparent) // Ensure no background interference
            ) {
                BusStopDetailsBottomNavigationBar(onHomeClick = onHomeClick)
            }
        }
    }
}

@Composable
private fun BusStopDetailsTopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(Color.Transparent)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back Button
        Box(
            modifier = Modifier
                .size(48.dp)
                .shadow(
                    elevation = 4.dp,
                    shape = CircleShape,
                    ambientColor = Color.Black.copy(alpha = 0.08f),
                    spotColor = Color.Black.copy(alpha = 0.08f)
                )
                .background(
                    Color.White.copy(alpha = 0.25f),
                    CircleShape
                )
                .border(
                    1.dp,
                    Color.White.copy(alpha = 0.30f),
                    CircleShape
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(
                        bounded = true,
                        color = Color.White.copy(0.20f)
                    )
                ) { onBackClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_chevron_left),
                contentDescription = "Back",
                tint = Color(0xFF2A2A2A),
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Text(
            text = "Bus Stop Details",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A),
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Placeholder for symmetry
        Spacer(modifier = Modifier.size(48.dp))
    }
}

@Composable
private fun BusStopDetailsStudentsHeader(totalCount: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Members to Board",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A)
        )
        
        Box(
            modifier = Modifier
                .background(
                    Color.White.copy(alpha = 0.6f),
                    RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = "$totalCount TOTAL",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4A5F5F),
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
private fun BusStopDetailsStudentMemberCard(
    student: StudentMember,
    onCallClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.08f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.25f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Color.White.copy(alpha = 0.30f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Student Initials Circle
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        Color.White.copy(alpha = 0.8f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = student.initials,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2A2A2A)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Student Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = student.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A1A1A)
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Status Badge
                Box(
                    modifier = Modifier
                        .background(
                            when (student.status) {
                                StudentStatus.WAITING_TO_BOARD -> Color(0xFFFF9800)
                                StudentStatus.BOARDED -> Color(0xFF4CAF50)
                            },
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = when (student.status) {
                            StudentStatus.WAITING_TO_BOARD -> "WAITING TO BOARD"
                            StudentStatus.BOARDED -> "BOARDED"
                        },
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                }
            }
            
            // Call Button
            IconButton(
                onClick = onCallClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_call),
                    contentDescription = "Call",
                    tint = Color(0xFF2A2A2A),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun BusStopDetailsBottomNavigationBar(onHomeClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 24.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(35.dp),
                    ambientColor = Color.Black.copy(alpha = 0.08f),
                    spotColor = Color.Black.copy(alpha = 0.08f)
                ),
            shape = RoundedCornerShape(35.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.25f)
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                Color.White.copy(alpha = 0.30f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BusStopDetailsBottomNavItem(
                    icon = R.drawable.ic_home,
                    label = "HOME",
                    isSelected = false,
                    onClick = onHomeClick
                )
                
                BusStopDetailsBottomNavItem(
                    icon = R.drawable.ic_group,
                    label = "MEMBERS",
                    isSelected = true,
                    onClick = { }
                )
                
                BusStopDetailsBottomNavItem(
                    icon = R.drawable.ic_map,
                    label = "ROUTES",
                    isSelected = false,
                    onClick = { }
                )
                
                BusStopDetailsBottomNavItem(
                    icon = R.drawable.ic_settings,
                    label = "SETTINGS",
                    isSelected = false,
                    onClick = { }
                )
            }
        }
    }
}

@Composable
private fun BusStopDetailsBottomNavItem(
    icon: Int,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    bounded = false,
                    radius = 24.dp,
                    color = Color(0xFF6B9A92).copy(alpha = 0.2f)
                )
            ) { onClick() }
            .padding(8.dp)
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = label,
            tint = if (isSelected) Color(0xFF2A2A2A) else Color(0xFF4A5F5F),
            modifier = Modifier.size(20.dp)
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color(0xFF2A2A2A) else Color(0xFF4A5F5F),
            letterSpacing = 0.5.sp
        )
    }
}