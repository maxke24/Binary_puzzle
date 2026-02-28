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

    // Always null in local mode — kept so ProgressScreen compiles unchanged
    val error: StateFlow<String?> = MutableStateFlow<String?>(null).asStateFlow()

    fun loadProgress() {
        viewModelScope.launch {
            _isLoading.value = true
            _progressList.value = repository.getUserProgress()
            _stats.value = repository.getUserStats()
            _bestTimes.value = listOf("EASY", "MEDIUM", "HARD").mapNotNull { diff ->
                repository.getBestTime(diff)?.let { diff to it }
            }.toMap()
            _isLoading.value = false
        }
    }
}
