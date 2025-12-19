package com.example.smartdesk.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.smartdesk.DashboardViewModel
import com.example.smartdesk.ui.theme.SmartDeskTheme

@Composable
fun App(dashboardViewModel: DashboardViewModel) {
    SmartDeskTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            DashboardScreen(vm = dashboardViewModel)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    SmartDeskTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            DashboardScreenPreview()
        }
    }
}