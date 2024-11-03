package com.wahyusembiring.data.repository.implementation

import com.wahyusembiring.data.local.dao.SubjectDao
import com.wahyusembiring.data.model.SubjectWithExam
import com.wahyusembiring.data.model.SubjectWithExamAndHomework
import com.wahyusembiring.data.model.SubjectWithLecturer
import com.wahyusembiring.data.model.entity.Subject
import com.wahyusembiring.data.repository.SubjectRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SubjectRepositoryImpl @Inject constructor(
    private val subjectDao: SubjectDao,
) : SubjectRepository {

    override fun getAllSubject(): Flow<List<Subject>> {
        return subjectDao.getAllSubject()
    }

    override fun getSubjectWithLecturerById(id: Int): Flow<SubjectWithLecturer?> {
        val subjectWithLecturer = subjectDao.getSubjectWithLecturerById(id)
        return subjectWithLecturer
    }

    override suspend fun saveSubject(subject: Subject) {
        subjectDao.insertSubject(subject)
    }

    override suspend fun onDeleteSubject(subject: Subject) {
        subjectDao.deleteSubjectById(subject.id) // Pastikan untuk menghapus dosen dari database
    }

    override suspend fun updateSubject(subject: Subject) {
        subjectDao.updateSubject(subject)
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