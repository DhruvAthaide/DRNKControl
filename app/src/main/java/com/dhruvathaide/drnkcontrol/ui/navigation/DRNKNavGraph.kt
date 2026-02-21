package com.dhruvathaide.drnkcontrol.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dhruvathaide.drnkcontrol.ui.challenges.GyroChallengeScreen
import com.dhruvathaide.drnkcontrol.ui.challenges.SemanticChallengeScreen
import com.dhruvathaide.drnkcontrol.ui.challenges.SimonSaysChallengeScreen
import com.dhruvathaide.drnkcontrol.ui.dashboard.DashboardScreen
import com.dhruvathaide.drnkcontrol.ui.review.ReviewQueueScreen

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object GyroChallenge : Screen("gyro_challenge")
    object SimonChallenge : Screen("simon_challenge")
    object SemanticChallenge : Screen("semantic_challenge")
    object ReviewQueue : Screen("review_queue")
}

@Composable
fun DRNKNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen()
            // In a real implementation we would pass callbacks to navigate
            // e.g. onNavigateToReview = { navController.navigate(Screen.ReviewQueue.route) }
        }
        
        composable(Screen.GyroChallenge.route) {
            GyroChallengeScreen(
                onSuccess = { navController.navigate(Screen.SimonChallenge.route) }
            )
        }
        
        composable(Screen.SimonChallenge.route) {
            SimonSaysChallengeScreen(
                onSuccess = { navController.navigate(Screen.SemanticChallenge.route) }
            )
        }
        
        composable(Screen.SemanticChallenge.route) {
            SemanticChallengeScreen(
                onSuccess = { navController.navigate(Screen.Dashboard.route) }
            )
        }
        
        composable(Screen.ReviewQueue.route) {
            ReviewQueueScreen(
                onEmergencyWipe = { navController.navigate(Screen.Dashboard.route) }
            )
        }
    }
}
