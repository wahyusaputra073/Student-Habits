package com.wahyusembiring.data.repository

import com.wahyusembiring.data.model.entity.Reminder
import kotlinx.coroutines.flow.Flow
import com.wahyusembiring.data.Result

interface ReminderRepository {

    fun getReminderById(id: String): Flow<Result<Flow<Reminder?>>>

    fun saveReminder(reminder: Reminder): Flow<Result<String>>

    fun updateReminder(reminder: Reminder): Flow<Result<Unit>>

    fun deleteReminder(reminder: Reminder): Flow<Result<Unit>>

}