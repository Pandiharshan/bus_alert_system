package com.campusbussbuddy.ui.screens.student

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.campusbussbuddy.viewmodel.student.StudentViewModel

@Composable
fun StudentHomeScreen(
    onNavigateToQrScanner: () -> Unit,
    onNavigateToMap: () -> Unit,
    onSignOut: () -> Unit,
    viewModel: StudentViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val busLocation by viewModel.busLocation.collectAsStateWithLifecycle()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Student Dashboard",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Bus Status Card
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Bus Status",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                busLocation?.let { location ->
                    Text("Bus ID: ${location.busId}")
                    Text("Last seen: ${java.text.SimpleDateFormat("HH:mm:ss").format(java.util.Date(location.lastSeen))}")
                    Text("Speed: ${location.speed} m/s")
                } ?: Text("No bus data available")
            }
        }
        
        // Action Buttons
        Button(
            onClick = onNavigateToQrScanner,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Scan QR Code")
        }
        
        Button(
            onClick = onNavigateToMap,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Track Bus")
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        OutlinedButton(
            onClick = onSignOut,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign Out")
        }
        
        // Show boarding success message
        if (uiState.boardingSuccess) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text(
                    text = "Boarding successful for bus ${uiState.lastScannedBusId}!",
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        
        // Show error message
        uiState.scanError?.let { error ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = error,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}