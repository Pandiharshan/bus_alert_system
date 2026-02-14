package com.campusbussbuddy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.campusbussbuddy.R
import com.campusbussbuddy.firebase.StudentData
import com.campusbussbuddy.firebase.StudentResult
import com.campusbussbuddy.firebase.FirebaseManager
import com.campusbussbuddy.ui.theme.AppBackgroundContainer
import kotlinx.coroutines.launch

@Composable
fun AddStudentScreen(
    onBackClick: () -> Unit,
    onStudentAdded: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var busId by remember { mutableStateOf("") }
    var stop by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    
    AppBackgroundContainer {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Bar
            AddStudentTopBar(onBackClick = onBackClick)
            
            // Main Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(24.dp)
            ) {
                // Title
                Text(
                    text = "Add New Student",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                Text(
                    text = "Create a new student account",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF666666)
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Form Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(24.dp),
                            ambientColor = Color.Black.copy(alpha = 0.1f),
                            spotColor = Color.Black.copy(alpha = 0.1f)
                        ),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.28f)
                    ),
                    border = BorderStroke(2.dp, Color.White.copy(alpha = 0.55f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        // Name Field
                        StudentFormField(
                            label = "Full Name",
                            value = name,
                            onValueChange = { 
                                name = it
                                errorMessage = null
                            },
                            placeholder = "Enter student's full name",
                            icon = R.drawable.ic_person,
                            enabled = !isLoading
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Username Field
                        StudentFormField(
                            label = "Username",
                            value = username,
                            onValueChange = { 
                                username = it
                                errorMessage = null
                            },
                            placeholder = "Enter unique username (e.g., pandi)",
                            icon = R.drawable.ic_person,
                            enabled = !isLoading
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Password Field
                        StudentPasswordField(
                            label = "Password",
                            value = password,
                            onValueChange = { 
                                password = it
                                errorMessage = null
                            },
                            placeholder = "Create a secure password",
                            isPasswordVisible = isPasswordVisible,
                            onVisibilityToggle = { isPasswordVisible = !isPasswordVisible },
                            enabled = !isLoading
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Bus ID Field
                        StudentFormField(
                            label = "Assigned Bus ID",
                            value = busId,
                            onValueChange = { 
                                busId = it
                                errorMessage = null
                            },
                            placeholder = "e.g., bus_01",
                            icon = R.drawable.ic_directions_bus_vector,
                            enabled = !isLoading
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Stop Field
                        StudentFormField(
                            label = "Bus Stop",
                            value = stop,
                            onValueChange = { 
                                stop = it
                                errorMessage = null
                            },
                            placeholder = "e.g., Main Gate, Library Stop",
                            icon = R.drawable.ic_pin_drop,
                            enabled = !isLoading
                        )
                        
                        // Error Message
                        if (errorMessage != null) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = errorMessage!!,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFFD32F2F),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        
                        // Success Message
                        if (successMessage != null) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = successMessage!!,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFF4CAF50),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        // Create Student Button
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .shadow(
                                    elevation = 4.dp,
                                    shape = RoundedCornerShape(28.dp),
                                    ambientColor = Color.Black.copy(alpha = 0.1f),
                                    spotColor = Color.Black.copy(alpha = 0.1f)
                                )
                                .background(
                                    if (isLoading) Color(0xFF7DD3C0).copy(alpha = 0.5f)
                                    else Color(0xFF7DD3C0).copy(alpha = 0.9f),
                                    RoundedCornerShape(28.dp)
                                )
                                .clip(RoundedCornerShape(28.dp))
                                .clickable(
                                    enabled = !isLoading && name.isNotBlank() && username.isNotBlank() 
                                            && password.isNotBlank() 
                                            && busId.isNotBlank() && stop.isNotBlank(),
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = rememberRipple(
                                        color = Color.White.copy(alpha = 0.3f),
                                        bounded = true
                                    )
                                ) {
                                    scope.launch {
                                        isLoading = true
                                        errorMessage = null
                                        successMessage = null
                                        
                                        val studentData = StudentData(
                                            name = name.trim(),
                                            username = username.trim(),
                                            busId = busId.trim(),
                                            stop = stop.trim()
                                        )
                                        
                                        when (val result = FirebaseManager.createStudentAccount(
                                            studentData = studentData,
                                            password = password
                                        )) {
                                            is StudentResult.Success -> {
                                                isLoading = false
                                                successMessage = "Student account created successfully!"
                                                // Navigate back after delay
                                                kotlinx.coroutines.delay(1000)
                                                onStudentAdded()
                                            }
                                            is StudentResult.Error -> {
                                                isLoading = false
                                                errorMessage = result.message
                                            }
                                        }
                                    }
                                }
                                .padding(horizontal = 24.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.Black,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_add),
                                    contentDescription = "Add",
                                    tint = Color.Black,
                                    modifier = Modifier.size(20.dp)
                                )
                                
                                Spacer(modifier = Modifier.width(8.dp))
                                
                                Text(
                                    text = "Create Student Account",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
    }
}

@Composable
private fun StudentFormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: Int,
    enabled: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    color = Color(0xFFAAAAAA)
                )
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = label,
                    tint = Color(0xFF888888)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF7DD3C0),
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color(0xFFFAFAFA)
            ),
            singleLine = true,
            enabled = enabled,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
        )
    }
}

@Composable
private fun StudentPasswordField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPasswordVisible: Boolean,
    onVisibilityToggle: () -> Unit,
    enabled: Boolean = true
) {
    Column {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    color = Color(0xFFAAAAAA)
                )
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_visibility_off),
                    contentDescription = "Password",
                    tint = Color(0xFF888888)
                )
            },
            trailingIcon = {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isPasswordVisible) Color(0xFF7DD3C0).copy(alpha = 0.15f)
                            else Color(0xFFF5F5F5)
                        )
                        .clickable { onVisibilityToggle() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_check),
                        contentDescription = if (isPasswordVisible) "Hide Password" else "Show Password",
                        tint = if (isPasswordVisible) Color(0xFF7DD3C0) else Color(0xFF888888),
                        modifier = Modifier.size(18.dp)
                    )
                }
            },
            visualTransformation = if (isPasswordVisible) VisualTransformation.None 
            else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF7DD3C0),
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color(0xFFFAFAFA)
            ),
            singleLine = true,
            enabled = enabled
        )
    }
}

@Composable
private fun AddStudentTopBar(
    onBackClick: () -> Unit
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
        
        Text(
            text = "Add Student",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )
    }
}
