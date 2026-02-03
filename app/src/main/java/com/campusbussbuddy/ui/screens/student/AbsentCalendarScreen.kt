package com.campusbussbuddy.ui.screens.student

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import com.campusbussbuddy.R
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AbsentCalendarScreen(
    navController: NavHostController
) {
    val primary = Color(0xFF0DF26C)
    val backgroundDark = Color(0xFF102217)
    val cardBackground = Color(0xFF1A2E21)
    
    var selectedDate by remember { mutableStateOf<LocalDate?>(LocalDate.now().plusDays(1)) }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    
    val today = LocalDate.now()
    val tomorrow = today.plusDays(1)
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundDark)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 20.dp)
        ) {
            item {
                // TopAppBar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .padding(top = 32.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    
                    Text(
                        text = "Report Absence",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.width(48.dp)) // Balance the back button
                }
            }
            
            item {
                // Headline Text
                Text(
                    text = "Schedule your absence",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            
            item {
                // Body Text
                Text(
                    text = "Select tomorrow's date to notify your driver. You can only mark absence for one day in advance.",
                    color = Color(0xFF9CA3AF),
                    fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            
            item {
                // Calendar Section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        // Month Header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { currentMonth = currentMonth.minusMonths(1) }
                            ) {
                                Icon(
                                    Icons.Default.KeyboardArrowLeft,
                                    contentDescription = "Previous month",
                                    tint = Color.White
                                )
                            }
                            
                            Text(
                                text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(1f)
                            )
                            
                            IconButton(
                                onClick = { currentMonth = currentMonth.plusMonths(1) }
                            ) {
                                Icon(
                                    Icons.Default.KeyboardArrowRight,
                                    contentDescription = "Next month",
                                    tint = Color.White
                                )
                            }
                        }
                        
                        // Days of Week Header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            listOf("S", "M", "T", "W", "T", "F", "S").forEach { day ->
                                Text(
                                    text = day,
                                    color = Color(0xFF6B7280),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                        
                        Divider(
                            color = Color(0xFF374151),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        // Calendar Grid
                        CalendarGrid(
                            currentMonth = currentMonth,
                            selectedDate = selectedDate,
                            today = today,
                            tomorrow = tomorrow,
                            onDateSelected = { date ->
                                selectedDate = if (date == tomorrow) date else null
                            }
                        )
                    }
                }
            }
            
            item {
                // Legend
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    LegendItem(
                        color = primary,
                        label = "Selectable (Tomorrow)"
                    )
                    LegendItem(
                        color = Color(0xFF374151),
                        label = "Unavailable"
                    )
                }
            }
            
            item {
                // Note Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2937)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Note for Driver",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Tapping 'Mark Absent' will instantly update the bus manifest for tomorrow's route.",
                                color = Color(0xFF9CA3AF),
                                fontSize = 14.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                        
                        Box(
                            modifier = Modifier
                                .size(96.dp, 64.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    primary.copy(alpha = 0.1f)
                                )
                        ) {
                            // Placeholder for abstract pattern
                            Icon(
                                painter = painterResource(id = R.drawable.ic_speed),
                                contentDescription = "Signal",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                                    .size(20.dp),
                                tint = primary.copy(alpha = 0.3f)
                            )
                        }
                    }
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
        
        // Action Button (Fixed at bottom)
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(backgroundDark)
                .padding(16.dp)
        ) {
            Button(
                onClick = { 
                    // Handle mark absent action
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primary,
                    contentColor = backgroundDark
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = selectedDate != null
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("", fontSize = 20.sp)
                    Text(
                        text = "Mark Absent for ${selectedDate?.format(DateTimeFormatter.ofPattern("MMM d")) ?: ""}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Text(
                text = "Changes can be reverted until 6:00 PM today",
                color = Color(0xFF6B7280),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            )
        }
    }
}

@Composable
private fun CalendarGrid(
    currentMonth: YearMonth,
    selectedDate: LocalDate?,
    today: LocalDate,
    tomorrow: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val primary = Color(0xFF0DF26C)
    val backgroundDark = Color(0xFF102217)
    
    val firstDayOfMonth = currentMonth.atDay(1)
    val lastDayOfMonth = currentMonth.atEndOfMonth()
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7
    val daysInMonth = currentMonth.lengthOfMonth()
    
    // Create calendar grid
    val calendarDays = mutableListOf<LocalDate?>()
    
    // Add empty cells for days before the first day of the month
    repeat(firstDayOfWeek) {
        calendarDays.add(null)
    }
    
    // Add all days of the month
    for (day in 1..daysInMonth) {
        calendarDays.add(currentMonth.atDay(day))
    }
    
    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier.height(300.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(calendarDays) { date ->
            CalendarDay(
                date = date,
                isSelected = date == selectedDate,
                isToday = date == today,
                isTomorrow = date == tomorrow,
                isSelectable = date == tomorrow,
                onClick = { date?.let { onDateSelected(it) } }
            )
        }
    }
}

@Composable
private fun CalendarDay(
    date: LocalDate?,
    isSelected: Boolean,
    isToday: Boolean,
    isTomorrow: Boolean,
    isSelectable: Boolean,
    onClick: () -> Unit
) {
    val primary = Color(0xFF0DF26C)
    val backgroundDark = Color(0xFF102217)
    
    Box(
        modifier = Modifier
            .size(48.dp)
            .clickable(enabled = isSelectable) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (date != null) {
            val textColor = when {
                isSelected -> backgroundDark
                isSelectable -> Color.White
                isToday -> Color(0xFF6B7280)
                else -> Color(0xFF4B5563)
            }
            
            val backgroundColor = when {
                isSelected -> primary
                isToday -> Color.Transparent
                else -> Color.Transparent
            }
            
            val borderColor = when {
                isToday -> Color(0xFF4B5563)
                else -> Color.Transparent
            }
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor, CircleShape)
                    .border(
                        width = if (isToday) 1.dp else 0.dp,
                        color = borderColor,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = date.dayOfMonth.toString(),
                    color = textColor,
                    fontSize = 14.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
            }
            
            // Add ring effect for selected date
            if (isSelected) {
                val infiniteTransition = rememberInfiniteTransition(label = "ring")
                val scale by infiniteTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = 1.2f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1500),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "ring"
                )
                
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(scale)
                        .border(
                            width = 4.dp,
                            color = primary.copy(alpha = 0.3f),
                            shape = CircleShape
                        )
                )
            }
        }
    }
}

@Composable
private fun LegendItem(
    color: Color,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, CircleShape)
        )
        Text(
            text = label,
            color = Color(0xFF6B7280),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}