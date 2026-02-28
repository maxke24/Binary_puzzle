package com.binarypuzzle.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.binarypuzzle.app.data.model.UserProgress
import com.binarypuzzle.app.data.repository.ProgressRepository
import com.binarypuzzle.app.game.BinaryPuzzle
import com.binarypuzzle.app.game.PuzzleGenerator
import com.binarypuzzle.app.game.PuzzleValidator
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameViewModel(private val repository: ProgressRepository) : ViewModel() {

    private val generator = PuzzleGenerator()

    private val _puzzle = MutableStateFlow<BinaryPuzzle?>(null)
    val puzzle: StateFlow<BinaryPuzzle?> = _puzzle.asStateFlow()

    private val _currentGrid = MutableStateFlow<Array<IntArray>>(emptyArray())
    val currentGrid: StateFlow<Array<IntArray>> = _currentGrid.asStateFlow()

    private val _invalidCells = MutableStateFlow<Set<Pair<Int, Int>>>(emptySet())
    val invalidCells: StateFlow<Set<Pair<Int, Int>>> = _invalidCells.asStateFlow()

    private val _isSolved = MutableStateFlow(false)
    val isSolved: StateFlow<Boolean> = _isSolved.asStateFlow()

    private val _elapsedSeconds = MutableStateFlow(0L)
    val elapsedSeconds: StateFlow<Long> = _elapsedSeconds.asStateFlow()

    private var timerJob: Job? = null

    fun startNewPuzzle(difficulty: BinaryPuzzle.Difficulty) {
        timerJob?.cancel()
        val puzzle = generator.generate(difficulty)
        _puzzle.value = puzzle
        _currentGrid.value = puzzle.grid.copyGrid()
        _invalidCells.value = emptySet()
        _isSolved.value = false
        _elapsedSeconds.value = 0L
        startTimer()
    }

    fun cellTapped(row: Int, col: Int) {
        val puzzle = _puzzle.value ?: return
        if (puzzle.isGiven[row][col]) return
        if (_isSolved.value) return

        val grid = _currentGrid.value.copyGrid()
        grid[row][col] = when (grid[row][col]) {
            -1 -> 0
            0 -> 1
            else -> -1
        }
        _currentGrid.value = grid
        _invalidCells.value = PuzzleValidator.getInvalidCells(grid, puzzle.size)

        if (PuzzleValidator.isSolved(grid, puzzle.solution, puzzle.size)) {
            _isSolved.value = true
            stopTimer()
        }
    }

    fun saveCompletedPuzzle() {
        val puzzle = _puzzle.value ?: return
        if (!_isSolved.value) return

        viewModelScope.launch {
            repository.saveProgress(
                UserProgress(
                    puzzleId = puzzle.id,
                    difficulty = puzzle.difficulty.name,
                    completed = true,
                    timeSeconds = _elapsedSeconds.value,
                    completedAtMillis = System.currentTimeMillis()
                )
            )
        }
    }

    private fun startTimer() {
        val startMs = System.currentTimeMillis()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _elapsedSeconds.value = (System.currentTimeMillis() - startMs) / 1000
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }

    private fun Array<IntArray>.copyGrid(): Array<IntArray> =
        Array(size) { r -> IntArray(this[r].size) { c -> this[r][c] } }
}
