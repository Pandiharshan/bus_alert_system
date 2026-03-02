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
import com.campusbussbuddy.ui.theme.GlassBackground
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
            isLoading = true
            buses = FirebaseManager.getAllBuses()
            Log.d("BusDatabaseScreen", "Loaded ${buses.size} buses")
            isLoading = false
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
    
    GlassBackground {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Top Bar
                TopBar(
                    onBackClick = onBackClick
                )
            
            // Search Bar
            SearchBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                placeholder = "Search by bus number...",
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
            )
            
            if (isLoading) {
                // Loading State
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFF7DD3C0)
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
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
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
            
            // Floating Add Button (bottom right)
            FloatingActionButton(
                onClick = onAddBusClick,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(28.dp)
                    .size(64.dp),
                containerColor = Color(0xFF6B9A92),
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_directions_bus_vector),
                        contentDescription = "Add Bus",
                        modifier = Modifier.size(24.dp)
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.ic_add),
                        contentDescription = "Add",
                        modifier = Modifier.size(20.dp).offset(x = (-4).dp)
                    )
                }
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
}

@Composable
private fun BusCard(
    bus: BusInfo,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Bus Icon
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .background(
                        Color(0xFFB8D4D1),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_directions_bus_vector),
                    contentDescription = "Bus",
                    tint = Color(0xFF6B9090),
                    modifier = Modifier.size(36.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Bus Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Bus ${bus.busNumber}",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A),
                    letterSpacing = 0.sp
                )
                
                Spacer(modifier = Modifier.height(2.dp))
                
                Text(
                    text = "Capacity: ${bus.capacity}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF4A5F5F)
                )
                
                if (bus.activeDriverName.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Driver: ${bus.activeDriverName}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )
                }
            }
            
            // Action Buttons - stacked vertically on the right
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Edit Button (settings icon)
                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_settings),
                        contentDescription = "Edit Bus",
                        tint = Color(0xFF2A2A2A),
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                // Delete Button (trash/remove icon)
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_remove),
                        contentDescription = "Delete Bus",
                        tint = Color(0xFF2A2A2A),
                        modifier = Modifier.size(24.dp)
                    )
                }
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
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(20.dp),
                    ambientColor = Color.Black.copy(alpha = 0.06f),
                    spotColor = Color.Black.copy(alpha = 0.06f)
                ),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.30f)
            ),
            border = BorderStroke(1.5.dp, Color.White.copy(alpha = 0.15f))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_directions_bus_vector),
                    contentDescription = "Delete",
                    tint = Color(0xFFFF5252),
                    modifier = Modifier.size(48.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Delete Bus $busNumber?",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "This action cannot be undone",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF888888),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel")
                    }
                    
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF5252)
                        )
                    ) {
                        Text("Delete")
                    }
                }
            }
        }
    }
}

@Composable
private fun TopBar(
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(Color.Transparent)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back button - using chevron left icon
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_chevron_left),
                contentDescription = "Back",
                tint = Color(0xFF2C3E3E),
                modifier = Modifier.size(32.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Title
        Text(
            text = "Bus Management",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A)
        )
    }
}

@Composable
private fun SearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        placeholder = {
            Text(
                text = placeholder,
                color = Color(0xFF7A9B9B),
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
            )
        },
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_directions_bus_vector),
                contentDescription = "Search",
                tint = Color(0xFF6B9090),
                modifier = Modifier.size(24.dp)
            )
        },
        shape = RoundedCornerShape(28.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            focusedContainerColor = Color(0xFFD9E8E6),
            unfocusedContainerColor = Color(0xFFD9E8E6),
            focusedTextColor = Color(0xFF2C3E3E),
            unfocusedTextColor = Color(0xFF2C3E3E),
            cursorColor = Color(0xFF5A9A8A)
        ),
        textStyle = androidx.compose.ui.text.TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF2C3E3E)
        ),
        singleLine = true
    )
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
            Icon(
                painter = painterResource(id = icon),
                contentDescription = title,
                modifier = Modifier.size(80.dp),
                tint = Color(0xFF8AAFA8)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF3A4F4F)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = subtitle,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF5A7070),
                textAlign = TextAlign.Center
            )
        }
    }
}
