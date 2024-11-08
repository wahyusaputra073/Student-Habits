package com.wahyusembiring.data.remote

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.wahyusembiring.data.exception.UserIsNotSignInException
import com.wahyusembiring.data.local.Converter
import com.wahyusembiring.data.model.SubjectWithExam
import com.wahyusembiring.data.model.entity.Subject
import com.wahyusembiring.data.remote.util.USER_COLLECTION_ID
import com.wahyusembiring.data.remote.util.toHashMap
import com.wahyusembiring.data.remote.util.toSubject
import com.wahyusembiring.data.repository.AuthRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SubjectService @Inject constructor(
    private val converter: Converter,
    private val authRepository: AuthRepository
) {

    companion object {
        const val SUBJECT_COLLECTION_ID = "subject"
    }

    private val db by lazy { Firebase.firestore }

    suspend fun getAllSubject(): List<Subject> {
        val user = authRepository.currentUser.first() ?: throw UserIsNotSignInException()
        val querySnapshot = db
            .collection(USER_COLLECTION_ID)
            .document(user.id)
            .collection(SUBJECT_COLLECTION_ID).get().await()
        return querySnapshot.documents.map { it.toSubject(converter) }
    }

    suspend fun getSubjectById(id: String): Subject {
        val user = authRepository.currentUser.first() ?: throw UserIsNotSignInException()
        val document = db
            .collection(USER_COLLECTION_ID)
            .document(user.id)
            .collection(SUBJECT_COLLECTION_ID).document(id).get().await()
        return document.toSubject(converter)
    }

    suspend fun saveSubject(subject: Subject) {
        val user = authRepository.currentUser.first() ?: throw UserIsNotSignInException()
        val newSubject = subject.toHashMap(converter)
        val document = db
            .collection(USER_COLLECTION_ID)
            .document(user.id)
            .collection(SUBJECT_COLLECTION_ID).document(subject.id)
        document
            .set(newSubject)
            .await()
    }

}