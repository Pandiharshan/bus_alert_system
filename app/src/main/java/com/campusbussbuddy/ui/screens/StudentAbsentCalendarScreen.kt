package com.campusbussbuddy.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.campusbussbuddy.firebase.AbsenceData
import com.campusbussbuddy.firebase.AbsenceResult
import com.campusbussbuddy.firebase.FirebaseManager
import com.campusbussbuddy.firebase.StudentInfo
import com.campusbussbuddy.ui.neumorphism.buttons.NeumorphismButton
import com.campusbussbuddy.ui.neumorphism.buttons.NeumorphismIconButton
import com.campusbussbuddy.ui.neumorphism.cards.NeumorphismCard
import com.campusbussbuddy.ui.neumorphism.layout.AppLabelPill
import com.campusbussbuddy.ui.neumorphism.layout.NeumorphismScreenContainer
import com.campusbussbuddy.ui.theme.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentAbsentCalendarScreen(
    studentUid: String,
    onBackClick: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(true) }
    var isSubmitting by remember { mutableStateOf(false) }
    var studentInfo by remember { mutableStateOf<StudentInfo?>(null) }
    
    // Existing Absences list
    var absences by remember { mutableStateOf<List<AbsenceData>>(emptyList()) }
    
    // The dates the student is currently selecting to mark absent
    var selectedDates by remember { mutableStateOf<List<String>>(emptyList()) }
    
    var showDatePicker by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    @OptIn(ExperimentalMaterial3Api::class)
    val datePickerState = rememberDatePickerState(
        // Default to tomorrow
        initialSelectedDateMillis = System.currentTimeMillis() + 86400000L,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val cal = Calendar.getInstance()
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                // Allow: tomorrow (today + 1 day) through today + 6 days (5 days of advance planning)
                // We add 86_400_000 ms leeway to handle UTC vs local timezone offset in the picker.
                val tomorrowUtc = cal.timeInMillis + 86400000L - 86400000L   // midnight today UTC approx
                val maxDateUtc  = cal.timeInMillis + 6 * 86400000L            // today + 6 days (covers +5)
                return utcTimeMillis in tomorrowUtc..maxDateUtc
            }
            override fun isSelectableYear(year: Int): Boolean {
                val currentYear = Calendar.getInstance().get(Calendar.YEAR)
                return year >= currentYear
            }
        }
    )
    val formatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    // Fetch student info and absences on start
    LaunchedEffect(studentUid) {
        isLoading = true
        FirebaseManager.getStudentAbsences(studentUid).let { 
            absences = it 
        }
        val doc = FirebaseManager.firestore.collection("students").document(studentUid).get().await()
        if (doc.exists()) {
            studentInfo = StudentInfo(
                uid = doc.id,
                name = doc.getString("name") ?: "",
                username = doc.getString("username") ?: "",
                email = doc.getString("email") ?: "",
                busId = doc.getString("busId") ?: "",
                stop = doc.getString("stop") ?: ""
            )
        }
        isLoading = false
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val cal = Calendar.getInstance()
                        cal.set(Calendar.HOUR_OF_DAY, 0)
                        cal.set(Calendar.MINUTE, 0)
                        cal.set(Calendar.SECOND, 0)
                        cal.set(Calendar.MILLISECOND, 0)

                        val tomorrowMillis = cal.timeInMillis + 86400000L
                        val maxMillis      = cal.timeInMillis + 6 * 86400000L // today + 6 days covers +5

                        when {
                            millis < tomorrowMillis -> errorMessage = "You can only plan absences from tomorrow onward."
                            millis > maxMillis      -> errorMessage = "Absences can be planned up to 5 days in advance only."
                            else -> {
                                val selectedFormatted = formatter.format(Date(millis))
                                if (absences.any { it.date == selectedFormatted }) {
                                    errorMessage = "Date $selectedFormatted is already marked absent."
                                } else if (!selectedDates.contains(selectedFormatted)) {
                                    selectedDates = selectedDates + selectedFormatted
                                    errorMessage = null
                                }
                            }
                        }
                    }
                    showDatePicker = false
                }) {
                    Text("Select", color = NeumorphAccentPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", color = NeumorphTextSecondary)
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = NeumorphAccentPrimary,
                    selectedDayContentColor = Color.White,
                    todayDateBorderColor = NeumorphAccentPrimary,
                    todayContentColor = NeumorphAccentPrimary
                )
            )
        }
    }

    NeumorphismScreenContainer {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(48.dp))

            // Top Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                NeumorphismIconButton(
                    iconRes = R.drawable.ic_chevron_left,
                    onClick = onBackClick,
                    size = 44.dp,
                    iconSize = 24.dp,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
                
                AppLabelPill(
                    icon = R.drawable.ic_calendar_today,
                    title = "Mark Absences"
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = NeumorphAccentPrimary)
                }
                return@Column
            }
            
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                
                // Add Absence Section
                item {
                    NeumorphismCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 24.dp,
                        contentPadding = PaddingValues(24.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Plan an Absence",
                                color = NeumorphTextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.align(Alignment.Start)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Selected Dates Row
                            if (selectedDates.isNotEmpty()) {
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(selectedDates) { dateStr ->
                                        SelectedDateChip(
                                            date = dateStr,
                                            onRemove = { selectedDates = selectedDates - dateStr }
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                            
                            // Show Error
                            AnimatedVisibility(visible = errorMessage != null) {
                                Text(
                                    text = errorMessage ?: "",
                                    color = Color(0xFFE53935),
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )
                            }
                            
                            // Buttons row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                NeumorphismButton(
                                    text = "Pick Date",
                                    onClick = { showDatePicker = true },
                                    modifier = Modifier.weight(1f)
                                )
                                
                                AnimatedVisibility(visible = selectedDates.isNotEmpty()) {
                                    NeumorphismButton(
                                        text = "Confirm",
                                        isLoading = isSubmitting,
                                        onClick = {
                                            studentInfo?.let { std ->
                                                scope.launch {
                                                    isSubmitting = true
                                                    val res = FirebaseManager.markAbsence(
                                                        studentId = std.uid,
                                                        studentName = std.name,
                                                        busId = std.busId,
                                                        stopName = std.stop,
                                                        dates = selectedDates
                                                    )
                                                    if (res is AbsenceResult.Success) {
                                                        // Refresh Absences
                                                        absences = FirebaseManager.getStudentAbsences(std.uid)
                                                        selectedDates = emptyList()
                                                        errorMessage = null
                                                    } else {
                                                        errorMessage = (res as AbsenceResult.Error).message
                                                    }
                                                    isSubmitting = false
                                                }
                                            }
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }
                
                // Existing Absences Section
                item {
                    Text(
                        "Your Absences",
                        color = NeumorphTextSecondary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                    )
                }
                
                if (absences.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No upcoming absences.",
                                color = NeumorphTextSecondary,
                                fontSize = 15.sp
                            )
                        }
                    }
                } else {
                    items(absences) { abs ->
                        AbsenceCardItem(
                            absence = abs,
                            onRevoke = {
                                scope.launch {
                                    FirebaseManager.revokeAbsence(studentUid, abs.date)
                                    // Refresh
                                    absences = FirebaseManager.getStudentAbsences(studentUid)
                                }
                            }
                        )
                    }
                }
                
                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
        }
    }
}

@Composable
private fun SelectedDateChip(date: String, onRemove: () -> Unit) {
    Box(
        modifier = Modifier
            .background(Color(0xFFE8EAF6), shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
            .clickable { onRemove() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(date, color = Color(0xFF3F51B5), fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                painter = painterResource(id = R.drawable.ic_remove),
                contentDescription = "Remove",
                tint = Color(0xFF3F51B5),
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
private fun AbsenceCardItem(absence: AbsenceData, onRevoke: () -> Unit) {
    NeumorphismCard(
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
        cornerRadius = 16.dp,
        contentPadding = PaddingValues(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_calendar_today),
                        contentDescription = "Date",
                        tint = NeumorphTextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = absence.date,
                        color = NeumorphTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Status: ",
                        color = NeumorphTextSecondary,
                        fontSize = 13.sp
                    )
                    Text(
                        text = absence.status.uppercase(),
                        color = Color(0xFFE53935),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            TextButton(
                onClick = onRevoke,
                colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFE53935))
            ) {
                Text("Revoke")
            }
        }
    }
}
