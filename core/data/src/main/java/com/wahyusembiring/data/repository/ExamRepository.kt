package com.wahyusembiring.data.repository

import com.wahyusembiring.data.Result
import com.wahyusembiring.data.model.ExamWithSubject
import com.wahyusembiring.data.model.entity.Exam
import kotlinx.coroutines.flow.Flow

interface ExamRepository {
    fun getAllExamWithSubject(): Flow<Result<Flow<List<ExamWithSubject>>>>
    fun getExamById(id: String): Flow<Result<Flow<ExamWithSubject?>>>
    fun saveExam(exam: Exam): Flow<Result<String>>
    fun updateExam(exam: Exam): Flow<Result<Unit>>
    fun deleteExam(exam: Exam): Flow<Result<Unit>>
}