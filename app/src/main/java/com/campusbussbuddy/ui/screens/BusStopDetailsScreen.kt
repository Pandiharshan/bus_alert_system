package com.campusbussbuddy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.campusbussbuddy.R
import com.campusbussbuddy.ui.theme.GlassBackground
import com.campusbussbuddy.ui.components.*

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
                // Top Bar (reusable component)
                TopBarLayout(
                    title = "Bus Stop Details",
                    onBackClick = onBackClick
                )
                
                // Main Content with LazyColumn
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(
                        start = 20.dp,
                        end = 20.dp,
                        top = 16.dp,
                        bottom = 90.dp
                    )
                ) {
                    item {
                        // Students Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SectionTitle(
                                text = "Members to Board",
                                fontSize = 18
                            )
                            
                            TextBadge(
                                text = "$totalStudents TOTAL",
                                backgroundColor = Color.White.copy(alpha = 0.6f),
                                textColor = Color(0xFF4A5F5F)
                            )
                        }
                    }
                    
                    items(students) { student ->
                        BusStopDetailsStudentMemberCard(
                            student = student,
                            onCallClick = { /* TODO: Handle call */ }
                        )
                    }
                }
            }
            
            // Fixed Bottom Navigation (reusable component)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color.Transparent)
            ) {
                BottomNavBar {
                    BottomNavItem(
                        icon = R.drawable.ic_home,
                        label = "HOME",
                        onClick = onHomeClick
                    )
                    
                    BottomNavItem(
                        icon = R.drawable.ic_group,
                        label = "MEMBERS",
                        isSelected = true,
                        onClick = { }
                    )
                    
                    BottomNavItem(
                        icon = R.drawable.ic_map,
                        label = "ROUTES",
                        onClick = { }
                    )
                    
                    BottomNavItem(
                        icon = R.drawable.ic_settings,
                        label = "SETTINGS",
                        onClick = { }
                    )
                }
            }
        }
    }
}

@Composable
private fun BusStopDetailsStudentMemberCard(
    student: StudentMember,
    onCallClick: () -> Unit
) {
    GlassListCard {
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
            SectionTitle(
                text = student.name,
                fontSize = 16
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Status Badge (reusable component)
            TextBadge(
                text = when (student.status) {
                    StudentStatus.WAITING_TO_BOARD -> "WAITING TO BOARD"
                    StudentStatus.BOARDED -> "BOARDED"
                },
                backgroundColor = when (student.status) {
                    StudentStatus.WAITING_TO_BOARD -> Color(0xFFFF9800)
                    StudentStatus.BOARDED -> Color(0xFF4CAF50)
                }
            )
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