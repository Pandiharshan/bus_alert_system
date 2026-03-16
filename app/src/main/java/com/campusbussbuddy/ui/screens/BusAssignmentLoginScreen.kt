package com.campusbussbuddy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.campusbussbuddy.firebase.BusAuthResult
import com.campusbussbuddy.firebase.FirebaseManager
import com.campusbussbuddy.ui.theme.GlassBackground
import com.campusbussbuddy.ui.components.*
import kotlinx.coroutines.launch

@Composable
fun BusAssignmentLoginScreen(
    onBackClick: () -> Unit = {},
    onLoginSuccess: (String, String) -> Unit = { _, _ -> }
) {
    var busNumber by remember { mutableStateOf("") }
    var busPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val scope = rememberCoroutineScope()
    
    GlassBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            
            // Back Button - Top Left (reusable component)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                BackButtonCircle(onBackClick = onBackClick)
            }
            
            Spacer(modifier = Modifier.height(80.dp))
            
            // Bus Icon Circle
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = CircleShape,
                        ambientColor = Color.Black.copy(alpha = 0.08f),
                        spotColor = Color.Black.copy(alpha = 0.08f)
                    )
                    .background(
                        Color.White.copy(alpha = 0.25f),
                        CircleShape
                    )
                    .border(
                        2.dp,
                        Color.White.copy(alpha = 0.40f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_directions_bus_vector),
                    contentDescription = "Bus",
                    tint = Color(0xFF2A2A2A),
                    modifier = Modifier.size(48.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Title
            SectionTitle(
                text = "Bus Assignment Login",
                fontSize = 28
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Subtitle
            Text(
                text = "Enter bus credentials to start operations",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF4A5F5F),
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Login Card (reusable glass card)
            GlassCardContainer {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Bus Number Field
                    BusTextField(
                        value = busNumber,
                        onValueChange = { 
                            busNumber = it
                            errorMessage = null
                        },
                        placeholder = "Bus Number",
                        leadingIcon = R.drawable.ic_directions_bus_vector,
                        keyboardType = KeyboardType.Number,
                        enabled = !isLoading
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Bus Password Field
                    BusPasswordField(
                        value = busPassword,
                        onValueChange = { 
                            busPassword = it
                            errorMessage = null
                        },
                        placeholder = "Bus Password",
                        isPasswordVisible = isPasswordVisible,
                        onVisibilityToggle = { isPasswordVisible = !isPasswordVisible },
                        enabled = !isLoading
                    )
                    
                    // Error Message
                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = errorMessage!!,
                            fontSize = 13.sp,
                            color = Color(0xFFE53935),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Enter Bus Button (reusable action button)
                    PrimaryActionButton(
                        text = if (isLoading) "" else "Enter Bus",
                        onClick = {
                            if (busNumber.isBlank() || busPassword.isBlank()) {
                                errorMessage = "Please fill in all fields"
                                return@PrimaryActionButton
                            }
                            
                            scope.launch {
                                isLoading = true
                                errorMessage = null
                                
                                val busNumberInt = busNumber.toIntOrNull()
                                if (busNumberInt == null) {
                                    errorMessage = "Please enter a valid bus number"
                                    isLoading = false
                                    return@launch
                                }
                                
                                when (val result = FirebaseManager.authenticateBus(busNumberInt, busPassword)) {
                                    is BusAuthResult.Success -> {
                                        isLoading = false
                                        onLoginSuccess(busNumber, result.busInfo.busId)
                                    }
                                    is BusAuthResult.Error -> {
                                        isLoading = false
                                        errorMessage = result.message
                                    }
                                }
                            }
                        }
                    )
                    
                    // Show loading indicator over the button when loading
                    if (isLoading) {
                        Spacer(modifier = Modifier.height(8.dp))
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color(0xFF4CAF50),
                            strokeWidth = 2.dp
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun BusTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: Int,
    keyboardType: KeyboardType = KeyboardType.Text,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholder,
                color = Color(0xFF7A9B9B),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        },
        leadingIcon = {
            Icon(
                painter = painterResource(id = leadingIcon),
                contentDescription = null,
                tint = Color(0xFF6B9090),
                modifier = Modifier.size(20.dp)
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = RoundedCornerShape(27.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF6B9A92).copy(alpha = 0.30f),
            unfocusedBorderColor = Color.White.copy(alpha = 0.40f),
            focusedContainerColor = Color.White.copy(alpha = 0.6f),
            unfocusedContainerColor = Color.White.copy(alpha = 0.6f),
            focusedTextColor = Color(0xFF2A2A2A),
            unfocusedTextColor = Color(0xFF2A2A2A),
            cursorColor = Color(0xFF6B9A92)
        ),
        textStyle = androidx.compose.ui.text.TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.2.sp
        ),
        singleLine = true,
        enabled = enabled,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
    )
}

@Composable
private fun BusPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPasswordVisible: Boolean,
    onVisibilityToggle: () -> Unit,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholder,
                color = Color(0xFF7A9B9B),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        },
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_visibility_off),
                contentDescription = null,
                tint = Color(0xFF6B9090),
                modifier = Modifier.size(20.dp)
            )
        },
        trailingIcon = {
            IconButton(onClick = onVisibilityToggle) {
                Icon(
                    painter = painterResource(
                        id = if (isPasswordVisible) R.drawable.ic_visibility 
                        else R.drawable.ic_visibility_off
                    ),
                    contentDescription = if (isPasswordVisible) "Hide Password" else "Show Password",
                    tint = Color(0xFF6B9090),
                    modifier = Modifier.size(20.dp)
                )
            }
        },
        visualTransformation = if (isPasswordVisible) VisualTransformation.None 
        else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = RoundedCornerShape(27.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF6B9A92).copy(alpha = 0.30f),
            unfocusedBorderColor = Color.White.copy(alpha = 0.40f),
            focusedContainerColor = Color.White.copy(alpha = 0.6f),
            unfocusedContainerColor = Color.White.copy(alpha = 0.6f),
            focusedTextColor = Color(0xFF2A2A2A),
            unfocusedTextColor = Color(0xFF2A2A2A),
            cursorColor = Color(0xFF6B9A92)
        ),
        textStyle = androidx.compose.ui.text.TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.2.sp
        ),
        singleLine = true,
        enabled = enabled
    )
}