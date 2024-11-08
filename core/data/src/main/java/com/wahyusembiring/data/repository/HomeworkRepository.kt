package com.wahyusembiring.data.repository

import com.wahyusembiring.data.Result
import com.wahyusembiring.data.model.HomeworkWithSubject
import com.wahyusembiring.data.model.entity.Homework
import kotlinx.coroutines.flow.Flow

interface HomeworkRepository {

    fun getHomeworkById(id: String): Flow<Result<Flow<HomeworkWithSubject?>>>

    fun saveHomework(homework: Homework): Flow<Result<String>>

    fun updateHomework(homework: Homework): Flow<Result<Unit>>

    fun deleteHomework(homework: Homework): Flow<Result<Unit>>

}