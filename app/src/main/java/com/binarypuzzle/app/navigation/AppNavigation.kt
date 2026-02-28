package com.binarypuzzle.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.binarypuzzle.app.game.BinaryPuzzle
import com.binarypuzzle.app.ui.screens.GameScreen
import com.binarypuzzle.app.ui.screens.HomeScreen
import com.binarypuzzle.app.ui.screens.LoginScreen
import com.binarypuzzle.app.ui.screens.ProgressScreen
import com.binarypuzzle.app.viewmodel.AuthViewModel
import com.binarypuzzle.app.viewmodel.GameViewModel
import com.binarypuzzle.app.viewmodel.ProgressViewModel

private object Route {
    const val LOGIN = "login"
    const val HOME = "home"
    const val GAME = "game/{difficulty}"
    const val PROGRESS = "progress"

    fun game(difficulty: BinaryPuzzle.Difficulty) = "game/${difficulty.name}"
}

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel,
    gameViewModel: GameViewModel,
    progressViewModel: ProgressViewModel
) {
    val navController = rememberNavController()
    val user by authViewModel.user.collectAsState()

    val startDestination = if (user != null) Route.HOME else Route.LOGIN

    NavHost(navController = navController, startDestination = startDestination) {

        composable(Route.LOGIN) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(Route.HOME) {
                        popUpTo(Route.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Route.HOME) {
            HomeScreen(
                authViewModel = authViewModel,
                progressViewModel = progressViewModel,
                onStartGame = { difficulty ->
                    navController.navigate(Route.game(difficulty))
                },
                onViewProgress = {
                    navController.navigate(Route.PROGRESS)
                },
                onSignOut = {
                    navController.navigate(Route.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Route.GAME,
            arguments = listOf(navArgument("difficulty") { type = NavType.StringType })
        ) { backStackEntry ->
            val difficultyName = backStackEntry.arguments?.getString("difficulty")
            val difficulty = runCatching {
                BinaryPuzzle.Difficulty.valueOf(difficultyName ?: "EASY")
            }.getOrDefault(BinaryPuzzle.Difficulty.EASY)

            GameScreen(
                difficulty = difficulty,
                gameViewModel = gameViewModel,
                authViewModel = authViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Route.PROGRESS) {
            ProgressScreen(
                progressViewModel = progressViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
