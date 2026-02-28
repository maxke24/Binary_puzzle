package com.binarypuzzle.app.data.repository

import com.binarypuzzle.app.data.model.UserProgress
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProgressRepository {

    private val db = FirebaseFirestore.getInstance()
    private val progressCollection = db.collection("progress")

    suspend fun saveProgress(progress: UserProgress) {
        val docRef = if (progress.id.isEmpty()) {
            progressCollection.document()
        } else {
            progressCollection.document(progress.id)
        }
        val toSave = progress.copy(id = docRef.id)
        docRef.set(toSave).await()
    }

    suspend fun getUserProgress(userId: String): List<UserProgress> {
        val snapshot = progressCollection
            .whereEqualTo("userId", userId)
            .get()
            .await()

        return snapshot.documents
            .mapNotNull { doc -> doc.toObject(UserProgress::class.java)?.copy(id = doc.id) }
            .sortedByDescending { it.completedAtMillis }
    }

    suspend fun getUserStats(userId: String): Map<String, Int> {
        val snapshot = progressCollection
            .whereEqualTo("userId", userId)
            .whereEqualTo("completed", true)
            .get()
            .await()

        return snapshot.documents
            .mapNotNull { doc -> doc.toObject(UserProgress::class.java) }
            .groupBy { it.difficulty }
            .mapValues { (_, entries) -> entries.size }
    }

    suspend fun getBestTime(userId: String, difficulty: String): Long? {
        val snapshot = progressCollection
            .whereEqualTo("userId", userId)
            .whereEqualTo("difficulty", difficulty)
            .whereEqualTo("completed", true)
            .get()
            .await()

        return snapshot.documents
            .mapNotNull { doc -> doc.toObject(UserProgress::class.java) }
            .minOfOrNull { it.timeSeconds }
    }
}
