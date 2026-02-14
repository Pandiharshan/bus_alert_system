package com.campusbussbuddy.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.campusbussbuddy.firebase.FirebaseManager
import com.campusbussbuddy.ui.screens.UnifiedLoginScreen
import com.campusbussbuddy.ui.screens.DriverHomeScreen
import com.campusbussbuddy.ui.screens.BusAssignmentLoginScreen
import com.campusbussbuddy.ui.screens.BusOperationsHubScreen
import com.campusbussbuddy.ui.screens.StudentPortalHomeScreen
import com.campusbussbuddy.ui.screens.AdminHomeScreen
import com.campusbussbuddy.ui.screens.AddDriverScreen
import com.campusbussbuddy.ui.screens.AddStudentScreen
import com.campusbussbuddy.ui.screens.AddBusScreen
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
            UnifiedLoginScreen(
                onStudentLoginSuccess = {
                    navController.navigate(Destinations.STUDENT_PORTAL_HOME) {
                        popUpTo(Destinations.LOGIN_SELECTION) {
                            inclusive = false
                        }
                    }
                },
                onDriverLoginSuccess = {
                    navController.navigate(Destinations.DRIVER_HOME) {
                        popUpTo(Destinations.LOGIN_SELECTION) {
                            inclusive = false
                        }
                    }
                },
                onAdminLoginSuccess = {
                    navController.navigate(Destinations.ADMIN_HOME) {
                        popUpTo(Destinations.LOGIN_SELECTION) {
                            inclusive = false
                        }
                    }
                }
            )
        }
        
        composable(Destinations.DRIVER_HOME) {
            DriverHomeScreen(
                onBusLoginClick = {
                    navController.navigate(Destinations.BUS_ASSIGNMENT_LOGIN)
                },
                onLogoutClick = {
                    // Sign out from Firebase
                    FirebaseManager.signOut()
                    // Navigate to Login Selection and clear back stack
                    navController.navigate(Destinations.LOGIN_SELECTION) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Destinations.BUS_ASSIGNMENT_LOGIN) {
            BusAssignmentLoginScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onLoginSuccess = { busNumber, busId ->
                    navController.navigate("${Destinations.BUS_OPERATIONS_HUB}/$busNumber/$busId") {
                        popUpTo(Destinations.DRIVER_HOME) {
                            inclusive = false
                        }
                    }
                }
            )
        }
        
        composable("${Destinations.BUS_OPERATIONS_HUB}/{busNumber}/{busId}") { backStackEntry ->
            val busNumber = backStackEntry.arguments?.getString("busNumber") ?: ""
            val busId = backStackEntry.arguments?.getString("busId") ?: ""
            
            BusOperationsHubScreen(
                busNumber = busNumber,
                onLogoutClick = {
                    // Navigate back to driver home
                    navController.popBackStack(Destinations.DRIVER_HOME, inclusive = false)
                }
            )
        }
        
        
        composable(Destinations.STUDENT_PORTAL_HOME) {
            StudentPortalHomeScreen(
                onLogoutClick = {
                    // Sign out from Firebase
                    FirebaseManager.signOut()
                    // Navigate to Login Selection and clear back stack
                    navController.navigate(Destinations.LOGIN_SELECTION) {
                        popUpTo(0) { inclusive = true }
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
                },
                onLogoutClick = {
                    // Sign out from Firebase
                    FirebaseManager.signOut()
                    // Navigate to Login Selection and clear back stack
                    navController.navigate(Destinations.LOGIN_SELECTION) {
                        popUpTo(0) { inclusive = true }
                    }
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
        
        composable(Destinations.ADD_BUS) {
            AddBusScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onBusAdded = {
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
                },
                onAddBusClick = {
                    navController.navigate(Destinations.ADD_BUS)
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