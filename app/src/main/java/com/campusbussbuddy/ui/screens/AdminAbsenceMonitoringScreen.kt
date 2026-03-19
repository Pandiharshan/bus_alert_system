package com.campusbussbuddy.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.campusbussbuddy.firebase.AbsenceData
import com.campusbussbuddy.firebase.BusInfo
import com.campusbussbuddy.ui.neumorphism.buttons.NeumorphismIconButton
import com.campusbussbuddy.ui.neumorphism.cards.NeumorphismCard
import com.campusbussbuddy.ui.neumorphism.layout.AppLabelPill
import com.campusbussbuddy.ui.neumorphism.layout.NeumorphismScreenContainer
import com.campusbussbuddy.ui.theme.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await

// ─── Admin Absence Monitoring Screen ─────────────────────────────────────────
// Real-time version: uses addSnapshotListener for live updates on both
// the bus list and the selected bus's absence records.
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAbsenceMonitoringScreen(
    onBackClick: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()

    // ── State ─────────────────────────────────────────────────────────────────
    var buses          by remember { mutableStateOf<List<BusInfo>>(emptyList()) }
    var busesLoading   by remember { mutableStateOf(true) }
    var busError       by remember { mutableStateOf<String?>(null) }

    var selectedBus    by remember { mutableStateOf<BusInfo?>(null) }
    var absencesList   by remember { mutableStateOf<List<AbsenceData>>(emptyList()) }
    var absencesLoading by remember { mutableStateOf(false) }

    // Grouped by student name for display
    val groupedAbsences = remember(absencesList) {
        absencesList.groupBy { it.studentName }
    }

    var isDropdownExpanded by remember { mutableStateOf(false) }

    // ── One-shot bus list load (same approach as AddStudentScreen – confirmed working) ───
    // busNumber is stored as int64 in Firestore; use getLong() only, never getString().
    LaunchedEffect(Unit) {
        try {
            val snapshot = db.collection("buses").get().await()
            buses = snapshot.documents.mapNotNull { doc ->
                try {
                    BusInfo(
                        busId          = doc.id,
                        busNumber      = doc.getLong("busNumber")?.toInt() ?: 0,
                        capacity       = doc.getLong("capacity")?.toInt()  ?: 0,
                        activeDriverId = doc.getString("activeDriverId")   ?: ""
                    )
                } catch (e: Exception) { null }
            }.sortedBy { it.busNumber }
            busError = null
        } catch (e: Exception) {
            busError = "Failed to load buses: ${e.message}"
        } finally {
            busesLoading = false
        }
    }

    // ── Real-time listener: absences for the selected bus ─────────────────────
    // Re-subscribes automatically every time selectedBus changes.
    DisposableEffect(selectedBus) {
        val currentBus = selectedBus
        if (currentBus == null) {
            absencesList    = emptyList()
            absencesLoading = false
            return@DisposableEffect onDispose {}
        }

        absencesLoading = true
        val reg: ListenerRegistration = db.collection("absences")
            .whereEqualTo("busId", currentBus.busId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    absencesLoading = false
                    return@addSnapshotListener
                }
                absencesList = snapshot.documents.mapNotNull { doc ->
                    runCatching {
                        AbsenceData(
                            id          = doc.id,
                            studentId   = doc.getString("studentId")   ?: "",
                            studentName = doc.getString("studentName") ?: "",
                            busId       = doc.getString("busId")       ?: "",
                            stopName    = doc.getString("stopName")    ?: "",
                            date        = doc.getString("date")        ?: "",
                            status      = doc.getString("status")      ?: "",
                            createdAt   = doc.getLong("createdAt")     ?: 0L
                        )
                    }.getOrNull()
                }.sortedByDescending { it.date }
                absencesLoading = false
            }
        onDispose { reg.remove() }
    }

    // ── UI ────────────────────────────────────────────────────────────────────
    NeumorphismScreenContainer {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(48.dp))

            // Header
            Box(
                modifier          = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                contentAlignment  = Alignment.Center
            ) {
                NeumorphismIconButton(
                    iconRes  = R.drawable.ic_chevron_left,
                    onClick  = onBackClick,
                    size     = 44.dp,
                    iconSize = 24.dp,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
                AppLabelPill(
                    icon  = R.drawable.ic_calendar_today,
                    title = "Absence Monitoring"
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                // ── Bus list loading or error ─────────────────────────────────
                if (busesLoading) {
                    Box(
                        modifier         = Modifier.fillMaxWidth().height(100.dp),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator(color = NeumorphAccentPrimary) }
                    return@Column
                }

                if (busError != null) {
                    Box(
                        modifier         = Modifier.fillMaxWidth().padding(top = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text       = busError ?: "",
                            color      = Color(0xFFE53935),
                            fontSize   = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    return@Column
                }

                // ── Bus dropdown (Exposed) ─────────────────────────────────────
                // Using a fully-enabled NeumorphismCard-backed dropdown instead of
                // the fragile invisible-Box-over-disabled-TextField pattern.
                ExposedDropdownMenuBox(
                    expanded        = isDropdownExpanded,
                    onExpandedChange = { isDropdownExpanded = it }
                ) {
                    NeumorphismCard(
                        modifier       = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),           // ← links the anchor to the menu
                        cornerRadius   = 16.dp,
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp)
                    ) {
                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter           = painterResource(R.drawable.ic_directions_bus_vector),
                                    contentDescription = "Bus",
                                    tint              = NeumorphAccentPrimary,
                                    modifier          = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    text       = selectedBus?.let { "Bus ${it.busNumber}" } ?: "Select a Bus",
                                    color      = if (selectedBus != null) NeumorphTextPrimary else NeumorphTextSecondary,
                                    fontSize   = 15.sp,
                                    fontWeight = if (selectedBus != null) FontWeight.SemiBold else FontWeight.Normal
                                )
                            }
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded)
                        }
                    }

                    ExposedDropdownMenu(
                        expanded        = isDropdownExpanded,
                        onDismissRequest = { isDropdownExpanded = false },
                        modifier        = Modifier.background(NeumorphSurface)
                    ) {
                        if (buses.isEmpty()) {
                            DropdownMenuItem(
                                text    = { Text("No buses found", color = NeumorphTextSecondary) },
                                onClick = { isDropdownExpanded = false }
                            )
                        } else {
                            buses.forEach { bus ->
                                DropdownMenuItem(
                                    text    = {
                                        Text(
                                            text       = "Bus ${bus.busNumber}",
                                            color      = if (bus.busId == selectedBus?.busId)
                                                             NeumorphAccentPrimary else NeumorphTextPrimary,
                                            fontWeight = if (bus.busId == selectedBus?.busId)
                                                             FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    onClick = {
                                        selectedBus          = bus
                                        isDropdownExpanded   = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ── Absence content ───────────────────────────────────────────
                when {
                    selectedBus == null -> {
                        Box(
                            modifier         = Modifier.fillMaxSize().padding(top = 48.dp),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            Text(
                                text       = "Select a bus above to view live absences.",
                                color      = NeumorphTextSecondary,
                                fontSize   = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    absencesLoading -> {
                        Box(
                            modifier         = Modifier.fillMaxWidth().padding(top = 48.dp),
                            contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator(color = NeumorphAccentPrimary) }
                    }
                    groupedAbsences.isEmpty() -> {
                        Box(
                            modifier         = Modifier.fillMaxSize().padding(top = 48.dp),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    painter           = painterResource(R.drawable.ic_calendar_today),
                                    contentDescription = null,
                                    tint              = NeumorphTextSecondary.copy(alpha = 0.5f),
                                    modifier          = Modifier.size(48.dp)
                                )
                                Spacer(Modifier.height(12.dp))
                                Text(
                                    "No absences recorded for Bus ${selectedBus?.busNumber}.",
                                    color      = NeumorphTextSecondary,
                                    fontSize   = 15.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                    else -> {
                        // ── LIVE badge ─────────────────────────────────────────
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier          = Modifier.padding(bottom = 12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(Color(0xFF4CAF50), androidx.compose.foundation.shape.CircleShape)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text      = "LIVE  —  ${absencesList.size} record(s) for Bus ${selectedBus?.busNumber}",
                                color     = NeumorphTextSecondary,
                                fontSize  = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        LazyColumn(
                            modifier         = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding   = PaddingValues(bottom = 120.dp)
                        ) {
                            items(groupedAbsences.entries.toList(), key = { it.key }) { (studentName, records) ->
                                ExpandableStudentAbsenceCard(
                                    studentName = studentName,
                                    records     = records
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─── Expandable Student Card ──────────────────────────────────────────────────
@Composable
private fun ExpandableStudentAbsenceCard(
    studentName: String,
    records: List<AbsenceData>
) {
    var expanded by remember { mutableStateOf(false) }
    val stopName = records.firstOrNull()?.stopName ?: "Unknown Stop"
    val count    = records.size

    NeumorphismCard(
        modifier       = Modifier
            .fillMaxWidth()
            .bounceClick { expanded = !expanded },
        cornerRadius   = 20.dp,
        contentPadding = PaddingValues(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text       = studentName,
                        color      = NeumorphTextPrimary,
                        fontSize   = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text       = "Stop: $stopName",
                        color      = NeumorphTextSecondary,
                        fontSize   = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text       = "Absences",
                        color      = NeumorphTextSecondary,
                        fontSize   = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text       = "$count",
                        color      = Color(0xFFE53935),
                        fontSize   = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                ) {
                    HorizontalDivider(color = NeumorphTextSecondary.copy(alpha = 0.2f), thickness = 1.dp)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text       = "Recorded Dates:",
                        color      = NeumorphTextPrimary,
                        fontSize   = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier   = Modifier.padding(bottom = 8.dp)
                    )
                    records.forEach { record ->
                        Row(
                            modifier          = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter           = painterResource(id = R.drawable.ic_calendar_today),
                                contentDescription = "Date",
                                tint              = NeumorphTextSecondary,
                                modifier          = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text       = record.date,
                                color      = NeumorphTextSecondary,
                                fontSize   = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(Modifier.width(12.dp))
                            Box(
                                modifier = Modifier
                                    .background(
                                        Color(0xFFE53935).copy(alpha = 0.12f),
                                        androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text       = record.status.uppercase(),
                                    color      = Color(0xFFE53935),
                                    fontSize   = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
