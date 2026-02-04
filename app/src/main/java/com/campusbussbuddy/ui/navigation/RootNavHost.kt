package com.campusbussbuddy.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.campusbussbuddy.ui.screens.LoginSelectionScreen

@Composable
fun RootNavHost() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = Destinations.LOGIN_SELECTION
    ) {
        composable(Destinations.LOGIN_SELECTION) {
            LoginSelectionScreen(
                onStudentLoginClick = { 
                    // TODO: Navigate to student login when implemented
                },
                onDriverAccessClick = { 
                    // TODO: Navigate to driver access when implemented
                }
            )
        }
    }
}