package com.wahyusembiring.data.repository

import com.wahyusembiring.data.model.ExamWithSubject
import com.wahyusembiring.data.model.entity.Exam
import kotlinx.coroutines.flow.Flow

interface ExamRepository {
    fun getAllExamWithSubject(): Flow<List<ExamWithSubject>>
    fun getExamById(id: Int): Flow<ExamWithSubject?>
    suspend fun saveExam(exam: Exam): Long
    suspend fun updateExam(exam: Exam)
    suspend fun deleteExam(exam: Exam)
}