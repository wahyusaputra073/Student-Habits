package com.wahyusembiring.data.repository

import kotlinx.coroutines.flow.Flow

interface EventRepository : HomeworkRepository, ExamRepository, ReminderRepository {

    fun getAllEvent(): Flow<List<Any>>

}