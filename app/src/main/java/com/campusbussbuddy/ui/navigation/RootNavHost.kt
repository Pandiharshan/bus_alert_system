package com.campusbussbuddy.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.campusbussbuddy.ui.screens.LoginSelectionScreen
import com.campusbussbuddy.ui.screens.StudentLoginScreen
import com.campusbussbuddy.ui.screens.DriverAuthenticationScreen
import com.campusbussbuddy.ui.screens.DriverHomeScreen
import com.campusbussbuddy.ui.screens.StudentPortalHomeScreen
import com.campusbussbuddy.ui.screens.AdminLoginScreen
import com.campusbussbuddy.ui.screens.AdminHomeScreen
import com.campusbussbuddy.ui.screens.AddDriverScreen
import com.campusbussbuddy.ui.screens.AddStudentScreen
import com.campusbussbuddy.ui.screens.DriverDatabaseScreen
import com.campusbussbuddy.ui.screens.BusDatabaseScreen
import com.campusbussbuddy.ui.screens.StudentDatabaseScreen

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
                    navController.navigate(Destinations.STUDENT_LOGIN)
                },
                onDriverAccessClick = { 
                    navController.navigate(Destinations.DRIVER_AUTHENTICATION)
                },
                onAdminLoginClick = {
                    navController.navigate(Destinations.ADMIN_LOGIN)
                }
            )
        }
        
        composable(Destinations.STUDENT_LOGIN) {
            StudentLoginScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onLoginSuccess = {
                    navController.navigate(Destinations.STUDENT_PORTAL_HOME) {
                        // Clear back stack to prevent going back to login
                        popUpTo(Destinations.LOGIN_SELECTION) {
                            inclusive = false
                        }
                    }
                }
            )
        }
        
        composable(Destinations.DRIVER_AUTHENTICATION) {
            DriverAuthenticationScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onStartShiftClick = {
                    navController.navigate(Destinations.DRIVER_HOME) {
                        // Clear back stack to prevent going back to authentication
                        popUpTo(Destinations.LOGIN_SELECTION) {
                            inclusive = false
                        }
                    }
                }
            )
        }
        
        composable(Destinations.DRIVER_HOME) {
            DriverHomeScreen()
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
            AdminHomeScreen(
                onManageDriversClick = {
                    navController.navigate(Destinations.MANAGE_DRIVERS)
                },
                onManageBusesClick = {
                    navController.navigate(Destinations.MANAGE_BUSES)
                },
                onManageStudentsClick = {
                    navController.navigate(Destinations.MANAGE_STUDENTS)
                }
            )
        }
        
        composable(Destinations.ADD_DRIVER) {
            AddDriverScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onDriverAdded = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Destinations.ADD_STUDENT) {
            AddStudentScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onStudentAdded = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Destinations.MANAGE_DRIVERS) {
            DriverDatabaseScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onAddDriverClick = {
                    navController.navigate(Destinations.ADD_DRIVER)
                }
            )
        }
        
        composable(Destinations.MANAGE_BUSES) {
            BusDatabaseScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Destinations.MANAGE_STUDENTS) {
            StudentDatabaseScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onAddStudentClick = {
                    navController.navigate(Destinations.ADD_STUDENT)
                }
            )
        }
    }
}