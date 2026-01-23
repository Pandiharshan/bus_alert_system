package com.campusbussbuddy.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.campusbussbuddy.ui.screens.auth.LoginScreen
import com.campusbussbuddy.ui.screens.driver.DriverHomeScreen
import com.campusbussbuddy.ui.screens.student.StudentHomeScreen

@Composable
fun CampusBusBuddyNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "auth"
    ) {
        // Auth Graph
        composable("auth") {
            LoginScreen(
                onNavigateToStudent = {
                    navController.navigate("student") {
                        popUpTo("auth") { inclusive = true }
                    }
                },
                onNavigateToDriver = {
                    navController.navigate("driver") {
                        popUpTo("auth") { inclusive = true }
                    }
                }
            )
        }
        
        // Student Graph
        composable("student") {
            StudentHomeScreen(
                onNavigateToQrScanner = {
                    navController.navigate("qr_scanner")
                },
                onNavigateToMap = {
                    navController.navigate("bus_map")
                },
                onSignOut = {
                    navController.navigate("auth") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        
        composable("qr_scanner") {
            // QR Scanner Screen - To be implemented
            // QrScannerScreen(onNavigateBack = { navController.popBackStack() })
        }
        
        composable("bus_map") {
            // Bus Map Screen - To be implemented
            // BusMapScreen(onNavigateBack = { navController.popBackStack() })
        }
        
        // Driver Graph
        composable("driver") {
            DriverHomeScreen(
                onSignOut = {
                    navController.navigate("auth") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}