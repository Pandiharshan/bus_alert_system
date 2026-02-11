package com.campusbussbuddy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import com.campusbussbuddy.R
import com.campusbussbuddy.firebase.AuthResult
import com.campusbussbuddy.firebase.FirebaseManager
import kotlinx.coroutines.launch

@Composable
fun AdminLoginScreen(
    onBackClick: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    var adminUsername by remember { mutableStateOf("") }
    var adminPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Handle login
    fun handleLogin() {
        if (adminUsername.isBlank() || adminPassword.isBlank()) {
            errorMessage = "Please fill in all fields"
            return
        }
        
        isLoading = true
        errorMessage = ""
        
        scope.launch {
            val result = FirebaseManager.authenticateAdmin(adminUsername, adminPassword)
            
            when (result) {
                is AuthResult.Success -> {
                    isLoading = false
                    Toast.makeText(context, "Welcome, Admin!", Toast.LENGTH_SHORT).show()
                    onLoginSuccess()
                }
                is AuthResult.Error -> {
                    isLoading = false
                    errorMessage = result.message
                    Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Bar
            TopBar(onBackClick = onBackClick)
            
            // Main Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))
                
                // Bus Icon
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(
                            Color(0xFFB8A9D9).copy(alpha = 0.3f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_directions_bus_vector),
                        contentDescription = "Bus Icon",
                        modifier = Modifier.size(50.dp),
                        tint = Color(0xFFB8A9D9)
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Welcome Text
                Text(
                    text = "Admin Access",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Sign in to manage the system",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF666666),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(48.dp))
                
                // Login Form Card
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
                        containerColor = Color.White.copy(alpha = 0.95f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Admin Username Field
                        Text(
                            text = "Username",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        )
                        
                        OutlinedTextField(
                            value = adminUsername,
                            onValueChange = { 
                                adminUsername = it
                                errorMessage = ""
                            },
                            placeholder = {
                                Text(
                                    text = "Enter username",
                                    color = Color(0xFFAAAAAA)
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_person),
                                    contentDescription = "Username",
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
                                unfocusedContainerColor = Color(0xFFFAFAFA),
                                errorBorderColor = Color(0xFFE57373)
                            ),
                            singleLine = true,
                            enabled = !isLoading,
                            isError = errorMessage.isNotEmpty()
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Admin Password Field
                        Text(
                            text = "Admin Password",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        )
                        
                        OutlinedTextField(
                            value = adminPassword,
                            onValueChange = { 
                                adminPassword = it
                                errorMessage = ""
                            },
                            placeholder = {
                                Text(
                                    text = "Enter password",
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
                                        .clickable { isPasswordVisible = !isPasswordVisible },
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
                                unfocusedContainerColor = Color(0xFFFAFAFA),
                                errorBorderColor = Color(0xFFE57373)
                            ),
                            singleLine = true,
                            enabled = !isLoading,
                            isError = errorMessage.isNotEmpty()
                        )
                        
                        // Error Message
                        if (errorMessage.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = errorMessage,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFFE57373),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(40.dp))
                        
                        // Login Button
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
                                    if (isLoading) Color(0xFF7DD3C0).copy(alpha = 0.6f)
                                    else Color(0xFF7DD3C0).copy(alpha = 0.9f),
                                    RoundedCornerShape(28.dp)
                                )
                                .clip(RoundedCornerShape(28.dp))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = if (!isLoading) rememberRipple(
                                        color = Color.White.copy(alpha = 0.3f),
                                        bounded = true
                                    ) else null,
                                    enabled = !isLoading
                                ) { handleLogin() }
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
                                
                                Spacer(modifier = Modifier.width(12.dp))
                                
                                Text(
                                    text = "Authenticating...",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Black
                                )
                            } else {
                                Text(
                                    text = "Access Dashboard",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Black
                                )
                                
                                Spacer(modifier = Modifier.width(8.dp))
                                
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_chevron_right),
                                    contentDescription = "Access",
                                    tint = Color.Black,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Bottom App Version
                Text(
                    text = "CAMPUS BUDDY V2.4.0",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFAAAAAA),
                    letterSpacing = 1.sp,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun TopBar(
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
            text = "Admin Login",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )
    }
}