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
import com.campusbussbuddy.firebase.BusInfo
import com.campusbussbuddy.ui.theme.NeumorphAccentPrimary
import com.campusbussbuddy.ui.theme.NeumorphSurface
import com.campusbussbuddy.ui.theme.NeumorphTextPrimary
import com.campusbussbuddy.ui.theme.NeumorphTextSecondary
import com.campusbussbuddy.ui.neumorphism.buttons.NeumorphismButton
import com.campusbussbuddy.ui.neumorphism.inputs.NeumorphismTextField
import com.campusbussbuddy.ui.theme.*

@Composable
fun EditBusDialog(
    bus: BusInfo,
    onDismiss: () -> Unit,
    onSave: (BusInfo) -> Unit
) {
    var busNumber by remember { mutableStateOf(bus.busNumber.toString()) }
    var capacity by remember { mutableStateOf(bus.capacity.toString()) }
    
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
                        text = "Edit Bus",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeumorphTextPrimary
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))

                    NeumorphismTextField(
                        value = busNumber,
                        onValueChange = { busNumber = it },
                        placeholder = "Bus Number",
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_directions_bus_vector),
                                contentDescription = "Bus Number",
                                tint = NeumorphTextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    NeumorphismTextField(
                        value = capacity,
                        onValueChange = { capacity = it },
                        placeholder = "Capacity",
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_person),
                                contentDescription = "Capacity",
                                tint = NeumorphTextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Note: Update bus number and capacity as needed.",
                        fontSize = 12.sp,
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
                                val updatedBus = bus.copy(
                                    busNumber = busNumber.toIntOrNull() ?: bus.busNumber,
                                    capacity = capacity.toIntOrNull() ?: bus.capacity
                                )
                                onSave(updatedBus)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}
