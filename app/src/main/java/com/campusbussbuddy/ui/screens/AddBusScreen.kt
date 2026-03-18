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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.campusbussbuddy.R
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
fun AddBusScreen(
    onBackClick: () -> Unit,
    onBusAdded: () -> Unit
) {
    var busNumber by remember { mutableStateOf("") }
    var capacity by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    
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
                    icon = R.drawable.ic_directions_bus_vector,
                    title = "Add New Bus"
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
                // Bus Icon (Neumorphic Style)
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .neumorphic(cornerRadius = 60.dp, elevation = 6.dp, blur = 12.dp)
                        .background(NeumorphSurface, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_directions_bus_vector),
                        contentDescription = "Bus",
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
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Bus Number
                        NeumorphismTextField(
                            value = busNumber,
                            onValueChange = { 
                                busNumber = it
                                errorMessage = null
                            },
                            placeholder = "Bus Number",
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_directions_bus_vector),
                                    contentDescription = null,
                                    tint = NeumorphTextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            enabled = !isLoading
                        )
                        
                        // Capacity
                        NeumorphismTextField(
                            value = capacity,
                            onValueChange = { 
                                capacity = it
                                errorMessage = null
                            },
                            placeholder = "Capacity",
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_person),
                                    contentDescription = null,
                                    tint = NeumorphTextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            enabled = !isLoading
                        )
                        
                        // Password
                        NeumorphismTextField(
                            value = password,
                            onValueChange = { 
                                password = it
                                errorMessage = null
                            },
                            placeholder = "Bus Password",
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_lock),
                                    contentDescription = null,
                                    tint = NeumorphTextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                    Icon(
                                        painter = painterResource(
                                            id = if (isPasswordVisible) R.drawable.ic_visibility
                                                 else R.drawable.ic_visibility_off
                                        ),
                                        contentDescription = null,
                                        tint = NeumorphTextSecondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            },
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None
                                                   else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            enabled = !isLoading
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Messages
                AnimatedVisibility(visible = errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        fontSize = 13.sp,
                        color = Color(0xFFE53935),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
                
                AnimatedVisibility(visible = successMessage != null) {
                    Text(
                        text = successMessage ?: "",
                        fontSize = 13.sp,
                        color = Color(0xFF4CAF50),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
                
                // Create Bus Button
                NeumorphismButton(
                    text = "Add Bus",
                    isLoading = isLoading,
                    onClick = {
                        if (isLoading) return@NeumorphismButton
                        when {
                            busNumber.isEmpty() -> { errorMessage = "Please enter bus number" }
                            capacity.isEmpty() -> { errorMessage = "Please enter capacity" }
                            password.isEmpty() -> { errorMessage = "Please enter bus password" }
                            capacity.toIntOrNull() == null -> { errorMessage = "Capacity must be a valid number" }
                            busNumber.toIntOrNull() == null -> { errorMessage = "Bus number must be a valid number" }
                            else -> {
                                errorMessage = null
                                isLoading = true
                                
                                scope.launch {
                                    try {
                                        val result = FirebaseManager.createBus(
                                            busNumber = busNumber.toInt(),
                                            capacity = capacity.toInt(),
                                            password = password
                                        )
                                        
                                        when (result) {
                                            is com.campusbussbuddy.firebase.BusResult.Success -> {
                                                successMessage = "Bus added successfully!"
                                                kotlinx.coroutines.delay(1000)
                                                onBusAdded()
                                            }
                                            is com.campusbussbuddy.firebase.BusResult.Error -> {
                                                errorMessage = result.message
                                                isLoading = false
                                            }
                                        }
                                    } catch (e: Exception) {
                                        errorMessage = "Failed to add bus: ${e.message}"
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
