package com.campusbussbuddy.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.campusbussbuddy.ui.theme.*
import com.campusbussbuddy.ui.neumorphism.cards.NeumorphismCard
import com.campusbussbuddy.ui.neumorphism.cards.NeumorphismStatCard
import com.campusbussbuddy.ui.neumorphism.buttons.NeumorphismButton
import com.campusbussbuddy.ui.neumorphism.buttons.NeumorphismIconButton
import com.campusbussbuddy.ui.neumorphism.layout.AppLabelPill
import com.campusbussbuddy.ui.neumorphism.layout.NeumorphismScreenContainer
import com.campusbussbuddy.ui.neumorphism.layout.NeumorphismPill
import com.google.firebase.firestore.FirebaseFirestore

enum class StudentStatus {
    WAITING_TO_BOARD,
    BOARDED
}

data class StudentMember(
    val id: String,
    val name: String,
    val initials: String,
    val status: StudentStatus,
    val phoneNumber: String = ""
)

@Composable
fun BusOperationsHubScreen(
    busNumber: String = "42-B",
    busId: String = "",
    onLogoutClick: () -> Unit = {},
    onStartTrip: () -> Unit = {}
) {
    var currentTab by remember { mutableStateOf("HOME") }
    var studentsList by remember { mutableStateOf<List<StudentMember>>(emptyList()) }
    val firestore = FirebaseFirestore.getInstance()

    DisposableEffect(busId) {
        if (busId.isEmpty()) return@DisposableEffect onDispose { }

        val listener = firestore.collection("students")
            .whereEqualTo("busId", busId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.map { doc ->
                        val name = doc.getString("name") ?: "Unknown"
                        val initials = name.split(" ").take(2).joinToString("") { it.take(1) }.uppercase()
                        val statusStr = doc.getString("status") ?: "WAITING"
                        val statusEnum = if (statusStr == "BOARDED") StudentStatus.BOARDED else StudentStatus.WAITING_TO_BOARD
                        val phone = doc.getString("phone") ?: ""
                        StudentMember(
                            id = doc.id,
                            name = name,
                            initials = initials,
                            status = statusEnum,
                            phoneNumber = phone
                        )
                    }
                    studentsList = list
                }
            }

        onDispose {
            listener.remove()
        }
    }

    val totalMembers = studentsList.size
    val presentToday = studentsList.count { it.status == StudentStatus.BOARDED }

    var driverName by remember { mutableStateOf("Alex Thompson") }
    var shiftInfo by remember { mutableStateOf("Morning Shift") }
    var routeInfo by remember { mutableStateOf("Route: North Campus → Science Park Terminal") }

    NeumorphismScreenContainer {
        Box(modifier = Modifier.fillMaxSize()) {
            
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(200)) },
                label = "hub_tab_transition",
                modifier = Modifier.fillMaxSize()
            ) { tab ->
                when (tab) {
                    "HOME" -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(modifier = Modifier.height(48.dp))

                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                NeumorphismIconButton(
                                    iconRes = R.drawable.ic_chevron_left,
                                    onClick = onLogoutClick,
                                    size = 44.dp,
                                    iconSize = 24.dp,
                                    modifier = Modifier.align(Alignment.CenterStart)
                                )

                                AppLabelPill(
                                    icon = R.drawable.ic_directions_bus_vector,
                                    title = "Bus Operations"
                                )
                            }

                            Spacer(modifier = Modifier.height(32.dp))

                            BusOpsDriverProfile(
                                driverName = driverName,
                                busNumber = busNumber,
                                shiftInfo = shiftInfo
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                NeumorphismStatCard(
                                    count = totalMembers.toString(),
                                    label = "Total Members",
                                    iconRes = R.drawable.ic_group,
                                    modifier = Modifier.weight(1f)
                                )

                                NeumorphismStatCard(
                                    count = presentToday.toString(),
                                    label = "Present Today",
                                    iconRes = R.drawable.ic_person,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(32.dp))

                            BusOpsStartTripButton(onClick = onStartTrip)

                            Spacer(modifier = Modifier.height(16.dp))

                            BusOpsRouteInfo(routeInfo)

                            Spacer(modifier = Modifier.height(32.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                BusOpsManagementCard(
                                    icon = R.drawable.ic_group,
                                    title = "Members List",
                                    modifier = Modifier.weight(1f),
                                    onClick = { currentTab = "MEMBERS" }
                                )

                                BusOpsManagementCard(
                                    icon = R.drawable.ic_directions_bus_vector,
                                    title = "Bus Profile",
                                    modifier = Modifier.weight(1f),
                                    onClick = { }
                                )
                            }

                            Spacer(modifier = Modifier.height(120.dp))
                        }
                    }
                    "MEMBERS" -> {
                        MembersTabContent(
                            students = studentsList,
                            totalStudents = totalMembers,
                            onBackClick = { currentTab = "HOME" }
                        )
                    }
                    "ROUTES" -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
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
                                    painter = painterResource(id = R.drawable.ic_pin_drop),
                                    contentDescription = "Routes",
                                    tint = NeumorphTextSecondary,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = "Routes coming soon",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = NeumorphTextSecondary
                            )
                        }
                    }
                    "SETTINGS" -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
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
                                    painter = painterResource(id = R.drawable.ic_settings),
                                    contentDescription = "Settings",
                                    tint = NeumorphTextSecondary,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = "Settings coming soon",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = NeumorphTextSecondary
                            )
                        }
                    }
                }
            }

            // Bottom Navigation Bar matching Admin/Driver exactly
            HubBottomNavigationBar(
                currentTab = currentTab,
                onTabSelect = { currentTab = it },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

// ─── INTERNAL COMPOSABLES ───────────────────────────────────────────────────

@Composable
private fun MembersTabContent(
    students: List<StudentMember>,
    totalStudents: Int,
    onBackClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(48.dp))

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
                icon = R.drawable.ic_pin_drop,
                title = "Bus Stop Details"
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Members to Board",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = NeumorphTextPrimary
            )

            NeumorphismPill(
                label = "$totalStudents TOTAL",
                onClick = {}
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Group by status: Waiting To Board first, then Boarded
            val waiting = students.filter { it.status == StudentStatus.WAITING_TO_BOARD }
            val boarded = students.filter { it.status == StudentStatus.BOARDED }

            if (waiting.isNotEmpty()) {
                items(waiting) { student ->
                    BusStopStudentCard(student = student, onCallClick = {})
                }
            }

            if (boarded.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Boarded",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeumorphTextSecondary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                items(boarded) { student ->
                    BusStopStudentCard(student = student, onCallClick = {})
                }
            }
        }
    }
}

@Composable
private fun BusStopStudentCard(
    student: StudentMember,
    onCallClick: () -> Unit
) {
    NeumorphismCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 20.dp,
        contentPadding = PaddingValues(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .neumorphicInset(cornerRadius = 24.dp, blur = 8.dp)
                    .background(NeumorphBgPrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = student.initials,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeumorphTextPrimary
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = student.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeumorphTextPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                val statusText = when (student.status) {
                    StudentStatus.WAITING_TO_BOARD -> "WAITING TO BOARD"
                    StudentStatus.BOARDED -> "BOARDED"
                }
                val statusColor = when (student.status) {
                    StudentStatus.WAITING_TO_BOARD -> Color(0xFFFF9800)
                    StudentStatus.BOARDED -> Color(0xFF4CAF50)
                }

                Box(
                    modifier = Modifier
                        .background(statusColor.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = statusText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            NeumorphismIconButton(
                iconRes = R.drawable.ic_call,
                onClick = onCallClick,
                size = 40.dp,
                iconSize = 20.dp,
                unselectedTint = NeumorphTextPrimary
            )
        }
    }
}

@Composable
private fun HubBottomNavigationBar(
    currentTab: String,
    onTabSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 24.dp)
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
                HubNavItem(
                    icon = R.drawable.ic_home,
                    label = "HOME",
                    isSelected = currentTab == "HOME",
                    onClick = { onTabSelect("HOME") }
                )
                HubNavItem(
                    icon = R.drawable.ic_group,
                    label = "MEMBERS",
                    isSelected = currentTab == "MEMBERS",
                    onClick = { onTabSelect("MEMBERS") }
                )
                HubNavItem(
                    icon = R.drawable.ic_pin_drop,
                    label = "ROUTES",
                    isSelected = currentTab == "ROUTES",
                    onClick = { onTabSelect("ROUTES") }
                )
                HubNavItem(
                    icon = R.drawable.ic_settings,
                    label = "SETTINGS",
                    isSelected = currentTab == "SETTINGS",
                    onClick = { onTabSelect("SETTINGS") }
                )
            }
        }
    }
}

@Composable
private fun HubNavItem(
    icon: Int,
    label: String,
    isSelected: Boolean = false,
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
        // Soft indicator container for selected tab to match Admin/Driver
        Box(
            modifier = Modifier
                .size(36.dp)
                .then(
                    if (isSelected) Modifier.neumorphicInset(cornerRadius = 18.dp, blur = 6.dp).background(NeumorphBgPrimary, CircleShape)
                    else Modifier.background(Color.Transparent, CircleShape)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = label,
                tint = if (isSelected) NeumorphAccentPrimary else NeumorphTextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) NeumorphAccentPrimary else NeumorphTextSecondary,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
private fun BusOpsDriverProfile(driverName: String, busNumber: String, shiftInfo: String) {
    NeumorphismCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 24.dp,
        contentPadding = PaddingValues(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .neumorphicInset(cornerRadius = 28.dp, blur = 8.dp)
                    .background(NeumorphBgPrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_person),
                    contentDescription = "Driver Profile",
                    tint = NeumorphAccentPrimary,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = driverName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeumorphTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Bus #$busNumber ($shiftInfo)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = NeumorphTextSecondary
                )
            }
        }
    }
}

@Composable
private fun BusOpsStartTripButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(200.dp)
            .neumorphic(cornerRadius = 100.dp, elevation = 10.dp, blur = 20.dp)
            .background(NeumorphSurface, CircleShape)
            .bounceClick { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .neumorphicInset(cornerRadius = 30.dp, blur = 8.dp)
                    .background(NeumorphBgPrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_chevron_right),
                    contentDescription = "Start Trip",
                    tint = NeumorphAccentPrimary,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "START TRIP",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = NeumorphTextPrimary,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
private fun BusOpsRouteInfo(routeInfo: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .neumorphic(cornerRadius = 14.dp, elevation = 3.dp, blur = 6.dp)
                .background(NeumorphSurface, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_pin_drop),
                contentDescription = "Route",
                tint = NeumorphTextSecondary,
                modifier = Modifier.size(14.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = routeInfo,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = NeumorphTextSecondary,
            textAlign = TextAlign.Center,
            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
        )
    }
}

@Composable
private fun BusOpsManagementCard(
    icon: Int,
    title: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    NeumorphismCard(
        modifier = modifier
            .height(120.dp)
            .bounceClick { onClick() },
        cornerRadius = 20.dp,
        contentPadding = PaddingValues(0.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .neumorphicInset(cornerRadius = 24.dp, blur = 8.dp)
                    .background(NeumorphBgPrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = title,
                    tint = NeumorphAccentPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = NeumorphTextPrimary,
                textAlign = TextAlign.Center
            )
        }
    }
}