package com.campusbussbuddy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.campusbussbuddy.R
import com.campusbussbuddy.firebase.StudentData
import com.campusbussbuddy.firebase.StudentResult
import com.campusbussbuddy.firebase.FirebaseManager
import com.campusbussbuddy.ui.theme.GlassBackground
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
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    
    GlassBackground {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Bar
            TopBar(
                onBackClick = onBackClick
            )
            
            // Form Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Student Icon
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFB8D4D1)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_person),
                        contentDescription = "Student",
                        tint = Color(0xFF6B9090),
                        modifier = Modifier.size(70.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Form Card
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
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Full Name
                        AddField(
                            value = name,
                            onValueChange = { 
                                name = it
                                errorMessage = null
                            },
                            placeholder = "Full Name",
                            icon = R.drawable.ic_person
                        )
                        
                        // Username
                        AddField(
                            value = username,
                            onValueChange = { 
                                username = it
                                errorMessage = null
                            },
                            placeholder = "Username",
                            icon = R.drawable.ic_person
                        )
                        
                        // Password
                        AddField(
                            value = password,
                            onValueChange = { 
                                password = it
                                errorMessage = null
                            },
                            placeholder = "Password",
                            icon = R.drawable.ic_visibility_off,
                            keyboardType = KeyboardType.Password
                        )
                        
                        // Bus ID
                        AddField(
                            value = busId,
                            onValueChange = { 
                                busId = it
                                errorMessage = null
                            },
                            placeholder = "Bus ID",
                            icon = R.drawable.ic_directions_bus_vector
                        )
                        
                        // Bus Stop
                        AddField(
                            value = stop,
                            onValueChange = { 
                                stop = it
                                errorMessage = null
                            },
                            placeholder = "Bus Stop",
                            icon = R.drawable.ic_pin_drop
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Error Message
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        fontSize = 13.sp,
                        color = Color(0xFFD32F2F)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                // Success Message
                if (successMessage != null) {
                    Text(
                        text = successMessage!!,
                        fontSize = 13.sp,
                        color = Color(0xFF4CAF50)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Create Student Button
                Button(
                    onClick = {
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
                                    kotlinx.coroutines.delay(1000)
                                    onStudentAdded()
                                }
                                is StudentResult.Error -> {
                                    isLoading = false
                                    errorMessage = result.message
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50),
                        contentColor = Color.White
                    ),
                    enabled = !isLoading && name.isNotBlank() && username.isNotBlank() 
                            && password.isNotBlank() && busId.isNotBlank() && stop.isNotBlank()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Add Student",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AddField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: Int,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholder,
                color = Color(0xFF7A9B9B),
                fontSize = 14.sp
            )
        },
        leadingIcon = {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = placeholder,
                tint = Color(0xFF6B9090),
                modifier = Modifier.size(20.dp)
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(26.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            focusedContainerColor = Color.White.copy(alpha = 0.8f),
            unfocusedContainerColor = Color.White.copy(alpha = 0.8f),
            focusedTextColor = Color(0xFF2E2E2E),
            unfocusedTextColor = Color(0xFF2E2E2E),
            cursorColor = Color(0xFF5A9A8A)
        ),
        textStyle = androidx.compose.ui.text.TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
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
            text = "Add New Student",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A)
        )
    }
}
