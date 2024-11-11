package com.wahyusembiring.data.repository

import com.wahyusembiring.data.Result
import com.wahyusembiring.data.model.entity.Task
import com.wahyusembiring.data.model.entity.Thesis
import com.wahyusembiring.data.model.ThesisWithTask
import kotlinx.coroutines.flow.Flow

interface ThesisRepository {

    fun getAllThesis(): Flow<Result<Flow<List<ThesisWithTask>>>>

    fun getThesisById(id: String): Flow<Result<Flow<ThesisWithTask>>>

    fun saveNewThesis(thesis: Thesis): Flow<Result<String>>

    fun updateThesis(thesis: Thesis): Flow<Result<Unit>>

    fun updateThesisTitleById(id: String, title: String): Flow<Result<Unit>>

    fun deleteThesis(thesis: Thesis): Flow<Result<Unit>>

    fun addNewTask(task: Task): Flow<Result<String>>

    fun deleteTask(task: Task): Flow<Result<Unit>>

    fun changeTaskCompletedStatus(task: Task, isCompleted: Boolean): Flow<Result<Unit>>
}