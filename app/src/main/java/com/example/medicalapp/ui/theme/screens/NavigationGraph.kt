package com.example.medicalapp.ui.theme.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun NavigationGraph() {
    val navController = rememberNavController()
    //start destination screen or launcher screen
    NavHost(navController = navController, startDestination = Routes.SplashScreen){
        composable(Routes.SplashScreen){
            SplashScreen(navController)
           // SplashScreen(navController)
        }
        composable(Routes.UserInfoScreen){
            UserInfoScreen(navController)
        }
        composable(
            "samples_screen/{isCamera}",
            arguments = listOf(navArgument("isCamera") { type = NavType.BoolType })
        ) { backStackEntry ->
            val isCamera = backStackEntry.arguments?.getBoolean("isCamera") ?: true
            SamplesScreen(navController, isCamera)
        }
    }
}