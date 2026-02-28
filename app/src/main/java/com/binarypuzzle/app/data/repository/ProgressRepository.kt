package com.binarypuzzle.app.data.repository

import com.binarypuzzle.app.data.model.UserProgress
import java.util.UUID

/**
 * In-memory progress repository for testing (no Firebase required).
 * Data is held for the app session and cleared on restart.
 */
class ProgressRepository {

    private val records = mutableListOf<UserProgress>()

    fun saveProgress(progress: UserProgress) {
        records.add(progress.copy(id = UUID.randomUUID().toString()))
    }

    fun getUserProgress(): List<UserProgress> =
        records.sortedByDescending { it.completedAtMillis }

    fun getUserStats(): Map<String, Int> = records
        .filter { it.completed }
        .groupBy { it.difficulty }
        .mapValues { (_, v) -> v.size }

    fun getBestTime(difficulty: String): Long? = records
        .filter { it.completed && it.difficulty == difficulty }
        .minOfOrNull { it.timeSeconds }
}
