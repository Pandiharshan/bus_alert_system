package com.campusbussbuddy.ui.screens

import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.campusbussbuddy.R
import com.campusbussbuddy.firebase.FirebaseManager
import com.campusbussbuddy.ui.theme.*

// ─── Admin Home Screen (Unified Neumorphic) ───────────────────────────────────
@Composable
fun AdminHomeScreen(
    onManageDriversClick: () -> Unit = {},
    onManageBusesClick: () -> Unit = {},
    onManageStudentsClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    var totalStudents by remember { mutableStateOf(0) }
    var totalDrivers  by remember { mutableStateOf(0) }
    var totalBuses    by remember { mutableStateOf(0) }
    var isLoading     by remember { mutableStateOf(true) }

    // Selected bottom nav tab
    var selectedTab by remember { mutableStateOf(0) }

    // Fetch dashboard counts from Firestore
    LaunchedEffect(Unit) {
        Log.d("AdminHomeScreen", "Fetching dashboard counts...")
        try {
            val students = FirebaseManager.getAllStudents()
            totalStudents = students.size

            val drivers = FirebaseManager.getAllDrivers()
            totalDrivers = drivers.size

            val buses = FirebaseManager.getAllBuses()
            totalBuses = buses.size

            isLoading = false
        } catch (e: Exception) {
            Log.e("AdminHomeScreen", "Failed to load dashboard data", e)
            isLoading = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NeumorphBgPrimary)
    ) {
        // Scrollable content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 100.dp), // space for fixed bottom nav
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // ── Top Header Section ────────────────────────────────────────────
            TopHeaderSection()

            Spacer(modifier = Modifier.height(32.dp))

            // ── Statistics Section ────────────────────────────────────────────
            StatsSection(
                totalStudents = totalStudents,
                totalDrivers  = totalDrivers,
                totalBuses    = totalBuses,
                isLoading     = isLoading
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ── "Management" label ────────────────────────────────────────────
            Text(
                text       = "Management",
                fontSize   = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color      = NeumorphTextSecondary,
                modifier   = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // ── Management Section ────────────────────────────────────────────
            ManagementSection(
                onManageDriversClick  = onManageDriversClick,
                onManageBusesClick    = onManageBusesClick,
                onManageStudentsClick = onManageStudentsClick
            )

            Spacer(modifier = Modifier.height(32.dp))
        }

        // ── Bottom Navigation Bar (fixed) ─────────────────────────────────────
        BottomNavigationBar(
            selectedTab = selectedTab,
            onHomeClick = { selectedTab = 0 },
            onSettingsClick = { selectedTab = 1 },
            onLogoutClick = {
                selectedTab = 2
                onLogoutClick()
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

// ─── Top Header Section ───────────────────────────────────────────────────────
// Welcome text + admin name + neumorphic avatar circle
@Composable
private fun TopHeaderSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Admin avatar — raised neumorphic circle
        Box(
            modifier = Modifier
                .size(90.dp)
                .neumorphic(cornerRadius = 45.dp, elevation = 6.dp, blur = 12.dp)
                .background(NeumorphSurface, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter            = painterResource(id = R.drawable.ic_admin_panel),
                contentDescription = "Admin",
                tint               = NeumorphAccentPrimary,
                modifier           = Modifier.size(42.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text       = "Welcome, Admin",
            fontSize   = 13.sp,
            fontWeight = FontWeight.Normal,
            color      = NeumorphTextSecondary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text          = "Pandiharshan",
            fontSize      = 26.sp,
            fontWeight    = FontWeight.ExtraBold,
            color         = NeumorphTextPrimary,
            letterSpacing = (-0.5).sp
        )
    }
}

// ─── Stats Section ────────────────────────────────────────────────────────────
// Row of 3 neumorphic stat cards
@Composable
private fun StatsSection(
    totalStudents: Int,
    totalDrivers: Int,
    totalBuses: Int,
    isLoading: Boolean
) {
    if (isLoading) {
        Box(
            modifier         = Modifier.fillMaxWidth().height(130.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color       = NeumorphAccentPrimary,
                strokeWidth = 2.5.dp,
                modifier    = Modifier.size(28.dp)
            )
        }
    } else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            StatCard(
                count    = totalStudents,
                label    = "Students",
                icon     = R.drawable.ic_student,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                count    = totalDrivers,
                label    = "Drivers",
                icon     = R.drawable.ic_person,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                count    = totalBuses,
                label    = "Buses",
                icon     = R.drawable.ic_directions_bus_vector,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

// ─── Stat Card ────────────────────────────────────────────────────────────────
// Single raised neumorphic stat card with icon, count, label
@Composable
private fun StatCard(
    count: Int,
    label: String,
    icon: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .neumorphic(cornerRadius = 22.dp, elevation = 6.dp, blur = 12.dp)
            .background(NeumorphSurface, RoundedCornerShape(22.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon in a small inset circle
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .neumorphicInset(cornerRadius = 20.dp, elevation = 3.dp, blur = 6.dp)
                    .background(Color(0xFFDCDCDC), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter            = painterResource(id = icon),
                    contentDescription = label,
                    tint               = NeumorphAccentPrimary,
                    modifier           = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text       = count.toString(),
                fontSize   = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color      = NeumorphTextPrimary
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text          = label,
                fontSize      = 11.sp,
                fontWeight    = FontWeight.SemiBold,
                color         = NeumorphTextSecondary,
                letterSpacing = 0.5.sp
            )
        }
    }
}

// ─── Management Section ───────────────────────────────────────────────────────
// 3 neumorphic management cards stacked vertically
@Composable
private fun ManagementSection(
    onManageDriversClick: () -> Unit,
    onManageBusesClick: () -> Unit,
    onManageStudentsClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        ManagementCard(
            icon    = R.drawable.ic_person,
            title   = "Driver Management",
            onClick = onManageDriversClick
        )
        ManagementCard(
            icon    = R.drawable.ic_directions_bus_vector,
            title   = "Bus Management",
            onClick = onManageBusesClick
        )
        ManagementCard(
            icon    = R.drawable.ic_student,
            title   = "Student Management",
            onClick = onManageStudentsClick
        )
    }
}

// ─── Management Card ──────────────────────────────────────────────────────────
// Raised neumorphic card with icon circle + title + chevron
@Composable
private fun ManagementCard(
    icon: Int,
    title: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .neumorphic(cornerRadius = 22.dp, elevation = 6.dp, blur = 12.dp)
            .background(NeumorphSurface, RoundedCornerShape(22.dp))
            .bounceClick { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Icon circle — raised
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .neumorphic(cornerRadius = 22.dp, elevation = 4.dp, blur = 8.dp)
                    .background(NeumorphSurface, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter            = painterResource(id = icon),
                    contentDescription = title,
                    tint               = NeumorphAccentPrimary,
                    modifier           = Modifier.size(22.dp)
                )
            }

            // Title
            Text(
                text       = title,
                fontSize   = 16.sp,
                fontWeight = FontWeight.Bold,
                color      = NeumorphTextPrimary,
                modifier   = Modifier.weight(1f)
            )

            // Chevron
            Icon(
                painter            = painterResource(id = R.drawable.ic_chevron_right),
                contentDescription = "Open",
                tint               = NeumorphTextSecondary,
                modifier           = Modifier.size(18.dp)
            )
        }
    }
}

// ─── Bottom Navigation Bar ────────────────────────────────────────────────────
// 3 neumorphic nav items: Home, Settings, Logout
// Selected = inset (pressed), Unselected = raised
@Composable
private fun BottomNavigationBar(
    selectedTab: Int,
    onHomeClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(NeumorphBgPrimary)
            .padding(horizontal = 32.dp, vertical = 18.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .neumorphic(cornerRadius = 28.dp, elevation = 6.dp, blur = 12.dp)
                .background(NeumorphSurface, RoundedCornerShape(28.dp))
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            NavItem(
                icon       = R.drawable.ic_home,
                label      = "Home",
                isSelected = selectedTab == 0,
                onClick    = onHomeClick
            )
            NavItem(
                icon       = R.drawable.ic_settings,
                label      = "Settings",
                isSelected = selectedTab == 1,
                onClick    = onSettingsClick
            )
            NavItem(
                icon       = R.drawable.ic_chevron_left,
                label      = "Logout",
                isSelected = selectedTab == 2,
                onClick    = onLogoutClick
            )
        }
    }
}

// ─── Nav Item ─────────────────────────────────────────────────────────────────
@Composable
private fun NavItem(
    icon: Int,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.bounceClick { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .then(
                    if (isSelected)
                        Modifier.neumorphicInset(cornerRadius = 22.dp, elevation = 4.dp, blur = 8.dp)
                    else
                        Modifier.neumorphic(cornerRadius = 22.dp, elevation = 4.dp, blur = 8.dp)
                )
                .background(
                    if (isSelected) Color(0xFFDCDCDC) else NeumorphSurface,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter            = painterResource(id = icon),
                contentDescription = label,
                tint               = if (isSelected) NeumorphAccentPrimary else NeumorphTextSecondary,
                modifier           = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text       = label,
            fontSize   = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color      = if (isSelected) NeumorphAccentPrimary else NeumorphTextSecondary
        )
    }
}
