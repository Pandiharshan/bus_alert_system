package com.campusbussbuddy.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.campusbussbuddy.ui.screens.student.StudentHomeScreen
import com.campusbussbuddy.ui.screens.student.BusMapScreen
import com.campusbussbuddy.ui.screens.student.QrScannerScreen
import com.campusbussbuddy.ui.screens.student.StudentProfileScreen

fun NavGraphBuilder.studentNavGraph(
    navController: NavHostController
) {
    navigation(
        route = Destinations.STUDENT,
        startDestination = Destinations.STUDENT_HOME
    ) {
        composable(Destinations.STUDENT_HOME) {
            StudentHomeScreen(navController = navController)
        }
        
        composable(Destinations.BUS_MAP) {
            BusMapScreen(navController = navController)
        }
        
        composable(Destinations.QR_SCANNER) {
            QrScannerScreen(navController = navController)
        }
        
        composable(Destinations.STUDENT_PROFILE) {
            StudentProfileScreen(navController = navController)
        }
    }
}