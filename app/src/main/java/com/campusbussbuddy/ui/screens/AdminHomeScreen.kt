package com.campusbussbuddy.ui.screens

import android.util.Log
import androidx.compose.animation.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.campusbussbuddy.R
import com.campusbussbuddy.firebase.FirebaseManager
import com.campusbussbuddy.ui.theme.*
import com.campusbussbuddy.ui.neumorphism.cards.NeumorphismCard
import com.google.firebase.firestore.FirebaseFirestore

data class AdminStudent(val id: String, val name: String, val status: String)
data class AdminDriver(val id: String, val name: String)
data class AdminBus(val id: String, val busNumber: String)

// ─── Admin Home Screen (Unified Neumorphic) ───────────────────────────────────
@Composable
fun AdminHomeScreen(
    onManageDriversClick: () -> Unit = {},
    onManageBusesClick: () -> Unit = {},
    onManageStudentsClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    var studentsList by remember { mutableStateOf(emptyList<AdminStudent>()) }
    var driversList  by remember { mutableStateOf(emptyList<AdminDriver>()) }
    var busesList    by remember { mutableStateOf(emptyList<AdminBus>()) }
    var isLoading    by remember { mutableStateOf(true) }

    // CENTRAL STATE CONTROL
    var currentPage  by remember { mutableStateOf("HOME") }
    var selectedCard by remember { mutableStateOf("students") } // Default selection for Board view

    val firestore = FirebaseFirestore.getInstance()

    // Fetch dashboard counts from Firestore with Realtime Listeners
    DisposableEffect(Unit) {
        val stListener = firestore.collection("students").addSnapshotListener { snapshot, error ->
            if (error == null && snapshot != null) {
                studentsList = snapshot.documents.map { doc ->
                    AdminStudent(
                        id = doc.id,
                        name = doc.getString("name") ?: "Unknown",
                        status = doc.getString("status") ?: "WAITING"
                    )
                }
                isLoading = false
            }
        }
        val drListener = firestore.collection("drivers").addSnapshotListener { snapshot, error ->
            if (error == null && snapshot != null) {
                driversList = snapshot.documents.map { doc ->
                    AdminDriver(
                        id = doc.id,
                        name = doc.getString("name") ?: "Unknown"
                    )
                }
            }
        }
        val bsListener = firestore.collection("buses").addSnapshotListener { snapshot, error ->
            if (error == null && snapshot != null) {
                busesList = snapshot.documents.map { doc ->
                    AdminBus(
                        id = doc.id,
                        busNumber = doc.getLong("busNumber")?.toString() ?: doc.getString("busNumber") ?: "Unknown"
                    )
                }
            }
        }

        onDispose {
            stListener.remove()
            drListener.remove()
            bsListener.remove()
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
                totalStudents = studentsList.size,
                totalDrivers  = driversList.size,
                totalBuses    = busesList.size,
                isLoading     = isLoading,
                // Only highlight card if we are in BOARD view
                selectedCard  = if (currentPage == "BOARD") selectedCard else null,
                onCardClick   = { card -> 
                    selectedCard = card
                    currentPage = "BOARD" // Clicking any card switches to BOARD view
                }
            )

            // ── UI MAPPING based on currentPage ───────────────────────────────
            AnimatedContent(
                targetState = currentPage,
                transitionSpec = {
                    fadeIn(tween(300)) togetherWith fadeOut(tween(200))
                },
                label = "page_transition"
            ) { state ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    if (state == "HOME") {
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
                    } else if (state == "BOARD") {
                        // ── Expanded Details Section ──────────────────────────────────────
                        ExpandedDetailsSection(
                            selectedCard = selectedCard,
                            studentsList = studentsList,
                            driversList  = driversList,
                            busesList    = busesList
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }

        // ── Bottom Navigation Bar (fixed) ─────────────────────────────────────
        BottomNavigationBar(
            selectedTab   = if (currentPage == "HOME") 0 else 1,
            onHomeClick   = { currentPage = "HOME" },   // ALWAYS sets to HOME
            onBoardClick  = { currentPage = "BOARD" },  // Sets to BOARD
            onLogoutClick = { onLogoutClick() },
            modifier      = Modifier.align(Alignment.BottomCenter)
        )
    }
}

// ─── Top Header Section ───────────────────────────────────────────────────────
@Composable
private fun TopHeaderSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Admin avatar — raised neumorphic circle (same icon as UnifiedLoginScreen admin role)
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
@Composable
private fun StatsSection(
    totalStudents: Int,
    totalDrivers: Int,
    totalBuses: Int,
    isLoading: Boolean,
    selectedCard: String?,
    onCardClick: (String) -> Unit
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
                count      = totalStudents,
                label      = "Students",
                icon       = R.drawable.ic_student,
                isSelected = selectedCard == "students",
                onClick    = { onCardClick("students") },
                modifier   = Modifier.weight(1f)
            )
            StatCard(
                count      = totalDrivers,
                label      = "Drivers",
                icon       = R.drawable.ic_person,
                isSelected = selectedCard == "drivers",
                onClick    = { onCardClick("drivers") },
                modifier   = Modifier.weight(1f)
            )
            StatCard(
                count      = totalBuses,
                label      = "Buses",
                icon       = R.drawable.ic_directions_bus_vector,
                isSelected = selectedCard == "buses",
                onClick    = { onCardClick("buses") },
                modifier   = Modifier.weight(1f)
            )
        }
    }
}

// ─── Stat Card ────────────────────────────────────────────────────────────────
@Composable
private fun StatCard(
    count: Int,
    label: String,
    icon: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .bounceClick { onClick() }
            .then(
                if (isSelected) Modifier.neumorphicInset(cornerRadius = 22.dp, elevation = 4.dp, blur = 8.dp)
                else Modifier.neumorphic(cornerRadius = 22.dp, elevation = 6.dp, blur = 12.dp)
            )
            .background(if (isSelected) Color(0xFFDCDCDC) else NeumorphSurface, RoundedCornerShape(22.dp))
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

// ─── Expanded Details Section (Interactive List) ──────────────────────────────
@Composable
private fun ExpandedDetailsSection(
    selectedCard: String,
    studentsList: List<AdminStudent>,
    driversList: List<AdminDriver>,
    busesList: List<AdminBus>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
            .animateContentSize()
    ) {
        NeumorphismCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            cornerRadius = 22.dp,
            contentPadding = PaddingValues(20.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                AnimatedContent(
                    targetState = selectedCard,
                    transitionSpec = {
                        fadeIn(tween(200)) togetherWith fadeOut(tween(200))
                    },
                    label = "tab_content_transition"
                ) { card ->
                    Column(modifier = Modifier.fillMaxWidth()) {
                        when (card) {
                            "students" -> {
                                val present = studentsList.filter { it.status == "BOARDED" }
                                val absent = studentsList.filter { it.status == "WAITING" }

                                Text("Present Students (${present.size})", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NeumorphTextPrimary)
                                Spacer(modifier = Modifier.height(8.dp))
                                if (present.isEmpty()) Text("None", fontSize = 14.sp, color = NeumorphTextSecondary)
                                present.forEach { 
                                    Text("• ${it.name}", fontSize = 14.sp, color = NeumorphTextSecondary, modifier = Modifier.padding(vertical = 2.dp)) 
                                }

                                Spacer(modifier = Modifier.height(20.dp))
                                
                                Text("Absent Students (${absent.size})", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NeumorphTextPrimary)
                                Spacer(modifier = Modifier.height(8.dp))
                                if (absent.isEmpty()) Text("None", fontSize = 14.sp, color = NeumorphTextSecondary)
                                absent.forEach { 
                                    Text("• ${it.name}", fontSize = 14.sp, color = NeumorphTextSecondary, modifier = Modifier.padding(vertical = 2.dp)) 
                                }
                            }
                            "drivers" -> {
                                Text("All Drivers (${driversList.size})", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NeumorphTextPrimary)
                                Spacer(modifier = Modifier.height(8.dp))
                                if (driversList.isEmpty()) Text("None", fontSize = 14.sp, color = NeumorphTextSecondary)
                                driversList.forEach { 
                                    Text("• ${it.name}", fontSize = 14.sp, color = NeumorphTextSecondary, modifier = Modifier.padding(vertical = 2.dp)) 
                                }
                            }
                            "buses" -> {
                                Text("All Buses (${busesList.size})", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NeumorphTextPrimary)
                                Spacer(modifier = Modifier.height(8.dp))
                                if (busesList.isEmpty()) Text("None", fontSize = 14.sp, color = NeumorphTextSecondary)
                                busesList.forEach { 
                                    Text("• Bus #${it.busNumber}", fontSize = 14.sp, color = NeumorphTextSecondary, modifier = Modifier.padding(vertical = 2.dp)) 
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─── Management Section ───────────────────────────────────────────────────────
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

            Text(
                text       = title,
                fontSize   = 16.sp,
                fontWeight = FontWeight.Bold,
                color      = NeumorphTextPrimary,
                modifier   = Modifier.weight(1f)
            )

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
@Composable
fun BottomNavigationBar(
    selectedTab: Int,
    onHomeClick: () -> Unit,
    onBoardClick: () -> Unit,
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
            // Replaced settings icon with admin role icon and renamed to Board
            NavItem(
                icon       = R.drawable.ic_admin_panel,
                label      = "Board",
                isSelected = selectedTab == 1,
                onClick    = onBoardClick
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
fun NavItem(
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
