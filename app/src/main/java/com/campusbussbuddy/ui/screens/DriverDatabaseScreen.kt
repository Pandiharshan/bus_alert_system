package com.campusbussbuddy.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ripple.rememberRipple
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
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.campusbussbuddy.R
import com.campusbussbuddy.firebase.DriverData
import com.campusbussbuddy.firebase.DriverInfo
import com.campusbussbuddy.firebase.DriverResult
import com.campusbussbuddy.firebase.FirebaseManager
import kotlinx.coroutines.launch

@Composable
fun DriverDatabaseScreen(
    onBackClick: () -> Unit,
    onAddDriverClick: () -> Unit
) {
    var drivers by remember { mutableStateOf<List<DriverInfo>>(emptyList()) }
    var filteredDrivers by remember { mutableStateOf<List<DriverInfo>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedDriver by remember { mutableStateOf<DriverInfo?>(null) }
    var isDeleting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val scope = rememberCoroutineScope()
    
    // Load drivers on screen launch
    LaunchedEffect(Unit) {
        scope.launch {
            drivers = FirebaseManager.getAllDrivers()
            filteredDrivers = drivers
            isLoading = false
        }
    }
    
    // Filter drivers based on search query
    LaunchedEffect(searchQuery, drivers) {
        filteredDrivers = if (searchQuery.isBlank()) {
            drivers
        } else {
            drivers.filter { driver ->
                driver.name.contains(searchQuery, ignoreCase = true) ||
                driver.email.contains(searchQuery, ignoreCase = true) ||
                driver.phone.contains(searchQuery, ignoreCase = true)
            }
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Bar
            TopBar(
                onBackClick = onBackClick,
                onAddClick = onAddDriverClick,
                driverCount = drivers.size
            )
            
            // Search Bar
            SearchBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
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
            } else if (filteredDrivers.isEmpty()) {
                // Empty State
                EmptyState(
                    isSearching = searchQuery.isNotBlank(),
                    onAddClick = onAddDriverClick
                )
            } else {
                // Drivers List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredDrivers) { driver ->
                        DriverCard(
                            driver = driver,
                            onEditClick = {
                                selectedDriver = driver
                                showEditDialog = true
                            },
                            onDeleteClick = {
                                selectedDriver = driver
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
        }
        
        // Edit Driver Dialog
        if (showEditDialog && selectedDriver != null) {
            EditDriverDialog(
                driver = selectedDriver!!,
                onDismiss = {
                    showEditDialog = false
                    selectedDriver = null
                },
                onSave = { updatedDriver, newPassword ->
                    scope.launch {
                        // Update driver in Firebase
                        val result = FirebaseManager.updateDriverInfo(updatedDriver, newPassword)
                        if (result is DriverResult.Success) {
                            // Refresh list
                            drivers = FirebaseManager.getAllDrivers()
                            showEditDialog = false
                            selectedDriver = null
                        } else if (result is DriverResult.Error) {
                            errorMessage = result.message
                        }
                    }
                }
            )
        }
        
        // Delete Confirmation Dialog
        if (showDeleteDialog && selectedDriver != null) {
            DeleteConfirmationDialog(
                driverName = selectedDriver!!.name,
                isDeleting = isDeleting,
                errorMessage = errorMessage,
                onConfirm = {
                    scope.launch {
                        isDeleting = true
                        errorMessage = null
                        
                        when (val result = FirebaseManager.deleteDriverAccount(selectedDriver!!.uid)) {
                            is DriverResult.Success -> {
                                // Remove from local list
                                drivers = drivers.filter { it.uid != selectedDriver!!.uid }
                                isDeleting = false
                                showDeleteDialog = false
                                selectedDriver = null
                            }
                            is DriverResult.Error -> {
                                errorMessage = result.message
                                isDeleting = false
                            }
                        }
                    }
                },
                onDismiss = {
                    if (!isDeleting) {
                        showDeleteDialog = false
                        selectedDriver = null
                        errorMessage = null
                    }
                }
            )
        }
    }
}

@Composable
private fun SearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = "Search by name, email, or phone...",
                color = Color(0xFFAAAAAA),
                fontSize = 14.sp
            )
        },
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_person),
                contentDescription = "Search",
                tint = Color(0xFF888888),
                modifier = Modifier.size(20.dp)
            )
        },
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                IconButton(
                    onClick = { onSearchQueryChange("") }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_remove),
                        contentDescription = "Clear",
                        tint = Color(0xFF888888),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        },
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF7DD3C0),
            unfocusedBorderColor = Color(0xFFE0E0E0),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        ),
        singleLine = true
    )
}

@Composable
private fun EmptyState(
    isSearching: Boolean,
    onAddClick: () -> Unit
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
                painter = painterResource(id = R.drawable.ic_group),
                contentDescription = if (isSearching) "No Results" else "No Drivers",
                modifier = Modifier.size(80.dp),
                tint = Color(0xFFCCCCCC)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = if (isSearching) "No Drivers Found" else "No Drivers Yet",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF666666)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = if (isSearching) "Try a different search term" else "Add your first driver to get started",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF888888),
                textAlign = TextAlign.Center
            )
            
            if (!isSearching) {
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = onAddClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF7DD3C0)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_add),
                        contentDescription = "Add",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Driver")
                }
            }
        }
    }
}

@Composable
private fun DriverCard(
    driver: DriverInfo,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val context = LocalContext.current
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black.copy(alpha = 0.06f),
                spotColor = Color.Black.copy(alpha = 0.06f)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.95f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Driver Photo
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2E4A5A))
                    .border(2.dp, Color.White.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                val photoUrl = driver.photoUrl
                
                if (photoUrl.isNotEmpty() && photoUrl.isNotBlank()) {
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(photoUrl)
                            .crossfade(true)
                            .memoryCacheKey(photoUrl)
                            .diskCacheKey(photoUrl)
                            .build(),
                        contentDescription = "Driver Photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        loading = {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
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
                                    modifier = Modifier.size(30.dp),
                                    tint = Color.White
                                )
                            }
                        }
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_person),
                        contentDescription = "Default",
                        modifier = Modifier.size(30.dp),
                        tint = Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Driver Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = driver.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = driver.email,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF666666)
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_call),
                        contentDescription = "Phone",
                        modifier = Modifier.size(12.dp),
                        tint = Color(0xFF888888)
                    )
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    Text(
                        text = driver.phone,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF888888)
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Icon(
                        painter = painterResource(id = R.drawable.ic_directions_bus_vector),
                        contentDescription = "Bus",
                        modifier = Modifier.size(12.dp),
                        tint = Color(0xFF7DD3C0)
                    )
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    Text(
                        text = if (driver.assignedBusId.isNotEmpty()) driver.assignedBusId else "No bus",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF888888)
                    )
                    
                    if (driver.isActive) {
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(Color(0xFF4CAF50), CircleShape)
                        )
                        
                        Spacer(modifier = Modifier.width(4.dp))
                        
                        Text(
                            text = "Active",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF4CAF50)
                        )
                    }
                }
            }
            
            // Action Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Edit Button
                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_settings),
                        contentDescription = "Edit Driver",
                        tint = Color(0xFF7DD3C0),
                        modifier = Modifier.size(18.dp)
                    )
                }
                
                // Delete Button
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_remove),
                        contentDescription = "Delete Driver",
                        tint = Color(0xFFD32F2F),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TopBar(
    onBackClick: () -> Unit,
    onAddClick: () -> Unit,
    driverCount: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_back_vector),
                contentDescription = "Back",
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Driver Database",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
            
            Text(
                text = "$driverCount ${if (driverCount == 1) "driver" else "drivers"}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF888888)
            )
        }
        
        // Add Driver Button
        IconButton(
            onClick = onAddClick,
            modifier = Modifier
                .size(40.dp)
                .background(
                    Color(0xFF7DD3C0).copy(alpha = 0.15f),
                    CircleShape
                )
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_add),
                contentDescription = "Add Driver",
                tint = Color(0xFF7DD3C0),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun DeleteConfirmationDialog(
    driverName: String,
    isDeleting: Boolean,
    errorMessage: String?,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Delete Driver?",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = "Are you sure you want to permanently delete $driverName?",
                    fontSize = 14.sp
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "This will remove:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF666666)
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "• Driver profile from database\n• Driver photo from storage\n• Bus assignment\n• Authentication account",
                    fontSize = 12.sp,
                    color = Color(0xFF888888),
                    lineHeight = 18.sp
                )
                
                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = errorMessage,
                        fontSize = 12.sp,
                        color = Color(0xFFD32F2F)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = !isDeleting
            ) {
                if (isDeleting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = Color(0xFFD32F2F)
                    )
                } else {
                    Text(
                        text = "Delete",
                        color = Color(0xFFD32F2F),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isDeleting
            ) {
                Text(
                    text = "Cancel",
                    color = Color(0xFF666666)
                )
            }
        }
    )
}
