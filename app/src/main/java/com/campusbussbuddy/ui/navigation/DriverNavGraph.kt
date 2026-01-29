package com.campusbussbuddy.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.campusbussbuddy.ui.screens.driver.DriverHomeScreen
import com.campusbussbuddy.ui.screens.driver.TripManagementScreen
import com.campusbussbuddy.ui.screens.driver.QrGeneratorScreen
import com.campusbussbuddy.ui.screens.driver.DriverProfileScreen

fun NavGraphBuilder.driverNavGraph(
    navController: NavHostController
) {
    navigation(
        route = Destinations.DRIVER,
        startDestination = Destinations.DRIVER_HOME
    ) {
        composable(Destinations.DRIVER_HOME) {
            DriverHomeScreen(navController = navController)
        }
        
        composable(Destinations.TRIP_MANAGEMENT) {
            TripManagementScreen(navController = navController)
        }
        
        composable(Destinations.QR_GENERATOR) {
            QrGeneratorScreen(navController = navController)
        }
        
        composable(Destinations.DRIVER_PROFILE) {
            DriverProfileScreen(navController = navController)
        }
    }
}