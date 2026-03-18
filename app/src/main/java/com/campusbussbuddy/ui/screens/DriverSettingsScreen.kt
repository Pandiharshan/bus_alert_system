package com.campusbussbuddy.ui.screens

import androidx.compose.foundation.background
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
import com.campusbussbuddy.ui.theme.*
import com.campusbussbuddy.ui.neumorphism.cards.NeumorphismCard
import com.campusbussbuddy.ui.neumorphism.inputs.NeumorphismTextField
import com.campusbussbuddy.ui.neumorphism.buttons.NeumorphismButton
import com.campusbussbuddy.ui.neumorphism.buttons.NeumorphismIconButton
import com.campusbussbuddy.ui.neumorphism.layout.AppLabelPill
import com.campusbussbuddy.ui.neumorphism.layout.NeumorphismScreenContainer
import com.campusbussbuddy.ui.viewmodel.SettingsViewModel

@Composable
fun DriverSettingsScreen(
    onBackClick: () -> Unit = {},
    settingsViewModel: SettingsViewModel = viewModel()
) {
    val uiState by settingsViewModel.uiState.collectAsState()

    NeumorphismScreenContainer {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = NeumorphAccentPrimary
                )
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
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
                            icon = R.drawable.ic_settings,
                            title = "Settings"
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(32.dp))

                        // Success / Error Messages
                        if (uiState.successMessage != null) {
                            NeumorphismCard(
                                modifier = Modifier.fillMaxWidth(),
                                cornerRadius = 16.dp,
                                contentPadding = PaddingValues(16.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_check_circle),
                                        contentDescription = "Success",
                                        tint = Color(0xFF4CAF50),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = uiState.successMessage!!,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = NeumorphTextPrimary
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        if (uiState.errorMessage != null) {
                            NeumorphismCard(
                                modifier = Modifier.fillMaxWidth(),
                                cornerRadius = 16.dp,
                                contentPadding = PaddingValues(16.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_notifications),
                                        contentDescription = "Error",
                                        tint = Color(0xFFE53935),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = uiState.errorMessage!!,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFFE53935)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Profile Information Section
                        Text(
                            text = "Profile Information",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeumorphTextPrimary,
                            modifier = Modifier.align(Alignment.Start)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        NeumorphismCard(
                            modifier = Modifier.fillMaxWidth(),
                            cornerRadius = 24.dp,
                            contentPadding = PaddingValues(24.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                // Driver Name Field
                                Text(
                                    text = "DRIVER NAME",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeumorphTextSecondary,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                NeumorphismTextField(
                                    value = uiState.name,
                                    onValueChange = { settingsViewModel.onNameChange(it) },
                                    placeholder = "Enter your name",
                                    leadingIcon = {
                                        Icon(painterResource(R.drawable.ic_person), null, tint = NeumorphTextSecondary, modifier = Modifier.size(20.dp))
                                    }
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                // Phone Number Field
                                Text(
                                    text = "PHONE NUMBER",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeumorphTextSecondary,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                NeumorphismTextField(
                                    value = uiState.phone,
                                    onValueChange = { settingsViewModel.onPhoneChange(it) },
                                    placeholder = "Enter phone number",
                                    leadingIcon = {
                                        Icon(painterResource(R.drawable.ic_call), null, tint = NeumorphTextSecondary, modifier = Modifier.size(20.dp))
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                                )

                                Spacer(modifier = Modifier.height(32.dp))

                                // Update Profile Button
                                NeumorphismButton(
                                    text = if (uiState.isUpdatingProfile) "Updating..." else "Update Profile",
                                    onClick = { if (!uiState.isUpdatingProfile) settingsViewModel.updateProfile() },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Change Password Section
                        Text(
                            text = "Change Password",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeumorphTextPrimary,
                            modifier = Modifier.align(Alignment.Start)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        NeumorphismCard(
                            modifier = Modifier.fillMaxWidth(),
                            cornerRadius = 24.dp,
                            contentPadding = PaddingValues(24.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                // New Password Field
                                Text(
                                    text = "NEW PASSWORD",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeumorphTextSecondary,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                NeumorphismTextField(
                                    value = uiState.newPassword,
                                    onValueChange = { settingsViewModel.onNewPasswordChange(it) },
                                    placeholder = "Enter new password",
                                    leadingIcon = {
                                        Icon(painterResource(R.drawable.ic_visibility_off), null, tint = NeumorphTextSecondary, modifier = Modifier.size(20.dp))
                                    },
                                    visualTransformation = PasswordVisualTransformation()
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                // Confirm Password Field
                                Text(
                                    text = "CONFIRM PASSWORD",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeumorphTextSecondary,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                NeumorphismTextField(
                                    value = uiState.confirmPassword,
                                    onValueChange = { settingsViewModel.onConfirmPasswordChange(it) },
                                    placeholder = "Confirm new password",
                                    leadingIcon = {
                                        Icon(painterResource(R.drawable.ic_visibility_off), null, tint = NeumorphTextSecondary, modifier = Modifier.size(20.dp))
                                    },
                                    visualTransformation = PasswordVisualTransformation()
                                )

                                Spacer(modifier = Modifier.height(32.dp))

                                // Change Password Button
                                NeumorphismButton(
                                    text = if (uiState.isChangingPassword) "Changing..." else "Change Password",
                                    onClick = { if (!uiState.isChangingPassword) settingsViewModel.changePassword() },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(40.dp))
                    }
                }
            }
        }
    }
}
