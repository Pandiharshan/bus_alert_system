package com.campusbussbuddy.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.campusbussbuddy.ui.screens.auth.LoginScreen
import com.campusbussbuddy.ui.screens.auth.RegisterScreen

fun NavGraphBuilder.authNavGraph(
    navController: NavHostController
) {
    navigation(
        route = Destinations.AUTH,
        startDestination = Destinations.LOGIN
    ) {
        composable(Destinations.LOGIN) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Destinations.REGISTER)
                },
                onNavigateToStudent = {
                    navController.navigate(Destinations.STUDENT) {
                        popUpTo(Destinations.AUTH) { inclusive = true }
                    }
                },
                onNavigateToDriver = {
                    navController.navigate(Destinations.DRIVER) {
                        popUpTo(Destinations.AUTH) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Destinations.REGISTER) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }
    }
}