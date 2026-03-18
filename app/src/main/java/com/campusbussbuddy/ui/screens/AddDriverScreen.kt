package com.campusbussbuddy.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.campusbussbuddy.ui.theme.*
import com.campusbussbuddy.ui.neumorphism.cards.NeumorphismCard
import com.campusbussbuddy.ui.neumorphism.inputs.NeumorphismTextField
import com.campusbussbuddy.ui.neumorphism.buttons.NeumorphismButton
import com.campusbussbuddy.ui.neumorphism.buttons.NeumorphismIconButton
import com.campusbussbuddy.ui.neumorphism.layout.AppLabelPill
import com.campusbussbuddy.ui.neumorphism.layout.NeumorphismScreenContainer
import com.campusbussbuddy.ui.theme.*
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
    
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        photoUri = uri
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
                    icon = R.drawable.ic_person,
                    title = "Add New Driver"
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Main Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                
                // Photo Upload Section
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .neumorphic(cornerRadius = 60.dp, elevation = 6.dp, blur = 12.dp)
                        .background(NeumorphSurface, CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { photoPickerLauncher.launch("image/*") },
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
                            modifier = Modifier.size(56.dp),
                            tint = NeumorphAccentPrimary
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = if (photoUri != null) "Tap to change photo" else "Tap to add photo",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = NeumorphTextSecondary
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Form Card
                NeumorphismCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 24.dp,
                    contentPadding = PaddingValues(top = 32.dp, bottom = 32.dp, start = 24.dp, end = 24.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
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
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Error Message
                AnimatedVisibility(visible = errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        fontSize = 13.sp,
                        color = Color(0xFFE53935),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                    )
                }
                
                // Success Message
                AnimatedVisibility(visible = successMessage != null) {
                    Text(
                        text = successMessage ?: "",
                        fontSize = 13.sp,
                        color = Color(0xFF4CAF50),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                    )
                }
                
                // Create Driver Button
                NeumorphismButton(
                    text = "Add Driver",
                    isLoading = isLoading,
                    onClick = {
                        if (isLoading) return@NeumorphismButton
                        if (name.isBlank() || username.isBlank() || password.isBlank() || 
                            phone.isBlank() || assignedBusId.isBlank()) {
                            errorMessage = "Please fill in all fields"
                        } else {
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
                                        successMessage = "Driver account created successfully!"
                                        kotlinx.coroutines.delay(1000)
                                        onDriverAdded()
                                    }
                                    is DriverResult.Error -> {
                                        errorMessage = result.message
                                        isLoading = false
                                    }
                                }
                            }
                        }
                    }
                )
                
                Spacer(modifier = Modifier.height(32.dp))
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
    NeumorphismTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = placeholder,
        leadingIcon = {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = placeholder,
                tint = NeumorphTextSecondary,
                modifier = Modifier.size(20.dp)
            )
        },
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
    NeumorphismTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = placeholder,
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_lock),
                contentDescription = "Password",
                tint = NeumorphTextSecondary,
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
                    tint = NeumorphTextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        },
        visualTransformation = if (isPasswordVisible) VisualTransformation.None 
        else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        enabled = enabled
    )
}
