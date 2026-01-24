package com.campusbussbuddy.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.campusbussbuddy.ui.screens.driver.DriverHomeScreen

fun NavGraphBuilder.driverNavGraph(
    navController: NavHostController
) {
    navigation(
        route = Destinations.DRIVER,
        startDestination = "driver_home"
    ) {
        composable("driver_home") {
            DriverHomeScreen()
        }
    }
}