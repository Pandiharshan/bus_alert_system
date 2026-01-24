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
        when (authState) {
            is AuthState.Authenticated -> {
                val destination = when (authState.user.role) {
                    UserRole.STUDENT -> Destinations.STUDENT
                    UserRole.DRIVER -> Destinations.DRIVER
                }
                navController.navigate(destination) {
                    popUpTo(Destinations.AUTH) { inclusive = true }
                }
            }
            is AuthState.Unauthenticated -> {
                navController.navigate(Destinations.AUTH) {
                    popUpTo(0) { inclusive = true }
                }
            }
            else -> { /* Loading or Error - stay on current screen */ }
        }
    }
    
    NavHost(
        navController = navController,
        startDestination = Destinations.AUTH
    ) {
        authNavGraph(navController)
        studentNavGraph(navController)
        driverNavGraph(navController)
    }
}