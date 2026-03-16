package com.campusbussbuddy.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.campusbussbuddy.R
import com.campusbussbuddy.ui.theme.GlassBackground
import com.campusbussbuddy.ui.components.*
import com.campusbussbuddy.ui.viewmodel.SettingsViewModel

@Composable
fun DriverSettingsScreen(
    onBackClick: () -> Unit = {},
    settingsViewModel: SettingsViewModel = viewModel()
) {
    val uiState by settingsViewModel.uiState.collectAsState()

    GlassBackground {
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFF6B9A92)
                    )
                }

                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Top Bar
                        TopBarLayout(
                            title = "Settings",
                            onBackClick = onBackClick
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(modifier = Modifier.height(16.dp))

                            // Success / Error Messages
                            if (uiState.successMessage != null) {
                                GlassCardContainer(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_check_circle),
                                            contentDescription = "Success",
                                            tint = Color(0xFF4CAF50),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = uiState.successMessage!!,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF2E7D32)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }

                            if (uiState.errorMessage != null) {
                                GlassCardContainer(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_notifications),
                                            contentDescription = "Error",
                                            tint = Color(0xFFE53935),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = uiState.errorMessage!!,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFFC62828)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }

                            // ── Profile Information Section ──
                            SectionTitle(
                                text = "Profile Information",
                                fontSize = 18
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            GlassCardContainer {
                                Column(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    // Driver Name Field
                                    SectionSubtitle(text = "DRIVER NAME")
                                    Spacer(modifier = Modifier.height(8.dp))
                                    SettingsTextField(
                                        value = uiState.name,
                                        onValueChange = { settingsViewModel.onNameChange(it) },
                                        placeholder = "Enter your name",
                                        leadingIcon = R.drawable.ic_person,
                                        enabled = !uiState.isUpdatingProfile
                                    )

                                    Spacer(modifier = Modifier.height(20.dp))

                                    // Phone Number Field
                                    SectionSubtitle(text = "PHONE NUMBER")
                                    Spacer(modifier = Modifier.height(8.dp))
                                    SettingsTextField(
                                        value = uiState.phone,
                                        onValueChange = { settingsViewModel.onPhoneChange(it) },
                                        placeholder = "Enter phone number",
                                        leadingIcon = R.drawable.ic_call,
                                        keyboardType = KeyboardType.Phone,
                                        enabled = !uiState.isUpdatingProfile
                                    )

                                    Spacer(modifier = Modifier.height(24.dp))

                                    // Update Profile Button
                                    PrimaryActionButton(
                                        text = if (uiState.isUpdatingProfile) "Updating..." else "Update Profile",
                                        onClick = { settingsViewModel.updateProfile() }
                                    )

                                    if (uiState.isUpdatingProfile) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        LinearProgressIndicator(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(3.dp),
                                            color = Color(0xFF4CAF50),
                                            trackColor = Color.White.copy(alpha = 0.3f)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(32.dp))

                            // ── Change Password Section ──
                            SectionTitle(
                                text = "Change Password",
                                fontSize = 18
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            GlassCardContainer {
                                Column(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    // New Password Field
                                    SectionSubtitle(text = "NEW PASSWORD")
                                    Spacer(modifier = Modifier.height(8.dp))
                                    SettingsTextField(
                                        value = uiState.newPassword,
                                        onValueChange = { settingsViewModel.onNewPasswordChange(it) },
                                        placeholder = "Enter new password",
                                        leadingIcon = R.drawable.ic_visibility_off,
                                        isPassword = true,
                                        enabled = !uiState.isChangingPassword
                                    )

                                    Spacer(modifier = Modifier.height(20.dp))

                                    // Confirm Password Field
                                    SectionSubtitle(text = "CONFIRM PASSWORD")
                                    Spacer(modifier = Modifier.height(8.dp))
                                    SettingsTextField(
                                        value = uiState.confirmPassword,
                                        onValueChange = { settingsViewModel.onConfirmPasswordChange(it) },
                                        placeholder = "Confirm new password",
                                        leadingIcon = R.drawable.ic_visibility_off,
                                        isPassword = true,
                                        enabled = !uiState.isChangingPassword
                                    )

                                    Spacer(modifier = Modifier.height(24.dp))

                                    // Change Password Button
                                    PrimaryActionButton(
                                        text = if (uiState.isChangingPassword) "Changing..." else "Change Password",
                                        onClick = { settingsViewModel.changePassword() }
                                    )

                                    if (uiState.isChangingPassword) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        LinearProgressIndicator(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(3.dp),
                                            color = Color(0xFF4CAF50),
                                            trackColor = Color.White.copy(alpha = 0.3f)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(40.dp))
                        }
                    }
                }
            }
        }
    }
}

/**
 * Glass-styled text field for the settings screen.
 * Matches the BusAssignmentLoginScreen text field styling.
 */
@Composable
private fun SettingsTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: Int,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
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
        visualTransformation = if (isPassword) PasswordVisualTransformation() 
            else androidx.compose.ui.text.input.VisualTransformation.None,
        modifier = modifier
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
