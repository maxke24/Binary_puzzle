package com.binarypuzzle.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.binarypuzzle.app.auth.GoogleAuthManager
import com.binarypuzzle.app.data.repository.ProgressRepository
import com.binarypuzzle.app.navigation.AppNavigation
import com.binarypuzzle.app.ui.theme.BinaryPuzzleTheme
import com.binarypuzzle.app.viewmodel.AuthViewModel
import com.binarypuzzle.app.viewmodel.GameViewModel
import com.binarypuzzle.app.viewmodel.ProgressViewModel

class MainActivity : ComponentActivity() {

    private val authManager by lazy { GoogleAuthManager(this) }
    private val progressRepository = ProgressRepository()

    private val authViewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(authManager)
    }
    private val gameViewModel: GameViewModel by viewModels {
        GameViewModelFactory(progressRepository)
    }
    private val progressViewModel: ProgressViewModel by viewModels {
        ProgressViewModelFactory(progressRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BinaryPuzzleTheme {
                AppNavigation(
                    authViewModel = authViewModel,
                    gameViewModel = gameViewModel,
                    progressViewModel = progressViewModel
                )
            }
        }
    }
}
