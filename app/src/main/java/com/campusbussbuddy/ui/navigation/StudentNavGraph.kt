package com.campusbussbuddy.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.campusbussbuddy.ui.screens.student.StudentHomeScreen
import com.campusbussbuddy.viewmodel.auth.AuthViewModel

fun NavGraphBuilder.studentNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    navigation(
        route = Destinations.STUDENT,
        startDestination = "student_home"
    ) {
        composable("student_home") {
            StudentHomeScreen(authViewModel = authViewModel)
        }
    }
}