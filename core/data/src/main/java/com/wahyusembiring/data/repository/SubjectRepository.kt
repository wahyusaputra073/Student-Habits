package com.wahyusembiring.data.repository

import com.wahyusembiring.data.Result
import com.wahyusembiring.data.model.SubjectWithExam
import com.wahyusembiring.data.model.SubjectWithExamAndHomework
import com.wahyusembiring.data.model.SubjectWithLecturer
import com.wahyusembiring.data.model.entity.Subject
import kotlinx.coroutines.flow.Flow

interface SubjectRepository {

    fun getAllSubject(): Flow<Result<Flow<List<Subject>>>>

    fun getSubjectWithLecturerById(id: String): Flow<Result<Flow<SubjectWithLecturer?>>>

    fun getAllSubjectWithExam(): Flow<Result<Flow<List<SubjectWithExam>>>>

    fun getAllSubjectWithExamAndHomework(): Flow<Result<Flow<List<SubjectWithExamAndHomework>>>>

    fun getAllSubjectWithExamAndHomework(scored: Boolean): Flow<Result<Flow<List<SubjectWithExamAndHomework>>>>

    fun saveSubject(subject: Subject): Flow<Result<Unit>>

    fun updateSubject(subject: Subject): Flow<Result<Unit>>

    fun onDeleteSubject(subject: Subject): Flow<Result<Unit>>

}