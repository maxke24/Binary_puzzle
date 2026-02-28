package com.binarypuzzle.app.data.model

/**
 * Represents a user's progress entry for a single puzzle attempt.
 * Stored in Firestore under the "progress" collection.
 * All fields have defaults to support Firestore deserialization.
 */
data class UserProgress(
    val id: String = "",
    val userId: String = "",
    val userDisplayName: String = "",
    val puzzleId: String = "",
    val difficulty: String = "",
    val completed: Boolean = false,
    val timeSeconds: Long = 0L,
    val completedAtMillis: Long = 0L
)
