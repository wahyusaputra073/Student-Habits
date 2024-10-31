package com.wahyusembiring.data.remote

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.wahyusembiring.data.exception.UserIsNotSignInException
import com.wahyusembiring.data.local.Converter
import com.wahyusembiring.data.model.LecturerWithSubject
import com.wahyusembiring.data.model.entity.Lecturer
import com.wahyusembiring.data.remote.util.USER_COLLECTION_ID
import com.wahyusembiring.data.remote.util.toHashMap
import com.wahyusembiring.data.remote.util.toLecturer
import com.wahyusembiring.data.remote.util.toSubject
import com.wahyusembiring.data.repository.AuthRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LecturerService @Inject constructor(
    private val converter: Converter,
    private val authRepository: AuthRepository
) {

    companion object {
        private const val LECTURER_COLLECTION_ID = "lecturer"
    }

    private val db by lazy { Firebase.firestore }

    suspend fun getAllLecturer(): List<Lecturer> {
        val user = authRepository.currentUser.first() ?: throw UserIsNotSignInException()
        val query = db
            .collection(USER_COLLECTION_ID)
            .document(user.id)
            .collection(LECTURER_COLLECTION_ID)
            .get()
            .await()
        return query.documents.map { it.toLecturer(converter) }
    }

    suspend fun getAllLecturerWithSubject(): List<LecturerWithSubject> {
        val user = authRepository.currentUser.first() ?: throw UserIsNotSignInException()
        val query = db
            .collection(USER_COLLECTION_ID)
            .document(user.id)
            .collection(LECTURER_COLLECTION_ID)
            .get()
            .await()
        return query.documents.map {
            val lecturer = it.toLecturer(converter)
            val subjects = db
                .collection(USER_COLLECTION_ID)
                .document(user.id)
                .collection(SubjectService.SUBJECT_COLLECTION_ID)
                .whereEqualTo("lecturer_id", lecturer.id)
                .get()
                .await()
                .documents.map { subject ->
                    subject.toSubject(converter)
                }
            LecturerWithSubject(
                lecturer = lecturer,
                subjects = subjects
            )
        }
    }

    suspend fun getLecturerById(id: Int): Lecturer {
        val user = authRepository.currentUser.first() ?: throw UserIsNotSignInException()
        val document = db
            .collection(USER_COLLECTION_ID)
            .document(user.id)
            .collection(LECTURER_COLLECTION_ID)
            .document(id.toString()).get().await()
        return document.toLecturer(converter)
    }

    suspend fun saveLecturer(lecturer: Lecturer) {
        val user = authRepository.currentUser.first() ?: throw UserIsNotSignInException()
        val newLecturer = lecturer.toHashMap(converter)
        db.collection(USER_COLLECTION_ID)
            .document(user.id)
            .collection(LECTURER_COLLECTION_ID)
            .document(lecturer.id.toString()).set(newLecturer)
            .await()
    }

//    // Fungsi baru untuk menghapus lecturer berdasarkan ID
//    suspend fun deleteLecturerById(id: Int) {
//        val user = authRepository.currentUser.first() ?: throw UserIsNotSignInException()
//        db.collection(USER_COLLECTION_ID)
//            .document(user.id)
//            .collection(LECTURER_COLLECTION_ID)
//            .document(id.toString())
//            .delete()
//            .await()
//    }
}
