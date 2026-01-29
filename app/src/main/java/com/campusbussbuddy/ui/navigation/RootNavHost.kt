package com.campusbussbuddy.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController

@Composable
fun RootNavHost() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = Destinations.AUTH
    ) {
        authNavGraph(navController)
        studentNavGraph(navController)
        driverNavGraph(navController)
    }
}