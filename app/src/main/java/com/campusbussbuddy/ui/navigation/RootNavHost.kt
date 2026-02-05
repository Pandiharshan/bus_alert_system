package com.campusbussbuddy.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.campusbussbuddy.ui.screens.LoginSelectionScreen
import com.campusbussbuddy.ui.screens.DriverAuthenticationScreen
import com.campusbussbuddy.ui.screens.StudentPortalHomeScreen
import com.campusbussbuddy.ui.screens.AdminLoginScreen
import com.campusbussbuddy.ui.screens.AdminHomeScreen

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
                    navController.navigate(Destinations.STUDENT_PORTAL_HOME)
                },
                onDriverAccessClick = { 
                    navController.navigate(Destinations.DRIVER_AUTHENTICATION)
                },
                onAdminLoginClick = {
                    navController.navigate(Destinations.ADMIN_LOGIN)
                }
            )
        }
        
        composable(Destinations.DRIVER_AUTHENTICATION) {
            DriverAuthenticationScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onStartShiftClick = {
                    // TODO: Navigate to driver dashboard when implemented
                }
            )
        }
        
        composable(Destinations.STUDENT_PORTAL_HOME) {
            StudentPortalHomeScreen()
        }
        
        composable(Destinations.ADMIN_LOGIN) {
            AdminLoginScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onLoginSuccess = {
                    navController.navigate(Destinations.ADMIN_HOME) {
                        // Clear back stack to prevent going back to login
                        popUpTo(Destinations.LOGIN_SELECTION) {
                            inclusive = false
                        }
                    }
                }
            )
        }
        
        composable(Destinations.ADMIN_HOME) {
            AdminHomeScreen()
        }
    }
}