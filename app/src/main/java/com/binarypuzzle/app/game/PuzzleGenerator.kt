package com.binarypuzzle.app.game

import java.util.UUID

class PuzzleGenerator {

    private val random = java.util.Random()

    fun generate(difficulty: BinaryPuzzle.Difficulty): BinaryPuzzle {
        val size = difficulty.gridSize
        val solution = generateSolution(size)
        val (grid, isGiven) = createPuzzle(solution, difficulty)

        return BinaryPuzzle(
            id = UUID.randomUUID().toString(),
            size = size,
            difficulty = difficulty,
            grid = grid,
            solution = solution,
            isGiven = isGiven
        )
    }

    private fun generateSolution(size: Int): Array<IntArray> {
        val grid = Array(size) { IntArray(size) { -1 } }
        if (solve(grid, size, 0, 0)) {
            return grid
        }
        // Fallback: alternating pattern (should rarely be reached)
        return Array(size) { row -> IntArray(size) { col -> (row + col) % 2 } }
    }

    private fun solve(grid: Array<IntArray>, size: Int, row: Int, col: Int): Boolean {
        if (row == size) return true

        val nextRow = if (col == size - 1) row + 1 else row
        val nextCol = if (col == size - 1) 0 else col + 1

        val values = if (random.nextBoolean()) intArrayOf(0, 1) else intArrayOf(1, 0)

        for (value in values) {
            if (isValidPlacement(grid, size, row, col, value)) {
                grid[row][col] = value
                if (solve(grid, size, nextRow, nextCol)) return true
                grid[row][col] = -1
            }
        }
        return false
    }

    private fun isValidPlacement(
        grid: Array<IntArray>, size: Int, row: Int, col: Int, value: Int
    ): Boolean {
        // No three consecutive same values in this row
        if (col >= 2 && grid[row][col - 1] == value && grid[row][col - 2] == value) return false
        // No three consecutive same values in this column
        if (row >= 2 && grid[row - 1][col] == value && grid[row - 2][col] == value) return false

        // Row balance: count of this value must not exceed half the row length
        val rowCount = (0 until col).count { grid[row][it] == value }
        if (rowCount >= size / 2) return false

        // Column balance
        val colCount = (0 until row).count { grid[it][col] == value }
        if (colCount >= size / 2) return false

        // Row uniqueness: check when completing a row
        if (col == size - 1) {
            val newRow = IntArray(size)
            for (i in 0 until col) newRow[i] = grid[row][i]
            newRow[col] = value
            for (r in 0 until row) {
                if (grid[r].contentEquals(newRow)) return false
            }
        }

        // Column uniqueness: check when completing a column
        if (row == size - 1) {
            val newCol = IntArray(size)
            for (i in 0 until row) newCol[i] = grid[i][col]
            newCol[row] = value
            for (c in 0 until col) {
                val existingCol = IntArray(size) { grid[it][c] }
                if (existingCol.contentEquals(newCol)) return false
            }
        }

        return true
    }

    private fun createPuzzle(
        solution: Array<IntArray>,
        difficulty: BinaryPuzzle.Difficulty
    ): Pair<Array<IntArray>, Array<BooleanArray>> {
        val size = solution.size
        val grid = Array(size) { r -> IntArray(size) { c -> solution[r][c] } }
        val isGiven = Array(size) { BooleanArray(size) { true } }

        val toRemove = (size * size * difficulty.removalRate).toInt()

        val cells = (0 until size)
            .flatMap { r -> (0 until size).map { c -> r to c } }
            .shuffled(random)
            .take(toRemove)

        for ((r, c) in cells) {
            grid[r][c] = -1
            isGiven[r][c] = false
        }

        return grid to isGiven
    }
}
