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
import com.campusbussbuddy.ui.theme.*
import com.campusbussbuddy.ui.neumorphism.cards.NeumorphismCard
import com.campusbussbuddy.ui.neumorphism.inputs.NeumorphismTextField
import com.campusbussbuddy.ui.neumorphism.buttons.NeumorphismButton
import com.campusbussbuddy.ui.neumorphism.buttons.NeumorphismIconButton
import com.campusbussbuddy.ui.neumorphism.layout.AppLabelPill
import com.campusbussbuddy.ui.neumorphism.layout.NeumorphismScreenContainer
import com.campusbussbuddy.ui.theme.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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
    
    NeumorphismScreenContainer {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))
            
            // Top Bar
            Box(
                modifier = Modifier.fillMaxWidth(),
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
                    title = "Bus Login"
                )
            }
            
            Spacer(modifier = Modifier.height(64.dp))
            
            // Bus Icon Circle
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
            
            // Title
            Text(
                text = "Bus Assignment",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = NeumorphTextPrimary,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Subtitle
            Text(
                text = "Enter bus credentials to start operations",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = NeumorphTextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Login Card
            NeumorphismCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 24.dp,
                contentPadding = PaddingValues(top = 32.dp, bottom = 32.dp, start = 24.dp, end = 24.dp)
            ) {
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
                    AnimatedVisibility(visible = errorMessage != null) {
                        Column {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = errorMessage ?: "",
                                fontSize = 13.sp,
                                color = Color(0xFFE53935),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Enter Bus Button
                    NeumorphismButton(
                        text = "Enter Bus",
                        isLoading = isLoading,
                        onClick = {
                            if (isLoading) return@NeumorphismButton
                            if (busNumber.isBlank() || busPassword.isBlank()) {
                                errorMessage = "Please fill in all fields"
                                return@NeumorphismButton
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
                                        val driverId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
                                        
                                        // Only block if ANOTHER driver is currently active on this bus
                                        val activeDriverId = result.busInfo.activeDriverId
                                        if (activeDriverId.isNotEmpty() && activeDriverId != driverId) {
                                            // Ghost driver timeout: if lock is older than 12 hours, allow override
                                            val currentBusDoc = FirebaseManager.firestore.collection("buses").document(result.busInfo.busId).get().await()
                                            val tripStartedAt = currentBusDoc.getTimestamp("tripStartedAt")?.toDate()?.time ?: 0L
                                            val now = System.currentTimeMillis()
                                            val isStale = tripStartedAt > 0 && (now - tripStartedAt) > 12 * 60 * 60 * 1000L

                                            if (!isStale) {
                                                isLoading = false
                                                errorMessage = "Bus $busNumber is currently active with another driver. Please wait."
                                                return@launch
                                            } else {
                                                android.util.Log.w("BusLogin", "Ghost lock over 12hrs old bypassed on Bus $busNumber.")
                                            }
                                        }

                                        // Log the bus access attempt
                                        if (driverId.isNotEmpty()) {
                                            val logData = hashMapOf(
                                                "driverId" to driverId,
                                                "busId" to busNumberInt.toString(),
                                                "timestamp" to com.google.firebase.firestore.FieldValue.serverTimestamp()
                                            )
                                            FirebaseManager.firestore.collection("driver_logs").add(logData)
                                        }
                                        
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
    NeumorphismTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = placeholder,
        leadingIcon = {
            Icon(
                painter = painterResource(id = leadingIcon),
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
private fun BusPasswordField(
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