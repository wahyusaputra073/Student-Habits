package com.wahyusembiring.data.remote

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.wahyusembiring.data.exception.UserIsNotSignInException
import com.wahyusembiring.data.local.Converter
import com.wahyusembiring.data.model.ExamWithSubject
import com.wahyusembiring.data.model.entity.Exam
import com.wahyusembiring.data.remote.util.USER_COLLECTION_ID
import com.wahyusembiring.data.remote.util.toExam
import com.wahyusembiring.data.remote.util.toHashMap
import com.wahyusembiring.data.repository.AuthRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ExamService @Inject constructor(
    private val converter: Converter,
    private val subjectService: SubjectService,
    private val authRepository: AuthRepository
) {

    companion object {
        private const val EXAM_COLLECTION_ID = "exam"
    }

    private val db by lazy { Firebase.firestore }

    suspend fun getAllExam(): List<Exam> {
        val user = authRepository.currentUser.first() ?: throw UserIsNotSignInException()
        val querySnapshot = db
            .collection(USER_COLLECTION_ID)
            .document(user.id)
            .collection(EXAM_COLLECTION_ID).get().await()
        return querySnapshot.documents.map { it.toExam(converter) }
    }

    suspend fun getAllExamWithSubject(): List<ExamWithSubject> {
        val exam = getAllExam()
        return exam.map {
            ExamWithSubject(
                exam = it,
                subject = subjectService.getSubjectById(it.subjectId)
            )
        }
    }

    suspend fun saveExam(exam: Exam) {
        val newExam = exam.toHashMap(converter)
        val user = authRepository.currentUser.first() ?: throw UserIsNotSignInException()
        val document = db
            .collection(USER_COLLECTION_ID)
            .document(user.id)
            .collection(EXAM_COLLECTION_ID)
            .document(exam.id.toString())
        document
            .set(newExam)
            .await()
    }

    suspend fun deleteExam(exam: Exam) {
        val user = authRepository.currentUser.first() ?: throw UserIsNotSignInException()
        val document = db
            .collection(USER_COLLECTION_ID)
            .document(user.id)
            .collection(EXAM_COLLECTION_ID).document(exam.id.toString())
        document
            .delete()
            .await()
    }

    suspend fun getAllExamBySubjectId(subjectId: Int): List<Exam> {
        val user = authRepository.currentUser.first() ?: throw UserIsNotSignInException()
        val querySnapshot = db
            .collection(USER_COLLECTION_ID)
            .document(user.id)
            .collection(EXAM_COLLECTION_ID)
            .whereEqualTo("subject_id", subjectId)
            .get()
            .await()
        return querySnapshot.documents.map { it.toExam(converter) }
    }

}