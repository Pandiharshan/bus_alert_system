package com.campusbussbuddy.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.campusbussbuddy.R
import com.campusbussbuddy.firebase.StudentInfo
import com.campusbussbuddy.firebase.StudentResult
import com.campusbussbuddy.firebase.FirebaseManager
import com.campusbussbuddy.ui.theme.*
import com.campusbussbuddy.ui.neumorphism.cards.NeumorphismCard
import com.campusbussbuddy.ui.neumorphism.inputs.NeumorphismTextField
import com.campusbussbuddy.ui.neumorphism.buttons.NeumorphismButton
import com.campusbussbuddy.ui.neumorphism.buttons.NeumorphismIconButton
import com.campusbussbuddy.ui.neumorphism.layout.AppLabelPill
import com.campusbussbuddy.ui.neumorphism.layout.NeumorphismScreenContainer
import kotlinx.coroutines.launch

@Composable
fun StudentDatabaseScreen(
    onBackClick: () -> Unit,
    onAddStudentClick: () -> Unit
) {
    var students by remember { mutableStateOf<List<StudentInfo>>(emptyList()) }
    var filteredStudents by remember { mutableStateOf<List<StudentInfo>>(emptyList()) }
    var busMap by remember { mutableStateOf<Map<String, Int>>(emptyMap()) } // busId -> busNumber
    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedStudent by remember { mutableStateOf<StudentInfo?>(null) }
    var isDeleting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    // Load students AND buses on screen launch so we can show bus numbers
    LaunchedEffect(Unit) {
        scope.launch {
            val allBuses = FirebaseManager.getAllBuses()
            busMap = allBuses.associate { it.busId to it.busNumber }
            students = FirebaseManager.getAllStudents()
            filteredStudents = students
            isLoading = false
        }
    }

    // Filter students based on search query
    LaunchedEffect(searchQuery, students) {
        filteredStudents = if (searchQuery.isBlank()) {
            students
        } else {
            students.filter { student ->
                student.name.contains(searchQuery, ignoreCase = true) ||
                student.username.contains(searchQuery, ignoreCase = true) ||
                student.busId.contains(searchQuery, ignoreCase = true) ||
                student.stop.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    NeumorphismScreenContainer {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(48.dp))

            // Embedded TopBar utilizing AppLabelPill
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
                    icon = R.drawable.ic_group,
                    title = "Student Management"
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Search Bar
            Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                NeumorphismTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = "Search students...",
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_person),
                            contentDescription = "Search",
                            tint = NeumorphTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (isLoading) {
                // Loading State
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = NeumorphAccentPrimary)
                }
            } else if (filteredStudents.isEmpty()) {
                // Empty State
                StudentEmptyState(
                    isSearching = searchQuery.isNotBlank(),
                    onAddClick = onAddStudentClick
                )
            } else {
                // Students List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 120.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    items(filteredStudents) { student ->
                        StudentCard(
                            student = student,
                            busLabel = busMap[student.busId]?.let { "Bus $it" } ?: if (student.busId.isBlank()) "Not Assigned" else "Bus ${student.busId.take(6)}...",
                            onEditClick = { selectedStudent = student; showEditDialog = true },
                            onDeleteClick = { selectedStudent = student; showDeleteDialog = true }
                        )
                    }
                }
            }
        }

        // Floating Add Button
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .size(64.dp)
                .neumorphic(cornerRadius = 32.dp, elevation = 8.dp, blur = 16.dp)
                .background(NeumorphSurface, CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onAddStudentClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_add),
                contentDescription = "Add Student",
                tint = NeumorphAccentPrimary,
                modifier = Modifier.size(28.dp)
            )
        }

        // Edit Student Dialog
        if (showEditDialog && selectedStudent != null) {
            EditStudentDialog(
                student = selectedStudent!!,
                onDismiss = {
                    showEditDialog = false
                    selectedStudent = null
                    errorMessage = null
                },
                onSave = { updatedStudent, newPassword ->
                    scope.launch {
                        val result = FirebaseManager.updateStudentInfo(updatedStudent, newPassword)
                        if (result is StudentResult.Success) {
                            students = FirebaseManager.getAllStudents()
                            showEditDialog = false
                            selectedStudent = null
                            errorMessage = null
                        } else if (result is StudentResult.Error) {
                            errorMessage = result.message
                        }
                    }
                },
                errorMessage = errorMessage
            )
        }

        // Delete Confirmation Dialog
        if (showDeleteDialog && selectedStudent != null) {
            StudentDeleteConfirmationDialog(
                studentName = selectedStudent!!.name,
                onDismiss = {
                    if (!isDeleting) {
                        showDeleteDialog = false
                        selectedStudent = null
                        errorMessage = null
                    }
                },
                onConfirm = {
                    scope.launch {
                        isDeleting = true
                        errorMessage = null

                        when (val result = FirebaseManager.deleteStudentAccount(selectedStudent!!.uid)) {
                            is StudentResult.Success -> {
                                students = students.filter { it.uid != selectedStudent!!.uid }
                                isDeleting = false
                                showDeleteDialog = false
                                selectedStudent = null
                            }
                            is StudentResult.Error -> {
                                errorMessage = result.message
                                isDeleting = false
                            }
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun StudentEmptyState(
    isSearching: Boolean,
    onAddClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .neumorphicInset(cornerRadius = 50.dp, blur = 12.dp)
                    .background(NeumorphBgPrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_group),
                    contentDescription = if (isSearching) "No Results" else "No Students",
                    modifier = Modifier.size(48.dp),
                    tint = NeumorphTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = if (isSearching) "No Students Found" else "No Students Yet",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = NeumorphTextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isSearching) "Try a different search term" else "Add your first student to get started",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = NeumorphTextSecondary,
                textAlign = TextAlign.Center
            )

            if (!isSearching) {
                Spacer(modifier = Modifier.height(24.dp))

                NeumorphismButton(
                    text = "Add Student",
                    onClick = onAddClick,
                    modifier = Modifier.fillMaxWidth(0.6f)
                )
            }
        }
    }
}

@Composable
private fun StudentCard(
    student: StudentInfo,
    busLabel: String,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    NeumorphismCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Student Icon (neumorphic circular container)
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .neumorphicInset(cornerRadius = 32.dp, blur = 8.dp)
                    .background(NeumorphSurface, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_person),
                    contentDescription = "Student",
                    tint = NeumorphAccentPrimary,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Student Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = student.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeumorphTextPrimary
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "@${student.username}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = NeumorphTextSecondary
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Bus: $busLabel",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = NeumorphTextSecondary
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Stop: ${student.stop}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeumorphTextPrimary
                )
            }

            // Action Buttons
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Edit Button
                NeumorphismIconButton(
                    iconRes = R.drawable.ic_settings,
                    onClick = onEditClick,
                    size = 40.dp,
                    iconSize = 20.dp,
                    unselectedTint = NeumorphTextPrimary
                )

                // Delete Button
                NeumorphismIconButton(
                    iconRes = R.drawable.ic_remove,
                    onClick = onDeleteClick,
                    size = 40.dp,
                    iconSize = 20.dp,
                    unselectedTint = Color(0xFFE53935)
                )
            }
        }
    }
}

@Composable
private fun StudentDeleteConfirmationDialog(
    studentName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    val alpha by androidx.compose.animation.core.animateFloatAsState(if (visible) 1f else 0f, androidx.compose.animation.core.tween(300), label = "a")
    val scale by androidx.compose.animation.core.animateFloatAsState(if (visible) 1f else 0.88f, androidx.compose.animation.core.tween(300), label = "s")

    androidx.compose.ui.window.Dialog(
        onDismissRequest = { visible = false; onDismiss() },
        properties = androidx.compose.ui.window.DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true, usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(NeumorphBgPrimary.copy(alpha = 0.85f * alpha))
                .clickable(remember { MutableInteractionSource() }, null) { visible = false; onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp)
                    .scale(scale)
                    .alpha(alpha)
                    .neumorphic(
                        cornerRadius = 24.dp,
                        elevation = 4.dp,
                        blur = 6.dp,
                        lightShadowColor = Color.Transparent,
                        darkShadowColor = Color.Black.copy(alpha = 0.15f)
                    )
                    .background(NeumorphSurface, RoundedCornerShape(24.dp))
                    .clickable(remember { MutableInteractionSource() }, null) { }
            ) {
                // Subtle purple bottom accent
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color.Transparent, NeumorphAccentPrimary.copy(alpha = 0.15f))
                            ),
                            shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .neumorphicInset(cornerRadius = 32.dp, blur = 12.dp)
                            .background(NeumorphBgPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Warning,
                            contentDescription = "Warning",
                            tint = Color(0xFFE53935),
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Delete $studentName?",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeumorphTextPrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "This will permanently remove this student, their bus assignment, and authentication account.",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = NeumorphTextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxWidth().height(56.dp).clickable { onDismiss() }, contentAlignment = Alignment.Center) {
                            Text("Cancel", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = NeumorphTextSecondary)
                        }
                        NeumorphismButton(
                            text = "Delete Permanently",
                            onClick = onConfirm,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}
