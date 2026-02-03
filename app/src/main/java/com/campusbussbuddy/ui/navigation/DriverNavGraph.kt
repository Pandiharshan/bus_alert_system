package com.campusbussbuddy.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.campusbussbuddy.ui.screens.driver.*

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
        
        composable(Destinations.DRIVER_PORTAL) {
            DriverPortalScreen(navController = navController)
        }
        
        composable(Destinations.BUS_LOGIN) {
            BusLoginScreen(navController = navController)
        }
        
        composable(Destinations.DRIVER_BUS_HOME) {
            DriverBusHomeScreen(navController = navController)
        }
        
        composable(Destinations.TRIP_SCREEN) {
            TripScreen(navController = navController)
        }
        
        composable(Destinations.BUS_PROFILE) {
            BusProfileScreen(navController = navController)
        }
        
        composable(Destinations.DRIVER_PROFILE) {
            DriverProfileScreen(navController = navController)
        }
        
        // Legacy screens (keeping for backward compatibility)
        composable(Destinations.TRIP_MANAGEMENT) {
            TripManagementScreen(navController = navController)
        }
        
        composable(Destinations.QR_GENERATOR) {
            QrGeneratorScreen(navController = navController)
        }
        
        // Placeholder screens for missing destinations
        composable(Destinations.BUS_MEMBERS) {
            // TODO: Create BusMembersScreen
            DriverBusHomeScreen(navController = navController)
        }
        
        composable(Destinations.ATTENDANCE) {
            // TODO: Create AttendanceScreen  
            DriverBusHomeScreen(navController = navController)
        }
    }
}