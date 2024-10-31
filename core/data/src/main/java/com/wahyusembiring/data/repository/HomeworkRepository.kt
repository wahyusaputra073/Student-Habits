package com.wahyusembiring.data.repository

import com.wahyusembiring.data.model.HomeworkWithSubject
import com.wahyusembiring.data.model.entity.Homework
import kotlinx.coroutines.flow.Flow

interface HomeworkRepository {

    fun getAllHomeworkWithSubject(
        minDate: Long? = null,
        maxDate: Long? = null
    ): Flow<List<HomeworkWithSubject>>

    fun getHomeworkById(id: Int): Flow<HomeworkWithSubject?>

    suspend fun saveHomework(homework: Homework): Long

    suspend fun updateHomework(homework: Homework)

    suspend fun deleteHomework(homework: Homework)
}