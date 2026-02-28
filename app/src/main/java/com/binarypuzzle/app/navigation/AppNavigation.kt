package com.binarypuzzle.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.binarypuzzle.app.game.BinaryPuzzle
import com.binarypuzzle.app.ui.screens.GameScreen
import com.binarypuzzle.app.ui.screens.HomeScreen
import com.binarypuzzle.app.ui.screens.ProgressScreen
import com.binarypuzzle.app.viewmodel.GameViewModel
import com.binarypuzzle.app.viewmodel.ProgressViewModel

private object Route {
    const val HOME = "home"
    const val GAME = "game/{difficulty}"
    const val PROGRESS = "progress"

    fun game(difficulty: BinaryPuzzle.Difficulty) = "game/${difficulty.name}"
}

@Composable
fun AppNavigation(
    gameViewModel: GameViewModel,
    progressViewModel: ProgressViewModel
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Route.HOME) {

        composable(Route.HOME) {
            HomeScreen(
                progressViewModel = progressViewModel,
                onStartGame = { difficulty ->
                    navController.navigate(Route.game(difficulty))
                },
                onViewProgress = {
                    navController.navigate(Route.PROGRESS)
                }
            )
        }

        composable(
            route = Route.GAME,
            arguments = listOf(navArgument("difficulty") { type = NavType.StringType })
        ) { backStackEntry ->
            val difficulty = runCatching {
                BinaryPuzzle.Difficulty.valueOf(
                    backStackEntry.arguments?.getString("difficulty") ?: "EASY"
                )
            }.getOrDefault(BinaryPuzzle.Difficulty.EASY)

            GameScreen(
                difficulty = difficulty,
                gameViewModel = gameViewModel,
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
