package com.campusbussbuddy.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.campusbussbuddy.R
import com.campusbussbuddy.firebase.DriverInfo

@Composable
fun EditDriverDialog(
    driver: DriverInfo,
    onDismiss: () -> Unit,
    onSave: (DriverInfo, String?) -> Unit
) {
    var name by remember { mutableStateOf(driver.name) }
    var email by remember { mutableStateOf(driver.email) }
    var phone by remember { mutableStateOf(driver.phone) }
    var assignedBusId by remember { mutableStateOf(driver.assignedBusId) }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var currentPhotoUrl by remember { mutableStateOf(driver.photoUrl) }
    var newPassword by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var showPasswordChangeConfirm by remember { mutableStateOf(false) }
    
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    
    // Photo picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        photoUri = uri
    }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Edit Driver",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_remove),
                            contentDescription = "Close",
                            tint = Color(0xFF666666)
                        )
                    }
                }
                
                Divider(color = Color(0xFFE0E0E0))
                
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
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2E4A5A))
                            .border(3.dp, Color(0xFF7DD3C0).copy(alpha = 0.3f), CircleShape)
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
                                            color = Color.White,
                                            strokeWidth = 2.dp
                                        )
                                    }
                                },
                                error = {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_person),
                                            contentDescription = "Default",
                                            modifier = Modifier.size(50.dp),
                                            tint = Color.White
                                        )
                                    }
                                }
                            )
                        } else {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_person),
                                contentDescription = "Default",
                                modifier = Modifier.size(50.dp),
                                tint = Color.White
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Tap to change photo",
                        fontSize = 12.sp,
                        color = Color(0xFF888888)
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Form Fields
                    EditFormField(
                        label = "Full Name",
                        value = name,
                        onValueChange = { name = it },
                        placeholder = "Enter driver's full name",
                        icon = R.drawable.ic_person
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    EditFormField(
                        label = "Email",
                        value = email,
                        onValueChange = { email = it },
                        placeholder = "driver@example.com",
                        icon = R.drawable.ic_person,
                        keyboardType = KeyboardType.Email,
                        enabled = false // Email cannot be changed
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    EditFormField(
                        label = "Phone Number",
                        value = phone,
                        onValueChange = { phone = it },
                        placeholder = "+1234567890",
                        icon = R.drawable.ic_call,
                        keyboardType = KeyboardType.Phone
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    EditFormField(
                        label = "Assigned Bus ID",
                        value = assignedBusId,
                        onValueChange = { assignedBusId = it },
                        placeholder = "e.g., bus_01",
                        icon = R.drawable.ic_directions_bus_vector
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Password Change Section
                    Column {
                        Text(
                            text = "Change Password (Optional)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        
                        OutlinedTextField(
                            value = newPassword,
                            onValueChange = { newPassword = it },
                            placeholder = {
                                Text(
                                    text = "Enter new password (leave blank to keep current)",
                                    color = Color(0xFFAAAAAA),
                                    fontSize = 14.sp
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_visibility),
                                    contentDescription = "Password",
                                    tint = Color(0xFF888888),
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            trailingIcon = {
                                IconButton(
                                    onClick = { showPassword = !showPassword }
                                ) {
                                    Icon(
                                        painter = painterResource(
                                            id = if (showPassword) R.drawable.ic_visibility_off else R.drawable.ic_visibility
                                        ),
                                        contentDescription = if (showPassword) "Hide password" else "Show password",
                                        tint = Color(0xFF888888),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            },
                            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF7DD3C0),
                                unfocusedBorderColor = Color(0xFFE0E0E0),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color(0xFFFAFAFA)
                            ),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                        )
                        
                        if (newPassword.isNotEmpty() && newPassword.length < 6) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Password must be at least 6 characters",
                                fontSize = 11.sp,
                                color = Color(0xFFD32F2F)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Info Note
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Color(0xFFFFF3CD),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_notifications),
                            contentDescription = "Info",
                            tint = Color(0xFF856404),
                            modifier = Modifier.size(16.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text(
                            text = "Email cannot be changed. You can reset the driver's password here.",
                            fontSize = 11.sp,
                            color = Color(0xFF856404),
                            lineHeight = 16.sp
                        )
                    }
                }
                
                Divider(color = Color(0xFFE0E0E0))
                
                // Footer Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF666666)
                        )
                    ) {
                        Text("Cancel")
                    }
                    
                    Button(
                        onClick = {
                            // If password is being changed, show confirmation
                            if (newPassword.isNotEmpty() && newPassword.length >= 6) {
                                showPasswordChangeConfirm = true
                            } else {
                                val updatedDriver = driver.copy(
                                    name = name.trim(),
                                    phone = phone.trim(),
                                    assignedBusId = assignedBusId.trim()
                                )
                                onSave(updatedDriver, null)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF7DD3C0)
                        ),
                        enabled = name.isNotBlank() && phone.isNotBlank() && 
                                  (newPassword.isEmpty() || newPassword.length >= 6)
                    ) {
                        Text("Save Changes")
                    }
                }
            }
        }
        
        // Password Change Confirmation Dialog
        if (showPasswordChangeConfirm) {
            AlertDialog(
                onDismissRequest = { showPasswordChangeConfirm = false },
                title = {
                    Text(
                        text = "Change Password?",
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        text = "Are you sure you want to change the password for ${driver.name}? The driver will need to use the new password to log in.",
                        fontSize = 14.sp
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showPasswordChangeConfirm = false
                            val updatedDriver = driver.copy(
                                name = name.trim(),
                                phone = phone.trim(),
                                assignedBusId = assignedBusId.trim()
                            )
                            onSave(updatedDriver, newPassword)
                        }
                    ) {
                        Text(
                            text = "Change Password",
                            color = Color(0xFF7DD3C0),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showPasswordChangeConfirm = false }
                    ) {
                        Text(
                            text = "Cancel",
                            color = Color(0xFF666666)
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun EditFormField(
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
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = if (enabled) Color.Black else Color(0xFF888888),
            modifier = Modifier.padding(bottom = 6.dp)
        )
        
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    color = Color(0xFFAAAAAA),
                    fontSize = 14.sp
                )
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = label,
                    tint = if (enabled) Color(0xFF888888) else Color(0xFFCCCCCC),
                    modifier = Modifier.size(18.dp)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF7DD3C0),
                unfocusedBorderColor = Color(0xFFE0E0E0),
                disabledBorderColor = Color(0xFFE0E0E0),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color(0xFFFAFAFA),
                disabledContainerColor = Color(0xFFF5F5F5)
            ),
            singleLine = true,
            enabled = enabled,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
        )
    }
}
