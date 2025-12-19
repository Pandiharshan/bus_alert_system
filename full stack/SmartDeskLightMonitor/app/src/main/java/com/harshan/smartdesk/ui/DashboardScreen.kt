package com.example.smartdesk.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartdesk.DashboardViewModel
import com.example.smartdesk.ui.theme.SmartDeskTheme
import com.example.smartdesk.ui.theme.StatusGreen
import com.example.smartdesk.ui.theme.StatusRed
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(vm: DashboardViewModel) {
    val lux by vm.currentLux.collectAsState()
    val low by vm.lowThreshold.collectAsState()
    val high by vm.highThreshold.collectAsState()
    val isAlert by vm.isAlert.collectAsState()
    val isConnected by vm.isConnected.collectAsState()
    val history by vm.luxHistory.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SmartDesk") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = lux.toInt().toString(),
                fontSize = 72.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "lx",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            LuxGauge(lux = lux, maxLux = 400f)

            Spacer(modifier = Modifier.height(24.dp))

            StatusChip(isAlert = isAlert)

            Spacer(modifier = Modifier.height(32.dp))

            ThresholdControl(
                label = "Low Threshold",
                value = low,
                onValueChange = { vm.setLowThreshold(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ThresholdControl(
                label = "High Threshold",
                value = high,
                onValueChange = { vm.setHighThreshold(it) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { vm.toggleManualAlert() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Toggle Manual Alert")
            }

            Spacer(modifier = Modifier.height(16.dp))

            PresetButtons(vm = vm)

            Spacer(modifier = Modifier.height(24.dp))

            Sparkline(history = history)

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedButton(
                onClick = { vm.toggleConnection() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isConnected) "Disconnect" else "Connect")
            }
        }
    }
}

@Composable
fun LuxGauge(lux: Float, maxLux: Float) {
    val percent = min(lux / maxLux, 1f)

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(180.dp)
    ) {
        Canvas(modifier = Modifier.size(180.dp)) {
            val stroke = 20.dp.toPx()
            val radius = size.minDimension / 2 - stroke / 2

            drawArc(
                color = Color.LightGray,
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                topLeft = Offset(stroke / 2, stroke / 2),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )

            drawArc(
                color = if (percent > 0.7f) StatusRed else StatusGreen,
                startAngle = 135f,
                sweepAngle = 270f * percent,
                useCenter = false,
                topLeft = Offset(stroke / 2, stroke / 2),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
        }

        Text(
            text = "${(percent * 100).toInt()}%",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun StatusChip(isAlert: Boolean) {
    SuggestionChip(
        onClick = {},
        label = { Text(if (isAlert) "Alert" else "Normal") },
        colors = SuggestionChipDefaults.suggestionChipColors(
            containerColor = if (isAlert) StatusRed else StatusGreen,
            labelColor = Color.White
        )
    )
}

@Composable
fun ThresholdControl(label: String, value: Float, onValueChange: (Float) -> Unit) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text("${value.toInt()} lx", fontWeight = FontWeight.Bold)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0f..400f,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun PresetButtons(vm: DashboardViewModel) {
    Text("Presets", style = MaterialTheme.typography.labelLarge)
    Spacer(modifier = Modifier.height(8.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(
            onClick = { vm.applyPreset(50f, 200f) },
            modifier = Modifier.weight(1f)
        ) {
            Text("Office")
        }
        OutlinedButton(
            onClick = { vm.applyPreset(100f, 300f) },
            modifier = Modifier.weight(1f)
        ) {
            Text("Bright")
        }
        OutlinedButton(
            onClick = { vm.applyPreset(20f, 100f) },
            modifier = Modifier.weight(1f)
        ) {
            Text("Dim")
        }
    }
}

@Composable
fun Sparkline(history: List<Float>) {
    if (history.isEmpty()) return

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("History", style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            val max = history.maxOrNull() ?: 1f
            history.takeLast(30).forEach { lux ->
                val h = ((lux / max) * 40).dp
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(h)
                        .width(4.dp)
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxSize()
                    ) {}
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    SmartDeskTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Dashboard Preview")
            }
        }
    }
}