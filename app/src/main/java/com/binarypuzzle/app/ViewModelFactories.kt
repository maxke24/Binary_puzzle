package com.binarypuzzle.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.binarypuzzle.app.data.repository.ProgressRepository
import com.binarypuzzle.app.viewmodel.GameViewModel
import com.binarypuzzle.app.viewmodel.ProgressViewModel

class GameViewModelFactory(private val repository: ProgressRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        GameViewModel(repository) as T
}

class ProgressViewModelFactory(private val repository: ProgressRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        ProgressViewModel(repository) as T
}
