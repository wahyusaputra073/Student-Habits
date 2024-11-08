package com.wahyusembiring.data.repository

import com.wahyusembiring.data.Result
import kotlinx.coroutines.flow.Flow

interface EventRepository : HomeworkRepository, ExamRepository, ReminderRepository {

    fun getAllEvent(): Flow<Result<Flow<List<Any>>>>

}