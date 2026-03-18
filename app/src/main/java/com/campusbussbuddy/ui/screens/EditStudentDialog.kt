package com.campusbussbuddy.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.campusbussbuddy.R
import com.campusbussbuddy.firebase.StudentInfo
import com.campusbussbuddy.ui.theme.NeumorphAccentPrimary
import com.campusbussbuddy.ui.theme.NeumorphSurface
import com.campusbussbuddy.ui.theme.NeumorphTextPrimary
import com.campusbussbuddy.ui.theme.NeumorphTextSecondary
import com.campusbussbuddy.ui.neumorphism.buttons.NeumorphismButton
import com.campusbussbuddy.ui.neumorphism.inputs.NeumorphismTextField
import com.campusbussbuddy.ui.theme.*

@Composable
fun EditStudentDialog(
    student: StudentInfo,
    onDismiss: () -> Unit,
    onSave: (StudentInfo, String?) -> Unit,
    errorMessage: String? = null
) {
    var name by remember { mutableStateOf(student.name) }
    var username by remember { mutableStateOf(student.username) }
    var busId by remember { mutableStateOf(student.busId) }
    var stop by remember { mutableStateOf(student.stop) }
    
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    val alpha by animateFloatAsState(if (visible) 1f else 0f, tween(300), label = "a")
    val scale by animateFloatAsState(if (visible) 1f else 0.88f, tween(300), label = "s")
    
    val scrollState = rememberScrollState()

    Dialog(
        onDismissRequest = { visible = false; onDismiss() },
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true, usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.45f * alpha))
                .clickable(remember { MutableInteractionSource() }, null) { visible = false; onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp)
                    .scale(scale)
                    .alpha(alpha)
                    .neumorphic(
                        cornerRadius = 24.dp,
                        elevation = 4.dp,
                        blur = 6.dp,
                        lightShadowColor = Color.Transparent,
                        darkShadowColor = Color.Black.copy(alpha = 0.15f)
                    )
                    .background(NeumorphSurface, RoundedCornerShape(24.dp))
                    .clickable(remember { MutableInteractionSource() }, null) { } // prevent clicks from closing
            ) {
                // Subtle purple bottom accent (same as Sign In button)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color.Transparent, NeumorphAccentPrimary.copy(alpha = 0.15f))
                            ),
                            shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                        )
                )

                Column(
                    modifier = Modifier.fillMaxWidth().padding(28.dp).verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Edit Student",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeumorphTextPrimary
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))

                    NeumorphismTextField(
                        value = name,
                        onValueChange = { name = it },
                        placeholder = "Full Name",
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_person),
                                contentDescription = "Name",
                                tint = NeumorphTextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    NeumorphismTextField(
                        value = username,
                        onValueChange = { },
                        placeholder = "Username",
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_person),
                                contentDescription = "Username",
                                tint = NeumorphTextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        enabled = false
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    NeumorphismTextField(
                        value = busId,
                        onValueChange = { busId = it },
                        placeholder = "Bus ID (e.g., B-101)",
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_directions_bus_vector),
                                contentDescription = "Bus ID",
                                tint = NeumorphTextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    NeumorphismTextField(
                        value = stop,
                        onValueChange = { stop = it },
                        placeholder = "Bus Stop",
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_pin_drop),
                                contentDescription = "Stop",
                                tint = NeumorphTextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    )

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = errorMessage,
                            fontSize = 13.sp,
                            color = Color(0xFFE53935),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = "Note: Username and email cannot be changed.",
                        fontSize = 11.sp,
                        color = NeumorphTextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxWidth().height(48.dp).clickable { visible = false; onDismiss() }, contentAlignment = Alignment.Center) {
                            Text("Cancel", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = NeumorphTextSecondary)
                        }
                        
                        NeumorphismButton(
                            text = "Save Changes",
                            onClick = {
                                val updatedStudent = student.copy(
                                    name = name.trim(),
                                    username = username.trim(),
                                    busId = busId.trim(),
                                    stop = stop.trim()
                                )
                                onSave(updatedStudent, null)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}
