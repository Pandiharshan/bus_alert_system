package com.campusbussbuddy.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.campusbussbuddy.R
import com.campusbussbuddy.firebase.BusInfo
import com.campusbussbuddy.firebase.FirebaseManager
import com.campusbussbuddy.ui.theme.*
import com.campusbussbuddy.ui.neumorphism.cards.NeumorphismCard
import com.campusbussbuddy.ui.neumorphism.inputs.NeumorphismTextField
import com.campusbussbuddy.ui.neumorphism.buttons.NeumorphismButton
import com.campusbussbuddy.ui.neumorphism.buttons.NeumorphismIconButton
import com.campusbussbuddy.ui.neumorphism.layout.AppLabelPill
import com.campusbussbuddy.ui.neumorphism.layout.NeumorphismScreenContainer
import androidx.compose.ui.draw.alpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import kotlinx.coroutines.launch

@Composable
fun BusDatabaseScreen(
    onBackClick: () -> Unit,
    onAddBusClick: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var buses by remember { mutableStateOf<List<BusInfo>>(emptyList()) }
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedBus by remember { mutableStateOf<BusInfo?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var busToDelete by remember { mutableStateOf<BusInfo?>(null) }
    
    val scope = rememberCoroutineScope()
    
    // Load buses from Firestore
    fun loadBuses() {
        scope.launch {
            try {
                isLoading = true
                buses = FirebaseManager.getAllBuses()
                Log.d("BusDatabaseScreen", "Loaded ${buses.size} buses")
            } catch (e: Exception) {
                Log.e("BusDatabaseScreen", "Failed to load buses", e)
            } finally {
                isLoading = false
            }
        }
    }
    
    LaunchedEffect(Unit) {
        loadBuses()
    }
    
    // Filter buses based on search query
    val filteredBuses = remember(buses, searchQuery) {
        if (searchQuery.isEmpty()) {
            buses
        } else {
            buses.filter { bus ->
                bus.busNumber.toString().contains(searchQuery, ignoreCase = true) ||
                bus.busId.contains(searchQuery, ignoreCase = true)
            }
        }
    }
    
    NeumorphismScreenContainer {
        Column(
            modifier = Modifier.fillMaxSize()
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
                    icon = R.drawable.ic_directions_bus_vector,
                    title = "Bus Database"
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Search Bar
            Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                NeumorphismTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = "Search by bus number...",
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_directions_bus_vector),
                            contentDescription = "Search",
                            tint = NeumorphTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            if (isLoading) {
                // Loading State
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = NeumorphAccentPrimary
                    )
                }
            } else if (filteredBuses.isEmpty()) {
                // Empty State
                EmptyState(
                    icon = R.drawable.ic_directions_bus_vector,
                    title = if (searchQuery.isEmpty()) "No Buses Found" else "No Results",
                    subtitle = if (searchQuery.isEmpty()) 
                        "Add buses to get started" 
                    else 
                        "No buses match your search"
                )
            } else {
                // Bus List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 120.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    items(filteredBuses) { bus ->
                        BusCard(
                            bus = bus,
                            onEditClick = {
                                selectedBus = bus
                                showEditDialog = true
                            },
                            onDeleteClick = {
                                busToDelete = bus
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
        }
            
        // Floating Add Button
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .size(64.dp)
                .neumorphic(cornerRadius = 32.dp, elevation = 8.dp, blur = 16.dp)
                .background(NeumorphSurface, CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onAddBusClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_add),
                contentDescription = "Add",
                tint = NeumorphAccentPrimary,
                modifier = Modifier.size(28.dp)
            )
        }
            
            // Edit Dialog
            if (showEditDialog && selectedBus != null) {
                EditBusDialog(
                    bus = selectedBus!!,
                    onDismiss = { showEditDialog = false },
                    onSave = { updatedBus ->
                        scope.launch {
                            val result = FirebaseManager.updateBusInfo(updatedBus)
                            if (result is com.campusbussbuddy.firebase.BusResult.Success) {
                                loadBuses()
                                showEditDialog = false
                            }
                        }
                    }
                )
            }
            
            // Delete Confirmation Dialog
            if (showDeleteDialog && busToDelete != null) {
                DeleteConfirmationDialog(
                    busNumber = busToDelete!!.busNumber,
                    onDismiss = { showDeleteDialog = false },
                    onConfirm = {
                        scope.launch {
                            val result = FirebaseManager.deleteBus(busToDelete!!.busId)
                            if (result is com.campusbussbuddy.firebase.BusResult.Success) {
                                loadBuses()
                                showDeleteDialog = false
                            }
                        }
                    }
                )
            }
    }
}

@Composable
private fun BusCard(
    bus: BusInfo,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    NeumorphismCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Bus Icon
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .neumorphicInset(cornerRadius = 32.dp, blur = 8.dp)
                    .background(NeumorphSurface, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_directions_bus_vector),
                    contentDescription = "Bus",
                    tint = NeumorphAccentPrimary,
                    modifier = Modifier.size(28.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Bus Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Bus ${bus.busNumber}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeumorphTextPrimary
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "Capacity: ${bus.capacity}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = NeumorphTextSecondary
                )
                
                if (bus.activeDriverName.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Driver: ${bus.activeDriverName}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = NeumorphTextSecondary
                    )
                }
            }
            
            // Action Buttons
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Edit Button
                NeumorphismIconButton(
                    iconRes = R.drawable.ic_settings,
                    onClick = onEditClick,
                    size = 40.dp,
                    iconSize = 20.dp,
                    unselectedTint = NeumorphTextPrimary
                )
                
                // Delete Button
                NeumorphismIconButton(
                    iconRes = R.drawable.ic_remove,
                    onClick = onDeleteClick,
                    size = 40.dp,
                    iconSize = 20.dp,
                    unselectedTint = Color(0xFFE53935)
                )
            }
        }
    }
}

@Composable
private fun DeleteConfirmationDialog(
    busNumber: Int,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    val alpha by androidx.compose.animation.core.animateFloatAsState(if (visible) 1f else 0f, androidx.compose.animation.core.tween(300), label = "a")
    val scale by androidx.compose.animation.core.animateFloatAsState(if (visible) 1f else 0.88f, androidx.compose.animation.core.tween(300), label = "s")

    androidx.compose.ui.window.Dialog(
        onDismissRequest = { visible = false; onDismiss() },
        properties = androidx.compose.ui.window.DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true, usePlatformDefaultWidth = false)
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
                        elevation = 8.dp,
                        blur = 16.dp
                    )
                    .background(NeumorphSurface, RoundedCornerShape(24.dp))
                    .clickable(remember { MutableInteractionSource() }, null) { } // prevent clicks from closing
            ) {
                // Subtle purple bottom accent
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(Color.Transparent, NeumorphAccentPrimary.copy(alpha = 0.15f))
                            ),
                            shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .neumorphicInset(cornerRadius = 32.dp, blur = 12.dp)
                            .background(NeumorphBgPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Warning,
                            contentDescription = "Warning",
                            tint = Color(0xFFE53935), // Red warning color
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = "Delete Bus $busNumber?",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeumorphTextPrimary,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = "This action cannot be undone and will permanently remove this bus from the system.",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = NeumorphTextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxWidth().height(56.dp).clickable { onDismiss() }, contentAlignment = Alignment.Center) {
                            Text("Cancel", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = NeumorphTextSecondary)
                        }
                        NeumorphismButton(
                            text = "Delete Permanently",
                            onClick = onConfirm,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}
@Composable
private fun EmptyState(
    icon: Int,
    title: String,
    subtitle: String
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .neumorphicInset(cornerRadius = 50.dp, blur = 12.dp)
                    .background(NeumorphBgPrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = title,
                    modifier = Modifier.size(48.dp),
                    tint = NeumorphTextSecondary
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = NeumorphTextPrimary,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = subtitle,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = NeumorphTextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}
