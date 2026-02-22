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
import com.dhruvathaide.drnkcontrol.ui.onboarding.OnboardingScreen
import com.dhruvathaide.drnkcontrol.ui.review.ReviewQueueScreen

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Dashboard : Screen("dashboard")
    object GyroChallenge : Screen("gyro_challenge")
    object SimonChallenge : Screen("simon_challenge")
    object SemanticChallenge : Screen("semantic_challenge")
    object ReviewQueue : Screen("review_queue")
    object AppSelector : Screen("app_selector")
}

@Composable
fun DRNKNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Onboarding.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.AppSelector.route) {
            com.dhruvathaide.drnkcontrol.ui.selector.AppSelectorScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onComplete = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToChallenges = {
                    navController.navigate(Screen.GyroChallenge.route)
                },
                onNavigateToReview = {
                    navController.navigate(Screen.ReviewQueue.route)
                },
                onNavigateToAppSelector = {
                    navController.navigate(Screen.AppSelector.route)
                }
            )
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
                onSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ReviewQueue.route) {
            ReviewQueueScreen(
                onEmergencyWipe = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
