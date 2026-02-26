package com.campusbussbuddy.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.campusbussbuddy.R
import com.campusbussbuddy.firebase.DriverInfo

@Composable
fun EditDriverDialog(
    driver: DriverInfo,
    onDismiss: () -> Unit,
    onSave: (DriverInfo, String?) -> Unit,
    errorMessage: String? = null
) {
    var name by remember { mutableStateOf(driver.name) }
    var username by remember { mutableStateOf(driver.username) }
    var phone by remember { mutableStateOf(driver.phone) }
    var assignedBusId by remember { mutableStateOf(driver.assignedBusId) }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var currentPhotoUrl by remember { mutableStateOf(driver.photoUrl) }
    
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    
    // Photo picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        photoUri = uri
    }
    
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.85f)
                .shadow(
                    elevation = 0.dp,
                    shape = RoundedCornerShape(28.dp)
                ),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.95f)
            ),
            border = BorderStroke(0.dp, Color.Transparent)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header with close button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFD9E8E6))
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Edit Driver",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )
                    
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_remove),
                            contentDescription = "Close",
                            tint = Color(0xFF3A3A3A),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                
                // Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Photo Section
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFB8D4D1))
                            .border(2.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                            .clickable { photoPickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (photoUri != null) {
                            SubcomposeAsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(photoUri)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "New Photo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else if (currentPhotoUrl.isNotEmpty() && currentPhotoUrl.isNotBlank()) {
                            SubcomposeAsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(currentPhotoUrl)
                                    .crossfade(true)
                                    .memoryCacheKey(currentPhotoUrl)
                                    .diskCacheKey(currentPhotoUrl)
                                    .build(),
                                contentDescription = "Current Photo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                                loading = {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(30.dp),
                                            color = Color(0xFF5A9A8A),
                                            strokeWidth = 2.dp
                                        )
                                    }
                                },
                                error = {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_person),
                                        contentDescription = "Default",
                                        modifier = Modifier.size(50.dp),
                                        tint = Color(0xFF6B9090)
                                    )
                                }
                            )
                        } else {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_person),
                                contentDescription = "Default",
                                modifier = Modifier.size(50.dp),
                                tint = Color(0xFF6B9090)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = "Tap to change photo",
                        fontSize = 13.sp,
                        color = Color(0xFF5A7070)
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Form Fields in glass card
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
                            EditField(
                                value = name,
                                onValueChange = { name = it },
                                placeholder = "Full Name",
                                icon = R.drawable.ic_person
                            )
                            
                            // Username (disabled)
                            EditField(
                                value = username,
                                onValueChange = { },
                                placeholder = "Username",
                                icon = R.drawable.ic_person,
                                enabled = false
                            )
                            
                            // Email (auto-generated, disabled)
                            EditField(
                                value = "${username.trim()}@gmail.com",
                                onValueChange = { },
                                placeholder = "Email",
                                icon = R.drawable.ic_person,
                                enabled = false
                            )
                            
                            // Phone Number
                            EditField(
                                value = phone,
                                onValueChange = { phone = it },
                                placeholder = "Phone Number",
                                icon = R.drawable.ic_call,
                                keyboardType = KeyboardType.Phone
                            )
                            
                            // Assigned Bus ID
                            EditField(
                                value = assignedBusId,
                                onValueChange = { assignedBusId = it },
                                placeholder = "Bus ID (e.g., B-101)",
                                icon = R.drawable.ic_directions_bus_vector
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Info Note
                    Text(
                        text = "Note: Username and email cannot be changed after account creation.",
                        fontSize = 12.sp,
                        color = Color(0xFF5A7070),
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp
                    )
                    
                    // Error Message
                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = errorMessage,
                            fontSize = 13.sp,
                            color = Color(0xFFD32F2F),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                
                // Footer Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFD9E8E6))
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Cancel Button
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF3A3A3A)
                        ),
                        border = BorderStroke(1.5.dp, Color(0xFF8AAFA8))
                    ) {
                        Text(
                            text = "Cancel",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    
                    // Save Button
                    Button(
                        onClick = {
                            val updatedDriver = driver.copy(
                                name = name.trim(),
                                username = username.trim(),
                                phone = phone.trim(),
                                assignedBusId = assignedBusId.trim()
                            )
                            onSave(updatedDriver, null)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50),
                            disabledContainerColor = Color(0xFF4CAF50).copy(alpha = 0.5f)
                        ),
                        enabled = name.isNotBlank() && phone.isNotBlank()
                    ) {
                        Text(
                            text = "Save Changes",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EditField(
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
                fontSize = 14.sp
            )
        },
        leadingIcon = {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = placeholder,
                tint = if (enabled) Color(0xFF6B9090) else Color(0xFF9AAFAF),
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
            disabledBorderColor = Color.Transparent,
            focusedContainerColor = Color.White.copy(alpha = 0.8f),
            unfocusedContainerColor = Color.White.copy(alpha = 0.8f),
            disabledContainerColor = Color.White.copy(alpha = 0.5f),
            focusedTextColor = Color(0xFF2E2E2E),
            unfocusedTextColor = Color(0xFF2E2E2E),
            disabledTextColor = Color(0xFF7A7A7A),
            cursorColor = Color(0xFF5A9A8A)
        ),
        textStyle = androidx.compose.ui.text.TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal
        ),
        singleLine = true,
        enabled = enabled,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
    )
}
