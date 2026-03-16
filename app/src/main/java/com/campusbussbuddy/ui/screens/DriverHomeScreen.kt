package com.campusbussbuddy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.shadow
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
import com.campusbussbuddy.ui.theme.GlassBackground
import com.campusbussbuddy.ui.components.*
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

    GlassBackground {
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.isLoading -> {
                    // Loading State
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFF6B9A92)
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
                        Icon(
                            painter = painterResource(id = R.drawable.ic_notifications),
                            contentDescription = "Error",
                            tint = Color(0xFF4A5F5F),
                            modifier = Modifier.size(48.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = uiState.errorMessage ?: "Something went wrong",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF4A5F5F),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        PrimaryActionButton(
                            text = "Retry",
                            onClick = { driverViewModel.refresh() }
                        )
                    }
                }

                else -> {
                    // Success State — show driver portal
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(32.dp))

                        // Top Label Pill - "CAMPUS TRANSPORT SYSTEM"
                        TopLabelPill()

                        Spacer(modifier = Modifier.height(32.dp))

                        // Profile Photo — loaded from Firebase photoUrl
                        DriverProfilePhoto(uiState.driverInfo)

                        Spacer(modifier = Modifier.height(20.dp))

                        // Driver Portal Title
                        SectionTitle(
                            text = "Driver Portal",
                            fontSize = 32
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Shift Status — dynamic from Firebase
                        val shiftText = if (uiState.driverInfo?.shift?.isNotEmpty() == true) {
                            uiState.driverInfo!!.shift.uppercase()
                        } else {
                            "SHIFT ACTIVE"
                        }
                        SectionSubtitle(text = shiftText)

                        Spacer(modifier = Modifier.height(32.dp))

                        // Assigned Bus Card — dynamic from Firebase
                        AssignedBusCard(
                            driverInfo = uiState.driverInfo,
                            busInfo = uiState.busInfo
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Bus Login Button (reusable action button)
                        PrimaryActionButton(
                            text = "Bus Login",
                            onClick = onBusLoginClick,
                            iconRes = R.drawable.ic_directions_bus_vector
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Bottom Navigation Bar (reusable component)
                        BottomNavBar {
                            BottomNavItem(
                                icon = R.drawable.ic_home,
                                label = "HOME",
                                isSelected = true,
                                onClick = { }
                            )

                            BottomNavItem(
                                icon = R.drawable.ic_history,
                                label = "HISTORY",
                                onClick = { }
                            )

                            BottomNavItem(
                                icon = R.drawable.ic_route,
                                label = "ROUTES",
                                onClick = { }
                            )

                            BottomNavItem(
                                icon = R.drawable.ic_settings,
                                label = "SETTINGS",
                                onClick = onSettingsClick
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun TopLabelPill() {
    Row(
        modifier = Modifier
            .shadow(elevation = 0.dp, shape = RoundedCornerShape(50))
            .background(
                Color.White.copy(alpha = 0.55f),
                RoundedCornerShape(50)
            )
            .border(
                1.dp,
                Color.White.copy(alpha = 0.7f),
                RoundedCornerShape(50)
            )
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_directions_bus_vector),
            contentDescription = null,
            tint = Color(0xFF2d6464),
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = "CAMPUS TRANSPORT SYSTEM",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A),
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
private fun DriverProfilePhoto(driverInfo: DriverInfo?) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .size(140.dp)
            .shadow(
                elevation = 8.dp,
                shape = CircleShape,
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.08f)
            )
            .background(
                Color.White.copy(alpha = 0.22f),
                CircleShape
            )
            .border(
                1.5.dp,
                Color.White.copy(alpha = 0.50f),
                CircleShape
            ),
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
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                loading = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(30.dp),
                            color = Color(0xFF6B9A92),
                            strokeWidth = 2.dp
                        )
                    }
                },
                error = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_person),
                        contentDescription = "Default Profile",
                        modifier = Modifier.size(70.dp),
                        tint = Color(0xFF6B9A92)
                    )
                }
            )
        } else {
            Icon(
                painter = painterResource(id = R.drawable.ic_person),
                contentDescription = "Default Profile",
                modifier = Modifier.size(70.dp),
                tint = Color(0xFF6B9A92)
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

    GlassCardContainer {
        // Header Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                SectionSubtitle(text = "ASSIGNED BUS")

                Spacer(modifier = Modifier.height(4.dp))

                SectionTitle(
                    text = busLabel,
                    fontSize = 28
                )
            }

            // Bus Icon Circle
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        Color.White.copy(alpha = 0.6f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_directions_bus_vector),
                    contentDescription = "Bus",
                    tint = Color(0xFF2A2A2A),
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Next Stop — dynamic route name from Firebase
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

        Spacer(modifier = Modifier.height(16.dp))

        // Alert Message Box
        AlertMessageBox(
            message = "\"Road closure on East Ave. Please use University Blvd detour for the next 2 hours.\""
        )
    }
}
