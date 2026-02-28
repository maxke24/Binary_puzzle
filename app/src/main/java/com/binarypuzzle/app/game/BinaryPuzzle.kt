package com.binarypuzzle.app.game

data class BinaryPuzzle(
    val id: String,
    val size: Int,
    val difficulty: Difficulty,
    val grid: Array<IntArray>,       // -1 = empty, 0 or 1 = given/placed
    val solution: Array<IntArray>,
    val isGiven: Array<BooleanArray>  // true = pre-filled hint (not editable)
) {
    enum class Difficulty(val gridSize: Int, val displayName: String, val removalRate: Double) {
        EASY(6, "Easy", 0.42),
        MEDIUM(8, "Medium", 0.50),
        HARD(10, "Hard", 0.58)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return id == (other as BinaryPuzzle).id
    }

    override fun hashCode(): Int = id.hashCode()
}
