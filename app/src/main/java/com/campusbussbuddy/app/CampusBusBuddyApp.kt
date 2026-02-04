package com.campusbussbuddy.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.campusbussbuddy.ui.navigation.RootNavHost

@Composable
fun CampusBusBuddyApp() {
    MaterialTheme {
        RootNavHost()
    }
}