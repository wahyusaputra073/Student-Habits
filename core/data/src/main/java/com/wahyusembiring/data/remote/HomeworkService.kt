package com.wahyusembiring.data.remote

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.wahyusembiring.data.exception.UserIsNotSignInException
import com.wahyusembiring.data.local.Converter
import com.wahyusembiring.data.local.dao.HomeworkDao
import com.wahyusembiring.data.model.HomeworkWithSubject
import com.wahyusembiring.data.model.entity.Homework
import com.wahyusembiring.data.remote.util.USER_COLLECTION_ID
import com.wahyusembiring.data.remote.util.toHashMap
import com.wahyusembiring.data.remote.util.toHomework
import com.wahyusembiring.data.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class HomeworkService @Inject constructor(
    private val converter: Converter,
    private val subjectService: SubjectService,
    private val authRepository: AuthRepository
) {

    companion object {
        const val HOMEWORK_COLLECTION_ID = "homework"
    }

    private val db by lazy { Firebase.firestore }


    suspend fun getAllHomework(): List<Homework> {
        val user = authRepository.currentUser.first() ?: throw UserIsNotSignInException()
        val querySnapshot = db
            .collection(USER_COLLECTION_ID)
            .document(user.id)
            .collection(HOMEWORK_COLLECTION_ID).get().await()
        return querySnapshot.documents.map { it.toHomework(converter) }
    }

    suspend fun getAllHomeworkWithSubject(): List<HomeworkWithSubject> {
        val homework = getAllHomework()
        return homework.map {
            HomeworkWithSubject(
                homework = it,
                subject = subjectService.getSubjectById(it.subjectId)
            )
        }
    }

    suspend fun getHomeworkWithSubjectById(id: String): HomeworkWithSubject {
        val user = authRepository.currentUser.first() ?: throw UserIsNotSignInException()
        val document = db
            .collection(USER_COLLECTION_ID)
            .document(user.id)
            .collection(HOMEWORK_COLLECTION_ID).document(id).get().await()
        val homework = document.toHomework(converter)
        val subject = subjectService.getSubjectById(homework.subjectId)
        return HomeworkWithSubject(homework, subject)
    }

    suspend fun saveHomework(homework: Homework) {
        val user = authRepository.currentUser.first() ?: throw UserIsNotSignInException()
        val newHomework = homework.toHashMap(converter)
        val document = db
            .collection(USER_COLLECTION_ID)
            .document(user.id)
            .collection(HOMEWORK_COLLECTION_ID).document(homework.id)
        document
            .set(newHomework)
            .await()
    }

    suspend fun deleteHomework(homework: Homework) {
        val user = authRepository.currentUser.first() ?: throw UserIsNotSignInException()
        val document = db
            .collection(USER_COLLECTION_ID)
            .document(user.id)
            .collection(HOMEWORK_COLLECTION_ID).document(homework.id)
        document
            .delete()
            .await()
    }

    suspend fun updateCompletedStatus(homeworkId: String, isCompleted: Boolean) {
        val user = authRepository.currentUser.first() ?: throw UserIsNotSignInException()
        val document = db
            .collection(USER_COLLECTION_ID)
            .document(user.id)
            .collection(HOMEWORK_COLLECTION_ID).document(homeworkId)

        document
            .update("completed", isCompleted)
            .await()
    }

}