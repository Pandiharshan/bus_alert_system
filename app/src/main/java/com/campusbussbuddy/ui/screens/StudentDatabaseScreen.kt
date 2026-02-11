package com.campusbussbuddy.ui.screens

import android.util.Log
import androidx.compose.foundation.background
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
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Bar
            TopBar(
                onBackClick = onBackClick,
                onAddClick = onAddStudentClick,
                studentCount = students.size
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
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
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
        modifier = modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = "Search by name, username, bus, or stop...",
                color = Color(0xFFAAAAAA),
                fontSize = 14.sp
            )
        },
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_person),
                contentDescription = "Search",
                tint = Color(0xFF888888),
                modifier = Modifier.size(20.dp)
            )
        },
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                IconButton(
                    onClick = { onSearchQueryChange("") }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_remove),
                        contentDescription = "Clear",
                        tint = Color(0xFF888888),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        },
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF7DD3C0),
            unfocusedBorderColor = Color(0xFFE0E0E0),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
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
                painter = painterResource(id = R.drawable.ic_person),
                contentDescription = if (isSearching) "No Results" else "No Students",
                modifier = Modifier.size(80.dp),
                tint = Color(0xFFCCCCCC)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = if (isSearching) "No Students Found" else "No Students Yet",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF666666)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = if (isSearching) "Try a different search term" else "Add your first student to get started",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF888888),
                textAlign = TextAlign.Center
            )
            
            if (!isSearching) {
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = onAddClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF7DD3C0)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_add),
                        contentDescription = "Add",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Student")
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Student Icon
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        Color(0xFF7DD3C0).copy(alpha = 0.15f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_person),
                    contentDescription = "Student",
                    tint = Color(0xFF7DD3C0),
                    modifier = Modifier.size(30.dp)
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
                    color = Color.Black
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "@${student.username}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF666666)
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_directions_bus_vector),
                        contentDescription = "Bus",
                        modifier = Modifier.size(12.dp),
                        tint = Color(0xFF7DD3C0)
                    )
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    Text(
                        text = student.busId,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF888888)
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Icon(
                        painter = painterResource(id = R.drawable.ic_pin_drop),
                        contentDescription = "Stop",
                        modifier = Modifier.size(12.dp),
                        tint = Color(0xFF888888)
                    )
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    Text(
                        text = student.stop,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF888888)
                    )
                }
            }
            
            // Action Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Edit Button
                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_settings),
                        contentDescription = "Edit Student",
                        tint = Color(0xFF7DD3C0),
                        modifier = Modifier.size(18.dp)
                    )
                }
                
                // Delete Button
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_remove),
                        contentDescription = "Delete Student",
                        tint = Color(0xFFD32F2F),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}


@Composable
private fun EditStudentDialog(
    student: StudentInfo,
    onDismiss: () -> Unit,
    onSave: (StudentInfo, String?) -> Unit,
    errorMessage: String? = null
) {
    var name by remember { mutableStateOf(student.name) }
    var username by remember { mutableStateOf(student.username) }
    var busId by remember { mutableStateOf(student.busId) }
    var stop by remember { mutableStateOf(student.stop) }
    var newPassword by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var showPasswordChangeConfirm by remember { mutableStateOf(false) }
    
    // Auto-generate email from username
    val generatedEmail = "${username.trim()}@gmail.com"
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "Edit Student",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF7DD3C0),
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    )
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    placeholder = { Text("Enter unique username (e.g., pandi)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF7DD3C0),
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        disabledBorderColor = Color(0xFFE0E0E0),
                        disabledContainerColor = Color(0xFFF5F5F5),
                        disabledTextColor = Color(0xFF888888)
                    ),
                    enabled = false
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = generatedEmail,
                    onValueChange = { },
                    label = { Text("Email") },
                    placeholder = { Text("Auto-generated from username") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFE0E0E0),
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        disabledBorderColor = Color(0xFFE0E0E0),
                        focusedContainerColor = Color(0xFFF5F5F5),
                        unfocusedContainerColor = Color(0xFFF5F5F5),
                        disabledContainerColor = Color(0xFFF5F5F5),
                        disabledTextColor = Color(0xFF888888)
                    ),
                    enabled = false
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = busId,
                    onValueChange = { busId = it },
                    label = { Text("Bus ID") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF7DD3C0),
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    )
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = stop,
                    onValueChange = { stop = it },
                    label = { Text("Bus Stop") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF7DD3C0),
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    )
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Password Change Section
                Column {
                    Text(
                        text = "Reset Password (Optional)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        placeholder = {
                            Text(
                                text = "Enter any value to trigger password reset email",
                                color = Color(0xFFAAAAAA),
                                fontSize = 14.sp
                            )
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = { showPassword = !showPassword }
                            ) {
                                Icon(
                                    painter = painterResource(
                                        id = if (showPassword) R.drawable.ic_visibility_off else R.drawable.ic_visibility
                                    ),
                                    contentDescription = if (showPassword) "Hide password" else "Show password",
                                    tint = Color(0xFF888888),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        },
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF7DD3C0),
                            unfocusedBorderColor = Color(0xFFE0E0E0)
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )
                    
                    if (newPassword.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Student will receive a password reset email",
                            fontSize = 11.sp,
                            color = Color(0xFF7DD3C0)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Error Message
                if (errorMessage != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Color(0xFFF8D7DA),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_emergency),
                            contentDescription = "Error",
                            tint = Color(0xFF721C24),
                            modifier = Modifier.size(16.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text(
                            text = errorMessage,
                            fontSize = 11.sp,
                            color = Color(0xFF721C24),
                            lineHeight = 16.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                }
                
                // Info Note
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Color(0xFFFFF3CD),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_notifications),
                        contentDescription = "Info",
                        tint = Color(0xFF856404),
                        modifier = Modifier.size(16.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = "Username cannot be changed after account creation. Email is auto-generated from username. You can reset the student's password here.",
                        fontSize = 11.sp,
                        color = Color(0xFF856404),
                        lineHeight = 16.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF666666)
                        )
                    ) {
                        Text("Cancel")
                    }
                    
                    Button(
                        onClick = {
                            // If password reset is requested, show confirmation
                            if (newPassword.isNotEmpty()) {
                                showPasswordChangeConfirm = true
                            } else {
                                val updatedStudent = student.copy(
                                    name = name.trim(),
                                    username = username.trim(),
                                    busId = busId.trim(),
                                    stop = stop.trim()
                                )
                                onSave(updatedStudent, null)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF7DD3C0)
                        ),
                        enabled = name.isNotBlank() && username.isNotBlank() && 
                                  busId.isNotBlank() && stop.isNotBlank()
                    ) {
                        Text("Save")
                    }
                }
            }
        }
        
        // Password Change Confirmation Dialog
        if (showPasswordChangeConfirm) {
            AlertDialog(
                onDismissRequest = { showPasswordChangeConfirm = false },
                title = {
                    Text(
                        text = "Send Password Reset?",
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        text = "A password reset email will be sent to ${student.name}. They will receive an email to set their new password. Do you want to proceed?",
                        fontSize = 14.sp
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showPasswordChangeConfirm = false
                            val updatedStudent = student.copy(
                                name = name.trim(),
                                username = username.trim(),
                                busId = busId.trim(),
                                stop = stop.trim()
                            )
                            onSave(updatedStudent, newPassword)
                        }
                    ) {
                        Text(
                            text = "Send Reset Email",
                            color = Color(0xFF7DD3C0),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showPasswordChangeConfirm = false }
                    ) {
                        Text(
                            text = "Cancel",
                            color = Color(0xFF666666)
                        )
                    }
                }
            )
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
    onBackClick: () -> Unit,
    onAddClick: () -> Unit,
    studentCount: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_back_vector),
                contentDescription = "Back",
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Student Database",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
            
            Text(
                text = "$studentCount ${if (studentCount == 1) "student" else "students"}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF888888)
            )
        }
        
        // Add Student Button
        IconButton(
            onClick = onAddClick,
            modifier = Modifier
                .size(40.dp)
                .background(
                    Color(0xFF7DD3C0).copy(alpha = 0.15f),
                    CircleShape
                )
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_add),
                contentDescription = "Add Student",
                tint = Color(0xFF7DD3C0),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
