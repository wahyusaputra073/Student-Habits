package com.wahyusembiring.data.remote

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.wahyusembiring.data.exception.UserIsNotSignInException
import com.wahyusembiring.data.local.Converter
import com.wahyusembiring.data.model.ThesisWithTask
import com.wahyusembiring.data.model.entity.Task
import com.wahyusembiring.data.model.entity.Thesis
import com.wahyusembiring.data.remote.util.USER_COLLECTION_ID
import com.wahyusembiring.data.remote.util.toHashMap
import com.wahyusembiring.data.remote.util.toThesisWithTask
import com.wahyusembiring.data.repository.AuthRepository
import com.wahyusembiring.data.repository.ThesisRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ThesisService @Inject constructor(
    private val converter: Converter,
    private val authRepository: AuthRepository
) {

    companion object {
        private const val THESIS_COLLECTION_ID = "thesis"
    }

    private val db by lazy { Firebase.firestore }

    suspend fun getAllThesisWithTask(): List<ThesisWithTask> {
        val user = authRepository.currentUser.first() ?: throw UserIsNotSignInException()
        val query = db
            .collection(USER_COLLECTION_ID)
            .document(user.id)
            .collection(THESIS_COLLECTION_ID)
            .get()
            .await()
        return query.documents.map { it.toThesisWithTask(converter) }
    }

    suspend fun getThesisById(id: String): ThesisWithTask {
        val user = authRepository.currentUser.first() ?: throw UserIsNotSignInException()
        val query = db
            .collection(USER_COLLECTION_ID)
            .document(user.id)
            .collection(THESIS_COLLECTION_ID)
            .document(id).get().await()

        return query.toThesisWithTask(converter)
    }

    suspend fun saveNewThesis(thesis: Thesis) {
        val user = authRepository.currentUser.first() ?: throw UserIsNotSignInException()
        val thesisWithTask = ThesisWithTask(thesis, emptyList())
        db.collection(USER_COLLECTION_ID)
            .document(user.id)
            .collection(THESIS_COLLECTION_ID).document(thesis.id)
            .set(thesisWithTask.toHashMap(converter))
            .await()
    }

    suspend fun saveThesisWithTask(thesisWithTask: ThesisWithTask) {
        val user = authRepository.currentUser.first() ?: throw UserIsNotSignInException()
        db.collection(USER_COLLECTION_ID)
            .document(user.id)
            .collection(THESIS_COLLECTION_ID).document(thesisWithTask.thesis.id)
            .set(thesisWithTask.toHashMap(converter))
            .await()
    }

    suspend fun updateThesis(thesis: Thesis) {
        val user = authRepository.currentUser.first() ?: throw UserIsNotSignInException()
        db.collection(USER_COLLECTION_ID)
            .document(user.id)
            .collection(THESIS_COLLECTION_ID).document(thesis.id)
            .update(thesis.toHashMap(converter))
            .await()
    }

    suspend fun updateThesisTitleById(id: String, title: String) {
        val user = authRepository.currentUser.first() ?: throw UserIsNotSignInException()
        db.collection(USER_COLLECTION_ID)
            .document(user.id)
            .collection(THESIS_COLLECTION_ID).document(id)
            .update("title", title)
            .await()
    }

    suspend fun deleteThesis(thesis: Thesis) {
        val user = authRepository.currentUser.first() ?: throw UserIsNotSignInException()
        db.collection(USER_COLLECTION_ID)
            .document(user.id)
            .collection(THESIS_COLLECTION_ID).document(thesis.id)
            .delete()
            .await()
    }

    suspend fun addNewTask(task: Task) {
        val user = authRepository.currentUser.first() ?: throw UserIsNotSignInException()
        db.collection(USER_COLLECTION_ID)
            .document(user.id)
            .collection(THESIS_COLLECTION_ID)
            .document(task.thesisId)
            .update(
                "tasks", FieldValue.arrayUnion(
                    task.toHashMap(converter).toMap()
                )
            )
            .await()
    }

    suspend fun deleteTask(task: Task) {
        val user = authRepository.currentUser.first() ?: throw UserIsNotSignInException()
        db.collection(USER_COLLECTION_ID)
            .document(user.id)
            .collection(THESIS_COLLECTION_ID)
            .document(task.thesisId)
            .update("tasks", FieldValue.arrayRemove(task.toHashMap(converter).toMap()))
            .await()
    }

    suspend fun changeTaskCompletedStatus(task: Task, isCompleted: Boolean) {
        val user = authRepository.currentUser.first() ?: throw UserIsNotSignInException()
        val test = db.runTransaction { transaction ->
            val docRef = db.collection(USER_COLLECTION_ID)
                .document(user.id)
                .collection(THESIS_COLLECTION_ID)
                .document(task.thesisId)
            transaction.update(
                docRef,
                "tasks",
                FieldValue.arrayRemove(task.toHashMap(converter).toMap())
            )
            transaction.update(
                docRef,
                "tasks",
                FieldValue.arrayUnion(
                    task.copy(isCompleted = isCompleted).toHashMap(converter).toMap()
                )
            )
            null
        }.await()
    }

}