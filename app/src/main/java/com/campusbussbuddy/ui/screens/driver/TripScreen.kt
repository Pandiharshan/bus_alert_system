package com.campusbussbuddy.ui.screens.driver

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
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

data class BusStop(
    val name: String,
    val students: List<Student>,
    val isCompleted: Boolean = false
)

data class Student(
    val name: String,
    val status: StudentStatus
)

enum class StudentStatus {
    PRESENT, LATE, ABSENT
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripScreen(
    navController: NavHostController
) {
    // Mock data
    val busStops = remember {
        listOf(
            BusStop(
                name = "Main Gate",
                students = listOf(
                    Student("Alice Johnson", StudentStatus.PRESENT),
                    Student("Bob Smith", StudentStatus.PRESENT),
                    Student("Carol Davis", StudentStatus.LATE)
                )
            ),
            BusStop(
                name = "Library Corner",
                students = listOf(
                    Student("David Wilson", StudentStatus.PRESENT),
                    Student("Emma Brown", StudentStatus.ABSENT),
                    Student("Frank Miller", StudentStatus.PRESENT)
                )
            ),
            BusStop(
                name = "Sports Complex",
                students = listOf(
                    Student("Grace Lee", StudentStatus.PRESENT),
                    Student("Henry Taylor", StudentStatus.PRESENT)
                )
            )
        )
    }
    
    // Animation for the gradient
    val infiniteTransition = rememberInfiniteTransition(label = "gradient")
    val animatedOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(7000, easing = LinearEasing),
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
                        Color(0xFF667eea).copy(alpha = 0.8f + animatedOffset * 0.2f),
                        Color(0xFF764ba2).copy(alpha = 0.9f),
                        Color(0xFF667eea).copy(alpha = 0.7f + animatedOffset * 0.3f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "🚌 Active Trip",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Bus #247 • Route A",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
                
                IconButton(
                    onClick = { navController.popBackStack() }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Close",
                        tint = Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Map Area Placeholder
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.95f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFFE2E8F0), Color(0xFFF7FAFC))
                            )
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
                            tint = Color(0xFF718096)
                        )
                        Text(
                            text = "Live Route Map",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF4A5568),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        Text(
                            text = "GPS tracking active",
                            fontSize = 12.sp,
                            color = Color(0xFF718096)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // QR Code Area
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.95f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0xFF667eea), Color(0xFF764ba2))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "QR Code",
                            modifier = Modifier.size(40.dp),
                            tint = Color.White
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(20.dp))
                    
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Student Check-in QR",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2D3748)
                        )
                        Text(
                            text = "Students scan to mark attendance",
                            fontSize = 14.sp,
                            color = Color(0xFF718096)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Bus Stops List
            Text(
                text = "Bus Stops",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(busStops) { busStop ->
                    BusStopCard(busStop = busStop)
                }
            }
        }
    }
}

@Composable
private fun BusStopCard(
    busStop: BusStop
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = busStop.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D3748)
                )
                
                if (busStop.isCompleted) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Completed",
                        tint = Color(0xFF38A169),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            busStop.students.forEach { student ->
                StudentRow(student = student)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun StudentRow(
    student: Student
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = student.name,
            fontSize = 16.sp,
            color = Color(0xFF4A5568),
            modifier = Modifier.weight(1f)
        )
        
        val (statusText, statusColor) = when (student.status) {
            StudentStatus.PRESENT -> "Present" to Color(0xFF38A169)
            StudentStatus.LATE -> "Late" to Color(0xFFD69E2E)
            StudentStatus.ABSENT -> "Absent" to Color(0xFFE53E3E)
        }
        
        Surface(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp)),
            color = statusColor.copy(alpha = 0.1f)
        ) {
            Text(
                text = statusText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = statusColor,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }
    }
}