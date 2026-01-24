package com.campusbussbuddy.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.campusbussbuddy.ui.screens.student.StudentHomeScreen

fun NavGraphBuilder.studentNavGraph(
    navController: NavHostController
) {
    navigation(
        route = Destinations.STUDENT,
        startDestination = "student_home"
    ) {
        composable("student_home") {
            StudentHomeScreen()
        }
    }
}