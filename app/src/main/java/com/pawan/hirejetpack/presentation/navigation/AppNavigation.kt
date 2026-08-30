package com.pawan.hirejetpack.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pawan.hirejetpack.presentation.state.JobDetailViewModel
import com.pawan.hirejetpack.presentation.state.LoginViewModel
import com.pawan.hirejetpack.presentation.ui.analytics.AnalyticsScreen
import com.pawan.hirejetpack.presentation.ui.jobdetail.JobDetailScreen
import com.pawan.hirejetpack.presentation.ui.login.LoginScreen
import com.pawan.hirejetpack.presentation.ui.main.MainScreen

/**
 * KEYWORD: [@Composable]
 * Annotation informing the Kotlin compiler that this function transforms
 * data into UI nodes.
 *
 * KEYWORD: [rememberNavController]
 * Creates and retains a [androidx.navigation.NavHostController] instance
 * across Activity recompositions.
 *
 * KEYWORD: [NavHost] & [composable]
 * Defines the navigation graph, mapping route strings to Composable
 * destinations.
 *
 * Flow: Login -> Main (bottom-nav shell: Home / Saved / Applications /
 * Profile tabs) -> JobDetail (pushed on top of Main, reachable from any
 * tab that lists a job).
 *
 * [LoginViewModel] is created once, here, and passed down into
 * [MainScreen] — that's what lets the Profile tab show the same
 * logged-in user's data without re-fetching or duplicating state.
 */
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val loginViewModel: LoginViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Login.route) { inclusive = true } // Clear backstack
                    }
                }
            )
        }
        composable(Screen.Main.route) {
            MainScreen(
                loginViewModel = loginViewModel,
                onJobClick = { jobId -> navController.navigate(Screen.JobDetail.createRoute(jobId)) },
                onLogout = {
                    loginViewModel.resetState()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Main.route) { inclusive = true }
                    }
                },
                onAnalyticsClick = {
                    navController.navigate(Screen.Analytics.route)
                }
            )
        }
        composable(Screen.Analytics.route) {
            AnalyticsScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.JobDetail.route,
            arguments = listOf(navArgument("jobId") { type = NavType.StringType })
        ) {
            // No need to read the argument here — JobDetailViewModel pulls
            // it from its injected SavedStateHandle instead.
            val jobDetailViewModel: JobDetailViewModel = viewModel()
            JobDetailScreen(
                viewModel = jobDetailViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}