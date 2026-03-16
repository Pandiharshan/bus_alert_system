package com.campusbussbuddy.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.campusbussbuddy.R
import com.campusbussbuddy.firebase.StudentInfo
import com.campusbussbuddy.firebase.StudentResult
import com.campusbussbuddy.firebase.FirebaseManager
import com.campusbussbuddy.ui.theme.GlassBackground
import kotlinx.coroutines.launch

@Composable
fun StudentDatabaseScreen(
    onBackClick: () -> Unit,
    onAddStudentClick: () -> Unit
) {
    var students by remember { mutableStateOf<List<StudentInfo>>(emptyList()) }
    var filteredStudents by remember { mutableStateOf<List<StudentInfo>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedStudent by remember { mutableStateOf<StudentInfo?>(null) }
    var isDeleting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val scope = rememberCoroutineScope()
    
    // Load students on screen launch
    LaunchedEffect(Unit) {
        scope.launch {
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
    
    GlassBackground {
        Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Bar
            TopBar(
                onBackClick = onBackClick
            )
            
            // Search Bar
            SearchBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
            )
            
            if (isLoading) {
                // Loading State
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFF7DD3C0)
                    )
                }
            } else if (filteredStudents.isEmpty()) {
                // Empty State
                EmptyState(
                    isSearching = searchQuery.isNotBlank(),
                    onAddClick = onAddStudentClick
                )
            } else {
                // Students List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredStudents) { student ->
                        StudentCard(
                            student = student,
                            onEditClick = {
                                selectedStudent = student
                                showEditDialog = true
                            },
                            onDeleteClick = {
                                selectedStudent = student
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
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
                        // Update student in Firebase
                        val result = FirebaseManager.updateStudentInfo(updatedStudent, newPassword)
                        if (result is StudentResult.Success) {
                            // Refresh list
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
            DeleteConfirmationDialog(
                studentName = selectedStudent!!.name,
                isDeleting = isDeleting,
                errorMessage = errorMessage,
                onConfirm = {
                    scope.launch {
                        isDeleting = true
                        errorMessage = null
                        
                        when (val result = FirebaseManager.deleteStudentAccount(selectedStudent!!.uid)) {
                            is StudentResult.Success -> {
                                // Remove from local list
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
                },
                onDismiss = {
                    if (!isDeleting) {
                        showDeleteDialog = false
                        selectedStudent = null
                        errorMessage = null
                    }
                }
            )
        }
            
        // Floating Add Button (bottom right)
        FloatingActionButton(
            onClick = onAddStudentClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(28.dp)
                .size(64.dp),
            containerColor = Color(0xFF6B9A92),
            contentColor = Color.White,
            shape = CircleShape
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_group),
                    contentDescription = "Add Student",
                    modifier = Modifier.size(24.dp)
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_add),
                    contentDescription = "Add",
                    modifier = Modifier.size(20.dp).offset(x = (-4).dp)
                )
            }
        }
    }
    }
}

@Composable
private fun SearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        placeholder = {
            Text(
                text = "Search students...",
                color = Color(0xFF7A9B9B),
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
            )
        },
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_person),
                contentDescription = "Search",
                tint = Color(0xFF6B9090),
                modifier = Modifier.size(24.dp)
            )
        },
        shape = RoundedCornerShape(28.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            focusedContainerColor = Color(0xFFD9E8E6),
            unfocusedContainerColor = Color(0xFFD9E8E6),
            focusedTextColor = Color(0xFF2C3E3E),
            unfocusedTextColor = Color(0xFF2C3E3E),
            cursorColor = Color(0xFF5A9A8A)
        ),
        textStyle = androidx.compose.ui.text.TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF2C3E3E)
        ),
        singleLine = true
    )
}

@Composable
private fun EmptyState(
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
            Icon(
                painter = painterResource(id = R.drawable.ic_group),
                contentDescription = if (isSearching) "No Results" else "No Students",
                modifier = Modifier.size(80.dp),
                tint = Color(0xFF8AAFA8)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = if (isSearching) "No Students Found" else "No Students Yet",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF3A4F4F)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = if (isSearching) "Try a different search term" else "Add your first student to get started",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF5A7070),
                textAlign = TextAlign.Center
            )
            
            if (!isSearching) {
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = onAddClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF5A9A8A)
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.height(48.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_add),
                        contentDescription = "Add",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Add Student",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun StudentCard(
    student: StudentInfo,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 0.dp,
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFD9E8E6)
        ),
        border = BorderStroke(0.dp, Color.Transparent)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Student Icon
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .background(
                        Color(0xFFB8D4D1),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_person),
                    contentDescription = "Student",
                    tint = Color(0xFF6B9090),
                    modifier = Modifier.size(36.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Student Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = student.name,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A),
                    letterSpacing = 0.sp
                )
                
                Spacer(modifier = Modifier.height(2.dp))
                
                Text(
                    text = "@${student.username}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF4A5F5F)
                )
                
                Spacer(modifier = Modifier.height(1.dp))
                
                Text(
                    text = "Bus: ${student.busId}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF4A5F5F)
                )
                
                Spacer(modifier = Modifier.height(2.dp))
                
                Text(
                    text = "Stop: ${student.stop}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
            }
            
            // Action Buttons - stacked vertically on the right
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Edit Button (settings icon)
                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_settings),
                        contentDescription = "Edit Student",
                        tint = Color(0xFF2A2A2A),
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                // Delete Button (trash/remove icon)
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_remove),
                        contentDescription = "Delete Student",
                        tint = Color(0xFF2A2A2A),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DeleteConfirmationDialog(
    studentName: String,
    isDeleting: Boolean,
    errorMessage: String?,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Delete Student?",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = "Are you sure you want to permanently delete $studentName?",
                    fontSize = 14.sp
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "This will remove:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF666666)
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "• Student profile from database\n• Bus assignment\n• Authentication account",
                    fontSize = 12.sp,
                    color = Color(0xFF888888),
                    lineHeight = 18.sp
                )
                
                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = errorMessage,
                        fontSize = 12.sp,
                        color = Color(0xFFD32F2F)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = !isDeleting
            ) {
                if (isDeleting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = Color(0xFFD32F2F)
                    )
                } else {
                    Text(
                        text = "Delete",
                        color = Color(0xFFD32F2F),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isDeleting
            ) {
                Text(
                    text = "Cancel",
                    color = Color(0xFF666666)
                )
            }
        }
    )
}

@Composable
private fun TopBar(
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(Color.Transparent)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back button - using chevron left icon
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_chevron_left),
                contentDescription = "Back",
                tint = Color(0xFF2C3E3E),
                modifier = Modifier.size(32.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Title
        Text(
            text = "Student Management",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A)
        )
    }
}
