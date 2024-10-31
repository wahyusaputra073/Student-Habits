package com.wahyusembiring.data.repository

import com.wahyusembiring.data.model.entity.Reminder
import kotlinx.coroutines.flow.Flow

interface ReminderRepository {

    fun getAllReminder(
        minDate: Long? = null,
        maxDate: Long? = null
    ): Flow<List<Reminder>>

    fun getReminderById(id: Int): Flow<Reminder?>

    suspend fun saveReminder(reminder: Reminder): Long

    suspend fun updateReminder(reminder: Reminder)

    suspend fun deleteReminder(reminder: Reminder)

}