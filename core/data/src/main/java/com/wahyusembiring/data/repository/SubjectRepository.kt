package com.wahyusembiring.data.repository

import com.wahyusembiring.data.model.SubjectWithExam
import com.wahyusembiring.data.model.SubjectWithExamAndHomework
import com.wahyusembiring.data.model.SubjectWithLecturer
import com.wahyusembiring.data.model.entity.Subject
import kotlinx.coroutines.flow.Flow

interface SubjectRepository {

    fun getAllSubject(): Flow<List<Subject>>

    fun getSubjectWithLecturerById(id: Int): Flow<SubjectWithLecturer?>

    fun getAllSubjectWithExam(): Flow<List<SubjectWithExam>>

    fun getAllSubjectWithExamAndHomework(): Flow<List<SubjectWithExamAndHomework>>

    fun getAllSubjectWithExamAndHomework(scored: Boolean): Flow<List<SubjectWithExamAndHomework>>

    suspend fun saveSubject(subject: Subject)

    suspend fun updateSubject(subject: Subject)

    suspend fun onDeleteSubject(id: Subject)

}