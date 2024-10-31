package com.wahyusembiring.data.remote

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.wahyusembiring.data.exception.UserIsNotSignInException
import com.wahyusembiring.data.local.Converter
import com.wahyusembiring.data.model.entity.Reminder
import com.wahyusembiring.data.remote.util.USER_COLLECTION_ID
import com.wahyusembiring.data.remote.util.toHashMap
import com.wahyusembiring.data.remote.util.toReminder
import com.wahyusembiring.data.repository.AuthRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ReminderService @Inject constructor(
    private val converter: Converter,
    private val authRepository: AuthRepository
) {

    companion object {
        private const val REMINDER_COLLECTION_ID = "reminder"
    }

    private val db by lazy { Firebase.firestore }

    suspend fun getAllReminder(): List<Reminder> {
        val user = authRepository.currentUser.first() ?: throw UserIsNotSignInException()
        val querySnapshot = db
            .collection(USER_COLLECTION_ID)
            .document(user.id)
            .collection(REMINDER_COLLECTION_ID).get().await()
        return querySnapshot.documents.map { it.toReminder(converter) }
    }

    suspend fun saveReminder(reminder: Reminder) {
        val newReminder = reminder.toHashMap(converter)
        val user = authRepository.currentUser.first() ?: throw UserIsNotSignInException()
        val document = db
            .collection(USER_COLLECTION_ID)
            .document(user.id)
            .collection(REMINDER_COLLECTION_ID)
            .document(reminder.id.toString())
        document
            .set(newReminder)
            .await()
    }

    suspend fun deleteReminder(reminder: Reminder) {
        val user = authRepository.currentUser.first() ?: throw UserIsNotSignInException()
        val document = db
            .collection(USER_COLLECTION_ID)
            .document(user.id)
            .collection(REMINDER_COLLECTION_ID)
            .document(reminder.id.toString())
        document
            .delete()
            .await()
    }

}