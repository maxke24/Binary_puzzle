package com.binarypuzzle.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.binarypuzzle.app.data.model.UserProgress
import com.binarypuzzle.app.data.repository.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProgressViewModel(private val repository: ProgressRepository) : ViewModel() {

    private val _progressList = MutableStateFlow<List<UserProgress>>(emptyList())
    val progressList: StateFlow<List<UserProgress>> = _progressList.asStateFlow()

    private val _stats = MutableStateFlow<Map<String, Int>>(emptyMap())
    val stats: StateFlow<Map<String, Int>> = _stats.asStateFlow()

    private val _bestTimes = MutableStateFlow<Map<String, Long>>(emptyMap())
    val bestTimes: StateFlow<Map<String, Long>> = _bestTimes.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadProgress(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _progressList.value = repository.getUserProgress(userId)
                _stats.value = repository.getUserStats(userId)
                _bestTimes.value = buildBestTimes(userId)
            } catch (e: Exception) {
                _error.value = "Failed to load progress: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun buildBestTimes(userId: String): Map<String, Long> {
        val difficulties = listOf("EASY", "MEDIUM", "HARD")
        val result = mutableMapOf<String, Long>()
        for (diff in difficulties) {
            val best = repository.getBestTime(userId, diff)
            if (best != null) result[diff] = best
        }
        return result
    }
}
