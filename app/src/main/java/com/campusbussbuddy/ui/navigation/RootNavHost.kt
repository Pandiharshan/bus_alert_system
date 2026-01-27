package com.campusbussbuddy.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.campusbussbuddy.domain.model.AuthState
import com.campusbussbuddy.domain.model.UserRole
import com.campusbussbuddy.viewmodel.auth.AuthViewModel

@Composable
fun RootNavHost() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    
    // Handle role-based navigation
    LaunchedEffect(authState) {
        val currentState = authState
        when (currentState) {
            is AuthState.Authenticated -> {
                val destination = when (currentState.user.role) {
                    UserRole.STUDENT -> Destinations.STUDENT
                    UserRole.DRIVER -> Destinations.DRIVER
                }
                
                // Navigate to role-based destination
                navController.navigate(destination) {
                    // Clear the entire back stack and replace with new destination
                    popUpTo(0) { inclusive = true }
                    // Avoid multiple copies of the same destination
                    launchSingleTop = true
                }
            }
            is AuthState.Unauthenticated -> {
                // Navigate back to auth
                navController.navigate(Destinations.AUTH) {
                    // Clear the entire back stack
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            }
            is AuthState.Error -> {
                // Navigate back to auth on error
                navController.navigate(Destinations.AUTH) {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            }
            is AuthState.Loading -> {
                // Stay on current screen during loading
            }
        }
    }
    
    NavHost(
        navController = navController,
        startDestination = Destinations.AUTH
    ) {
        authNavGraph(navController, authViewModel)
        studentNavGraph(navController, authViewModel)
        driverNavGraph(navController, authViewModel)
    }
}