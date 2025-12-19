package com.example.smartdesk.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.smartdesk.ui.theme.SmartDeskTheme

@Composable
fun ThresholdSlider(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float> = 0f..400f,
    onChange: (Float) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "${value.toInt()} lx",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Slider(
            value = value,
            onValueChange = onChange,
            valueRange = range,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun PresetButtons(
    onOffice: () -> Unit,
    onBright: () -> Unit,
    onDim: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Presets",
            style = MaterialTheme.typography.labelLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onOffice,
                modifier = Modifier.weight(1f)
            ) {
                Text("Office")
            }
            OutlinedButton(
                onClick = onBright,
                modifier = Modifier.weight(1f)
            ) {
                Text("Bright")
            }
            OutlinedButton(
                onClick = onDim,
                modifier = Modifier.weight(1f)
            ) {
                Text("Dim")
            }
        }
    }
}

@Composable
fun ManualToggle(
    isOn: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Manual Alert",
            style = MaterialTheme.typography.bodyLarge
        )
        Switch(
            checked = isOn,
            onCheckedChange = onToggle
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ControlsPreview() {
    SmartDeskTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ThresholdSlider(
                label = "Low Threshold",
                value = 50f,
                onChange = {}
            )

            ThresholdSlider(
                label = "High Threshold",
                value = 200f,
                onChange = {}
            )

            PresetButtons(
                onOffice = {},
                onBright = {},
                onDim = {}
            )

            ManualToggle(
                isOn = false,
                onToggle = {}
            )
        }
    }
}