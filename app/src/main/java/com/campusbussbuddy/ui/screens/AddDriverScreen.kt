package com.campusbussbuddy.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.campusbussbuddy.R
import com.campusbussbuddy.firebase.DriverData
import com.campusbussbuddy.firebase.DriverResult
import com.campusbussbuddy.firebase.FirebaseManager
import com.campusbussbuddy.ui.theme.GlassBackground
import kotlinx.coroutines.launch

@Composable
fun AddDriverScreen(
    onBackClick: () -> Unit,
    onDriverAdded: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var assignedBusId by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    
    // Photo picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        photoUri = uri
    }
    
    // Use same background as UnifiedLoginScreen
    GlassBackground {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Bar
            TopBar(onBackClick = onBackClick)
            
            // Main Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                
                // Photo Upload Section - centered circular
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.3f))
                        .border(2.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                        .clickable { photoPickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (photoUri != null) {
                        AsyncImage(
                            model = photoUri,
                            contentDescription = "Driver Photo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_person),
                            contentDescription = "Add Photo",
                            modifier = Modifier.size(60.dp),
                            tint = Color(0xFF6B9090)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = if (photoUri != null) "Tap to change photo" else "Tap to add photo",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF5A7070)
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Form Card with glass effect
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 0.dp,
                            shape = RoundedCornerShape(28.dp)
                        ),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.25f)
                    ),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Full Name Field
                        FormField(
                            value = name,
                            onValueChange = { 
                                name = it
                                errorMessage = null
                            },
                            placeholder = "Full Name",
                            icon = R.drawable.ic_person,
                            enabled = !isLoading
                        )
                        
                        // Username Field
                        FormField(
                            value = username,
                            onValueChange = { 
                                username = it
                                errorMessage = null
                            },
                            placeholder = "Username",
                            icon = R.drawable.ic_person,
                            enabled = !isLoading
                        )
                        
                        // Password Field
                        PasswordField(
                            value = password,
                            onValueChange = { 
                                password = it
                                errorMessage = null
                            },
                            placeholder = "Password",
                            isPasswordVisible = isPasswordVisible,
                            onVisibilityToggle = { isPasswordVisible = !isPasswordVisible },
                            enabled = !isLoading
                        )
                        
                        // Phone Field
                        FormField(
                            value = phone,
                            onValueChange = { 
                                phone = it
                                errorMessage = null
                            },
                            placeholder = "Phone Number",
                            icon = R.drawable.ic_call,
                            enabled = !isLoading,
                            keyboardType = KeyboardType.Phone
                        )
                        
                        // Assigned Bus ID Field
                        FormField(
                            value = assignedBusId,
                            onValueChange = { 
                                assignedBusId = it
                                errorMessage = null
                            },
                            placeholder = "Bus ID (e.g., B-101)",
                            icon = R.drawable.ic_directions_bus_vector,
                            enabled = !isLoading
                        )
                    }
                }
                
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
                
                // Create Driver Button - bright green
                Button(
                    onClick = {
                        if (name.isBlank() || username.isBlank() || password.isBlank() || 
                            phone.isBlank() || assignedBusId.isBlank()) {
                            errorMessage = "Please fill in all fields"
                            return@Button
                        }
                        scope.launch {
                            isLoading = true
                            errorMessage = null
                            successMessage = null
                            
                            val driverData = DriverData(
                                name = name.trim(),
                                username = username.trim(),
                                phone = phone.trim(),
                                assignedBusId = assignedBusId.trim()
                            )
                            
                            when (val result = FirebaseManager.createDriverAccount(
                                driverData = driverData,
                                password = password,
                                photoUri = photoUri
                            )) {
                                is DriverResult.Success -> {
                                    isLoading = false
                                    successMessage = "Driver account created successfully!"
                                    kotlinx.coroutines.delay(1000)
                                    onDriverAdded()
                                }
                                is DriverResult.Error -> {
                                    isLoading = false
                                    errorMessage = result.message
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50),
                        disabledContainerColor = Color(0xFF4CAF50).copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(28.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Create Driver",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun FormField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: Int,
    enabled: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholder,
                color = Color(0xFF7A9B9B),
                fontSize = 15.sp
            )
        },
        leadingIcon = {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = placeholder,
                tint = Color(0xFF6B9090),
                modifier = Modifier.size(22.dp)
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(28.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            focusedContainerColor = Color.White.copy(alpha = 0.7f),
            unfocusedContainerColor = Color.White.copy(alpha = 0.7f),
            focusedTextColor = Color(0xFF2E2E2E),
            unfocusedTextColor = Color(0xFF2E2E2E),
            disabledTextColor = Color(0xFF2E2E2E),
            cursorColor = Color(0xFF5A9A8A)
        ),
        textStyle = androidx.compose.ui.text.TextStyle(
            fontSize = 15.sp,
            fontWeight = FontWeight.Normal
        ),
        singleLine = true,
        enabled = enabled,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
    )
}

@Composable
private fun PasswordField(
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
                fontSize = 15.sp
            )
        },
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_visibility_off),
                contentDescription = "Password",
                tint = Color(0xFF6B9090),
                modifier = Modifier.size(22.dp)
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
                    modifier = Modifier.size(22.dp)
                )
            }
        },
        visualTransformation = if (isPasswordVisible) VisualTransformation.None 
        else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(28.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            focusedContainerColor = Color.White.copy(alpha = 0.7f),
            unfocusedContainerColor = Color.White.copy(alpha = 0.7f),
            focusedTextColor = Color(0xFF2E2E2E),
            unfocusedTextColor = Color(0xFF2E2E2E),
            cursorColor = Color(0xFF5A9A8A)
        ),
        textStyle = androidx.compose.ui.text.TextStyle(
            fontSize = 15.sp,
            fontWeight = FontWeight.Normal
        ),
        singleLine = true,
        enabled = enabled
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
        
        Text(
            text = "Add Driver",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A),
            modifier = Modifier.weight(1f)
        )
    }
}
