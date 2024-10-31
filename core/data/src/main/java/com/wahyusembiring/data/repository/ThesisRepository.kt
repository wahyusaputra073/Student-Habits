package com.wahyusembiring.data.repository

import com.wahyusembiring.data.model.entity.Task
import com.wahyusembiring.data.model.entity.Thesis
import com.wahyusembiring.data.model.ThesisWithTask
import kotlinx.coroutines.flow.Flow

interface ThesisRepository {

    fun getAllThesis(): Flow<List<ThesisWithTask>>

    fun getThesisById(id: Int): Flow<ThesisWithTask>

    suspend fun saveNewThesis(thesis: Thesis): Long

    suspend fun updateThesis(thesis: Thesis)

    suspend fun updateThesisTitleById(id: Int, title: String)

    suspend fun deleteThesis(thesis: Thesis)

    suspend fun addNewTask(task: Task): Long

    suspend fun deleteTask(task: Task)

    suspend fun changeTaskCompletedStatus(task: Task, isCompleted: Boolean)
}