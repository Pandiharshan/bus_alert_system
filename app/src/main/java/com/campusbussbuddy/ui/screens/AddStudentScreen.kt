package com.campusbussbuddy.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.campusbussbuddy.R
import com.campusbussbuddy.firebase.StudentData
import com.campusbussbuddy.firebase.StudentResult
import com.campusbussbuddy.firebase.BusInfo
import com.campusbussbuddy.firebase.FirebaseManager
import kotlinx.coroutines.tasks.await
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
    
    // Bus Dropdown State
    var buses by remember { mutableStateOf<List<BusInfo>>(emptyList()) }
    var selectedBus by remember { mutableStateOf<BusInfo?>(null) }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        val snapshot = FirebaseManager.firestore.collection("buses").get().await()
        buses = snapshot.documents.mapNotNull { doc ->
            try {
                BusInfo(
                    busId     = doc.id,
                    // busNumber is stored as int64 — use getLong() only, never getString()
                    busNumber = doc.getLong("busNumber")?.toInt() ?: 0,
                    capacity  = 0,
                    activeDriverId = ""
                )
            } catch (e: Exception) { null }
        }.sortedBy { it.busNumber }
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
                    icon = R.drawable.ic_student,
                    title = "Add New Student"
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Form Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Student Icon (Neumorphic Style)
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .neumorphic(cornerRadius = 60.dp, elevation = 6.dp, blur = 12.dp)
                        .background(NeumorphSurface, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_student),
                        contentDescription = "Student",
                        tint = NeumorphAccentPrimary,
                        modifier = Modifier.size(56.dp)
                    )
                }
                
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
                        // Full Name
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
                        
                        // Username
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
                        
                        // Password
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
                        
                        // Bus ID Dropdown
                        @OptIn(ExperimentalMaterial3Api::class)
                        ExposedDropdownMenuBox(
                            expanded = isDropdownExpanded,
                            onExpandedChange = { if (!isLoading) isDropdownExpanded = !isDropdownExpanded }
                        ) {
                            Box(modifier = Modifier.menuAnchor()) {
                                NeumorphismTextField(
                                    value = selectedBus?.let { "Bus ${it.busNumber}" } ?: "",
                                    onValueChange = {},
                                    placeholder = "Select Bus",
                                    leadingIcon = {
                                        androidx.compose.material3.Icon(
                                            painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_directions_bus_vector),
                                            contentDescription = "Bus",
                                            tint = NeumorphTextSecondary
                                        )
                                    },
                                    enabled = false
                                )
                                Box(modifier = Modifier.matchParentSize().clickable(enabled = !isLoading) { isDropdownExpanded = true })
                            }

                            ExposedDropdownMenu(
                                expanded = isDropdownExpanded,
                                onDismissRequest = { isDropdownExpanded = false },
                                modifier = Modifier.background(NeumorphSurface)
                            ) {
                                buses.forEach { bus ->
                                    DropdownMenuItem(
                                        text = { Text("Bus ${bus.busNumber}", color = NeumorphTextPrimary) },
                                        onClick = {
                                            selectedBus = bus
                                            busId = bus.busId // Store the actual Firebase Document ID 
                                            isDropdownExpanded = false
                                            errorMessage = null
                                        }
                                    )
                                }
                            }
                        }
                        
                        // Bus Stop
                        FormField(
                            value = stop,
                            onValueChange = { 
                                stop = it
                                errorMessage = null
                            },
                            placeholder = "Bus Stop",
                            icon = R.drawable.ic_pin_drop,
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
                
                // Create Student Button
                NeumorphismButton(
                    text = "Add Student",
                    isLoading = isLoading,
                    onClick = {
                        if (isLoading) return@NeumorphismButton
                        if (name.isBlank() || username.isBlank() || password.isBlank() || 
                            busId.isBlank() || stop.isBlank()) {
                            errorMessage = "Please fill in all fields"
                        } else {
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
                                        successMessage = "Student account created successfully!"
                                        kotlinx.coroutines.delay(1000)
                                        onStudentAdded()
                                    }
                                    is StudentResult.Error -> {
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
