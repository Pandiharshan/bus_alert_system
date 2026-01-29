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
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate("register")
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
        
        composable("register") {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }
    }
}