package com.campusbussbuddy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.campusbussbuddy.R
import com.campusbussbuddy.firebase.BusInfo
import com.campusbussbuddy.firebase.DriverInfo
import com.campusbussbuddy.ui.theme.*
import com.campusbussbuddy.ui.neumorphism.cards.NeumorphismCard
import com.campusbussbuddy.ui.neumorphism.buttons.NeumorphismButton
import com.campusbussbuddy.ui.neumorphism.layout.AppLabelPill
import com.campusbussbuddy.ui.neumorphism.layout.NeumorphismPill
import com.campusbussbuddy.ui.neumorphism.layout.NeumorphismScreenContainer
import com.campusbussbuddy.ui.theme.*
import com.campusbussbuddy.ui.viewmodel.DriverViewModel

@Composable
fun DriverHomeScreen(
    onBusLoginClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    driverViewModel: DriverViewModel = viewModel()
) {
    // Collect UI state from ViewModel (StateFlow → Compose State)
    val uiState by driverViewModel.uiState.collectAsState()

    NeumorphismScreenContainer {
        when {
            uiState.isLoading -> {
                // Loading State
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = NeumorphAccentPrimary
                )
            }

            uiState.errorMessage != null -> {
                // Error State
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .neumorphicInset(cornerRadius = 40.dp, blur = 12.dp)
                            .background(NeumorphBgPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_notifications),
                            contentDescription = "Error",
                            tint = NeumorphTextSecondary,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = uiState.errorMessage ?: "Something went wrong",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = NeumorphTextPrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    NeumorphismButton(
                        text = "Retry",
                        onClick = { driverViewModel.refresh() },
                        modifier = Modifier.fillMaxWidth(0.6f)
                    )
                }
            }

            else -> {
                // Success State — show driver portal
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(48.dp))

                        // Top Label Pill
                        AppLabelPill(
                            icon = R.drawable.ic_directions_bus_vector,
                            title = "Campus Transport System"
                        )

                        Spacer(modifier = Modifier.height(48.dp))

                        // Profile Photo
                        DriverProfilePhoto(uiState.driverInfo)

                        Spacer(modifier = Modifier.height(24.dp))

                        // Driver Portal Title
                        Text(
                            text = "Driver Portal",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeumorphTextPrimary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Shift Status
                        val shiftText = if (uiState.driverInfo?.shift?.isNotEmpty() == true) {
                            uiState.driverInfo!!.shift.uppercase()
                        } else {
                            "SHIFT ACTIVE"
                        }
                        
                        NeumorphismPill(
                            label = shiftText,
                            onClick = {},
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // Assigned Bus Card
                        AssignedBusCard(
                            driverInfo = uiState.driverInfo,
                            busInfo = uiState.busInfo
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // Bus Login Button
                        NeumorphismButton(
                            text = "Bus Login",
                            onClick = onBusLoginClick,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Padding for scrollable content above bottom nav
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // Bottom Navigation Bar
                    BottomNavBar(
                        onHomeClick = {},
                        onSettingsClick = onSettingsClick
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun DriverProfilePhoto(driverInfo: DriverInfo?) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .size(140.dp)
            .neumorphicInset(cornerRadius = 70.dp, blur = 16.dp)
            .background(NeumorphBgPrimary, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        val photoUrl = driverInfo?.photoUrl?.trim() ?: ""

        if (photoUrl.isNotEmpty() && photoUrl.isNotBlank()) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(context)
                    .data(photoUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Driver Profile Photo",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp) // padding inside the inset to make it look like it's inside a well
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                loading = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(30.dp),
                            color = NeumorphAccentPrimary,
                            strokeWidth = 2.dp
                        )
                    }
                },
                error = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_person),
                        contentDescription = "Default Profile",
                        modifier = Modifier.size(70.dp),
                        tint = NeumorphTextSecondary
                    )
                }
            )
        } else {
            Icon(
                painter = painterResource(id = R.drawable.ic_person),
                contentDescription = "Default Profile",
                modifier = Modifier.size(70.dp),
                tint = NeumorphTextSecondary
            )
        }
    }
}

@Composable
private fun AssignedBusCard(
    driverInfo: DriverInfo?,
    busInfo: BusInfo?
) {
    // Determine display values from Firebase data
    val busLabel = when {
        busInfo != null -> "Route ${busInfo.busNumber}"
        driverInfo?.assignedBusId?.isNotEmpty() == true -> "Bus Assigned"
        else -> "No Bus Assigned"
    }

    val routeName = when {
        driverInfo?.routeName?.isNotEmpty() == true -> driverInfo.routeName
        else -> "North Campus Terminal"
    }

    NeumorphismCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 24.dp,
        contentPadding = PaddingValues(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ASSIGNED BUS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeumorphTextSecondary,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = busLabel,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeumorphTextPrimary
                    )
                }

                // Bus Icon Circle
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .neumorphicInset(cornerRadius = 28.dp, blur = 8.dp)
                        .background(NeumorphBgPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_directions_bus_vector),
                        contentDescription = "Bus",
                        tint = NeumorphAccentPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Next Stop
            InfoRow(
                iconRes = R.drawable.ic_pin_drop,
                label = "NEXT STOP",
                value = routeName
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Departure Time
            InfoRow(
                iconRes = R.drawable.ic_history,
                label = "DEPARTURE",
                value = "14:30 PM (In 12 mins)"
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Alert Message Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .neumorphicInset(cornerRadius = 16.dp, blur = 6.dp)
                    .background(NeumorphBgPrimary, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = "\"Road closure on East Ave. Please use University Blvd detour for the next 2 hours.\"",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = NeumorphTextPrimary,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }
    }
}

@Composable
private fun InfoRow(iconRes: Int, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .neumorphic(cornerRadius = 20.dp, elevation = 4.dp, blur = 8.dp)
                .background(NeumorphBgPrimary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = label,
                tint = NeumorphTextPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column {
            Text(
                text = label, 
                color = NeumorphTextSecondary, 
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            Text(
                text = value, 
                color = NeumorphTextPrimary, 
                fontSize = 15.sp, 
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun BottomNavBar(
    onHomeClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    NeumorphismCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
        cornerRadius = 24.dp,
        contentPadding = PaddingValues(0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                icon = R.drawable.ic_home,
                label = "HOME",
                isSelected = true,
                onClick = onHomeClick
            )

            BottomNavItem(
                icon = R.drawable.ic_history,
                label = "HISTORY",
                isSelected = false,
                onClick = { }
            )

            BottomNavItem(
                icon = R.drawable.ic_route,
                label = "ROUTES",
                isSelected = false,
                onClick = { }
            )

            BottomNavItem(
                icon = R.drawable.ic_settings,
                label = "SETTINGS",
                isSelected = false,
                onClick = onSettingsClick
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: Int,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(8.dp)
    ) {
        val color = if (isSelected) NeumorphAccentPrimary else NeumorphTextSecondary
        
        Icon(
            painter = painterResource(id = icon),
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = color,
            letterSpacing = 0.5.sp
        )
    }
}
