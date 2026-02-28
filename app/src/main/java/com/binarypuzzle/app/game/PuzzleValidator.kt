package com.binarypuzzle.app.game

object PuzzleValidator {

    /**
     * Returns the set of cell coordinates that violate puzzle rules.
     * Only validates filled cells — empty (-1) cells are ignored.
     */
    fun getInvalidCells(grid: Array<IntArray>, size: Int): Set<Pair<Int, Int>> {
        val invalid = mutableSetOf<Pair<Int, Int>>()

        for (r in 0 until size) {
            for (c in 0 until size) {
                if (grid[r][c] == -1) continue

                // Three consecutive in row
                if (c >= 2 && grid[r][c - 1] == grid[r][c] && grid[r][c - 2] == grid[r][c]) {
                    invalid += Pair(r, c)
                    invalid += Pair(r, c - 1)
                    invalid += Pair(r, c - 2)
                }
                // Three consecutive in column
                if (r >= 2 && grid[r - 1][c] == grid[r][c] && grid[r - 2][c] == grid[r][c]) {
                    invalid += Pair(r, c)
                    invalid += Pair(r - 1, c)
                    invalid += Pair(r - 2, c)
                }
            }
        }

        // Row balance violations
        for (r in 0 until size) {
            val zeros = grid[r].count { it == 0 }
            val ones = grid[r].count { it == 1 }
            if (zeros > size / 2 || ones > size / 2) {
                for (c in 0 until size) {
                    if (grid[r][c] != -1) invalid += Pair(r, c)
                }
            }
        }

        // Column balance violations
        for (c in 0 until size) {
            val zeros = (0 until size).count { grid[it][c] == 0 }
            val ones = (0 until size).count { grid[it][c] == 1 }
            if (zeros > size / 2 || ones > size / 2) {
                for (r in 0 until size) {
                    if (grid[r][c] != -1) invalid += Pair(r, c)
                }
            }
        }

        // Duplicate row violations (only for complete rows)
        val completeRows = (0 until size).filter { r -> grid[r].none { it == -1 } }
        for (i in completeRows.indices) {
            for (j in i + 1 until completeRows.size) {
                val r1 = completeRows[i]
                val r2 = completeRows[j]
                if (grid[r1].contentEquals(grid[r2])) {
                    for (c in 0 until size) {
                        invalid += Pair(r1, c)
                        invalid += Pair(r2, c)
                    }
                }
            }
        }

        // Duplicate column violations (only for complete columns)
        val completeCols = (0 until size).filter { c ->
            (0 until size).none { grid[it][c] == -1 }
        }
        for (i in completeCols.indices) {
            for (j in i + 1 until completeCols.size) {
                val c1 = completeCols[i]
                val c2 = completeCols[j]
                val col1 = IntArray(size) { grid[it][c1] }
                val col2 = IntArray(size) { grid[it][c2] }
                if (col1.contentEquals(col2)) {
                    for (r in 0 until size) {
                        invalid += Pair(r, c1)
                        invalid += Pair(r, c2)
                    }
                }
            }
        }

        return invalid
    }

    fun isSolved(grid: Array<IntArray>, solution: Array<IntArray>, size: Int): Boolean {
        for (r in 0 until size) {
            for (c in 0 until size) {
                if (grid[r][c] != solution[r][c]) return false
            }
        }
        return true
    }
}
