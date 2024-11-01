package com.wahyusembiring.data.repository.implementation

import com.wahyusembiring.data.local.dao.SubjectDao
import com.wahyusembiring.data.model.SubjectWithExam
import com.wahyusembiring.data.model.SubjectWithExamAndHomework
import com.wahyusembiring.data.model.entity.Subject
import com.wahyusembiring.data.remote.SubjectService
import com.wahyusembiring.data.repository.SubjectRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class SubjectRepositoryImpl @Inject constructor(
    private val subjectDao: SubjectDao,
) : SubjectRepository {

    override fun getAllSubject(): Flow<List<Subject>> {
        return subjectDao.getAllSubject()
    }

    override suspend fun saveSubject(subject: Subject) {
        subjectDao.insertSubject(subject)
    }

    override fun getAllSubjectWithExam(): Flow<List<SubjectWithExam>> {
        return subjectDao.getAllSubjectWithExam()
    }

    override fun getAllSubjectWithExamAndHomework(): Flow<List<SubjectWithExamAndHomework>> {
        return subjectDao.getSubjectWithExamAndHomework()
    }

    override fun getAllSubjectWithExamAndHomework(scored: Boolean): Flow<List<SubjectWithExamAndHomework>> {
        return subjectDao.getSubjectWithExamAndHomework(scored)
    }
}