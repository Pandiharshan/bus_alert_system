package com.campusbussbuddy.ui.screens.driver

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusLoginScreen(
    navController: NavHostController
) {
    val primary = Color(0xFF0DF26C)
    val backgroundDark = Color(0xFF102217)
    
    var busId by remember { mutableStateOf("") }
    var busPassword by remember { mutableStateOf("") }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundDark)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
            
            item {
                // Header
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Bus Login",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    
                    Text(
                        text = "Enter your bus credentials to continue",
                        color = Color(0xFF9CBAA8),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
            
            item {
                // Bus ID Field
                Column {
                    Text(
                        text = "BUS ID",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    OutlinedTextField(
                        value = busId,
                        onValueChange = { busId = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { 
                            Text(
                                "Enter Bus ID (e.g., BUS-102)",
                                color = Color(0xFF9CBAA8)
                            ) 
                        },
                        trailingIcon = {
                            Text("🚌", fontSize = 20.sp)
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primary.copy(alpha = 0.5f),
                            unfocusedBorderColor = Color(0xFF3B5445),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }
            
            item {
                // Bus Password Field
                Column {
                    Text(
                        text = "BUS PASSWORD",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    OutlinedTextField(
                        value = busPassword,
                        onValueChange = { busPassword = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { 
                            Text(
                                "••••••••",
                                color = Color(0xFF9CBAA8)
                            ) 
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        trailingIcon = {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = null,
                                tint = Color(0xFF9CBAA8)
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primary.copy(alpha = 0.5f),
                            unfocusedBorderColor = Color(0xFF3B5445),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
            
            item {
                // Login Button
                Button(
                    onClick = { 
                        // Navigate to driver bus home
                        navController.navigate("driver_bus_home")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primary,
                        contentColor = backgroundDark
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = backgroundDark
                        )
                        Text(
                            text = "Login to Bus",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            item {
                // Help Text
                Text(
                    text = "Need help? Contact your fleet manager",
                    color = Color(0xFF9CBAA8),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}